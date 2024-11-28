package back_end.audio_video.service;

import back_end.audio_video.details.Rola;
import back_end.audio_video.details.StavProduktu;
import back_end.audio_video.entity.Produkt;
import back_end.audio_video.repository.ObjednavkaRepository;
import back_end.audio_video.repository.ProduktRepository;
import back_end.audio_video.request.RolaRequest;
import back_end.audio_video.request.VyhladavanieProduktuRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProduktService {

    @Autowired
    private ProduktRepository produktRepository;

    @Autowired
    private ObjednavkaRepository objednavkaRepository;

    public Produkt pridajProdukt(Produkt produkt) {
        return produktRepository.save(produkt);
    }

    public Boolean obsahujeProdukt(String id) {
        return produktRepository.existsById(id);
    }

    public Optional<Produkt> vratProdukt(String id) {
        return produktRepository.getProduktByIdProdukt(id);
    }

    public List<Produkt> vratVsetkyProdukty(RolaRequest request) {
        if (request.getRolaProduktu() == Rola.ADMIN) {
            return produktRepository.findAllByTypTechniky(request.getTypTechniky());
        }else if(request.getRolaProduktu() == Rola.UCITEL) {
            return produktRepository.findAllByStavProduktuAndTypTechniky(StavProduktu.FUNKCNE, request.getTypTechniky());
        } else {
            return produktRepository.findAllByRolaProduktuAndStavProduktuAndTypTechniky(request.getRolaProduktu(), StavProduktu.FUNKCNE, request.getTypTechniky());
        }
    }

    public List<Produkt> vratVsetkyProduktyPodlaVyhladavania(VyhladavanieProduktuRequest request) {

        if (request.getDatumVratenia() != null && request.getDatumVypozicania() != null) {
            if (request.getNazov() != null && !request.getNazov().isEmpty()) {
                if (request.getRola() == Rola.UCITEL) {
                    return produktRepository.findProduktyByVolneDatumNazovAndFilters(request.getDatumVypozicania(),request.getDatumVratenia(),request.getNazov(),StavProduktu.FUNKCNE);
                } else {
                    return produktRepository.findProduktyByVolneDatumNazovAndFiltersByRola(request.getDatumVypozicania(), request.getDatumVratenia(), request.getNazov(), request.getRola(), StavProduktu.FUNKCNE);
                }
            } else {
                if (request.getRola() == Rola.UCITEL) {
                    return produktRepository.findVolneProdukty(request.getDatumVypozicania(),request.getDatumVratenia(),StavProduktu.FUNKCNE);
                } else {
                    return produktRepository.findVolneProduktyByRola(request.getDatumVypozicania(), request.getDatumVratenia(), request.getRola(), StavProduktu.FUNKCNE);
                }
            }
        } else {
            if (request.getRola() == Rola.ADMIN ) {
                return produktRepository.findAllByNazovContainingIgnoreCase(request.getNazov());
            } else if (request.getRola() == Rola.UCITEL) {

                return produktRepository.findAllByStavProduktuAndNazovContainingIgnoreCase(StavProduktu.FUNKCNE, request.getNazov());
            } else {
                return produktRepository.findAllByRolaProduktuAndStavProduktuAndNazovContainingIgnoreCase(request.getRola(),StavProduktu.FUNKCNE, request.getNazov());
            }
        }
    }


    public ResponseEntity<?> odstranProdukt(String id) {
        if (produktRepository.existsByIdProdukt(id)) {
            produktRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body("Produkt bol odstraneny");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produkt s danym ID neexistuje");
        }
    }

    @Transactional
    public Produkt aktualizujProdukt(String id, Produkt novyProdukt) {
        Optional<Produkt> existujuciProduktOptional = produktRepository.getProduktByIdProdukt(id);

        System.out.println(novyProdukt.getTypTechniky());

        if (existujuciProduktOptional.isPresent()) {
            Produkt existujuciProdukt = existujuciProduktOptional.get();

            existujuciProdukt.setNazov(novyProdukt.getNazov());
            existujuciProdukt.setPopis(novyProdukt.getPopis());
            existujuciProdukt.setTypTechniky(novyProdukt.getTypTechniky());
            existujuciProdukt.setStavProduktu(novyProdukt.getStavProduktu());
            existujuciProdukt.setRolaProduktu(novyProdukt.getRolaProduktu());

            if (novyProdukt.getObrazok() != null) {
                existujuciProdukt.setObrazok(novyProdukt.getObrazok());
            }

            return produktRepository.save(existujuciProdukt);
        } else {
            throw new RuntimeException("Produkt neexistuje");
        }
    }

    @Transactional
    public int odstraProduktyPodlaID(List<String> zoznamIDProduktov) {
        return produktRepository.deleteProduktsByIdProduktIn(zoznamIDProduktov);
    }
}
