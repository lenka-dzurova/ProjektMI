package back_end.audio_video.service;

import back_end.audio_video.details.Rola;
import back_end.audio_video.details.StavObjednavky;
import back_end.audio_video.details.StavProduktu;
import back_end.audio_video.entity.Objednavka;
import back_end.audio_video.entity.Produkt;
import back_end.audio_video.repository.ObjednavkaProduktRepository;
import back_end.audio_video.repository.ObjednavkaRepository;
import back_end.audio_video.repository.ProduktRepository;
import back_end.audio_video.request.RolaRequest;
import back_end.audio_video.request.VyhladavanieProduktuRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class ProduktService {

    @Autowired
    private ProduktRepository produktRepository;

    @Autowired
    private ObjednavkaRepository objednavkaRepository;
    @Autowired
    private ObjednavkaProduktRepository objednavkaProduktRepository;

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
            if (request.getTypTechniky() != null) {
                return produktRepository.findAllByTypTechniky(request.getTypTechniky());
            } else {
                return produktRepository.findAll();
            }

        }else if(request.getRolaProduktu() == Rola.UCITEL) {
            if (request.getTypTechniky() != null) {
                return produktRepository.findAllByStavProduktuAndTypTechniky(StavProduktu.FUNKCNE, request.getTypTechniky());
            } else {
                return produktRepository.findAllByStavProduktu(StavProduktu.FUNKCNE);
            }
        } else {
            if (request.getTypTechniky() != null) {
                return produktRepository.findAllByRolaProduktuAndStavProduktuAndTypTechniky(request.getRolaProduktu(), StavProduktu.FUNKCNE, request.getTypTechniky());
            } else {
                return produktRepository.findAllByRolaProduktuAndStavProduktu(request.getRolaProduktu(), StavProduktu.FUNKCNE);
            }

        }
    }

    public List<Produkt> vratVsetkyProduktyPodlaVyhladavania(VyhladavanieProduktuRequest request) {

        if (request.getDatumVratenia() != null && request.getDatumVypozicania() != null) {

            if (request.getNazov() != null && !request.getNazov().isEmpty()) {
                if (request.getTypTechniky() != null) { // Kontrola, či je zadaný typ techniky
                    if (request.getRola() == Rola.UCITEL) {

                        return produktRepository.findProduktyByVolneDatumNazovTypAndFilters(request.getDatumVypozicania(), request.getDatumVratenia(), request.getNazov(), request.getTypTechniky(), StavProduktu.FUNKCNE);
                    } else {
                        return produktRepository.findProduktyByVolneDatumNazovTypAndFiltersByRola(request.getDatumVypozicania(), request.getDatumVratenia(), request.getNazov(), request.getTypTechniky(), request.getRola(), StavProduktu.FUNKCNE);
                    }
                } else {
                    // Ak nie je zadaný typ techniky, tak použijeme existujúci filter
                    if (request.getRola() == Rola.UCITEL) {
                        return produktRepository.findProduktyByVolneDatumNazovAndFilters(request.getDatumVypozicania(), request.getDatumVratenia(), request.getNazov(), StavProduktu.FUNKCNE);
                    } else {
                        return produktRepository.findProduktyByVolneDatumNazovAndFiltersByRola(request.getDatumVypozicania(), request.getDatumVratenia(), request.getNazov(), request.getRola(), StavProduktu.FUNKCNE);
                    }
                }
            } else {
                // Ak nie je zadaný názov produktu
                if (request.getTypTechniky() != null) { // Kontrola, či je zadaný typ techniky
                    if (request.getRola() == Rola.UCITEL) {
                        return produktRepository.findVolneProduktyByTypTechniky(request.getDatumVypozicania(), request.getDatumVratenia(), request.getTypTechniky(), StavProduktu.FUNKCNE);
                    } else {
                        return produktRepository.findVolneProduktyByTypTechnikyAndRola(request.getDatumVypozicania(), request.getDatumVratenia(), request.getTypTechniky(), request.getRola(), StavProduktu.FUNKCNE);
                    }
                } else {
                    if (request.getRola() == Rola.UCITEL) {
                        return produktRepository.findVolneProdukty(request.getDatumVypozicania(), request.getDatumVratenia(), StavProduktu.FUNKCNE);
                    } else {
                        return produktRepository.findVolneProduktyByRola(request.getDatumVypozicania(), request.getDatumVratenia(), request.getRola(), StavProduktu.FUNKCNE);
                    }
                }
            }
        } else {
            // Ak nie sú zadané dátumy
            if (request.getRola() == Rola.ADMIN) {
                if (request.getTypTechniky() != null) {
                    return produktRepository.findAllByNazovContainingIgnoreCaseAndTypTechniky(request.getNazov(), request.getTypTechniky());
                } else {
                    return produktRepository.findAllByNazovContainingIgnoreCase(request.getNazov());
                }
            } else if (request.getRola() == Rola.UCITEL) {
                if (request.getTypTechniky() != null) {
                    return produktRepository.findAllByStavProduktuAndNazovContainingIgnoreCaseAndTypTechniky(StavProduktu.FUNKCNE, request.getNazov(), request.getTypTechniky());
                } else {
                    return produktRepository.findAllByStavProduktuAndNazovContainingIgnoreCase(StavProduktu.FUNKCNE, request.getNazov());
                }
            } else {
                if (request.getTypTechniky() != null) {
                    return produktRepository.findAllByRolaProduktuAndStavProduktuAndNazovContainingIgnoreCaseAndTypTechniky(request.getRola(), StavProduktu.FUNKCNE, request.getNazov(), request.getTypTechniky());
                } else {
                    return produktRepository.findAllByRolaProduktuAndStavProduktuAndNazovContainingIgnoreCase(request.getRola(), StavProduktu.FUNKCNE, request.getNazov());
                }
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


    //TODO DOKONCI ABY SA ODSTRANILI AJ PRAZDNE OBJEDNAVKY
    @Transactional
    public int odstranProduktyPodlaID(List<String> zoznamIDProduktov) {
        objednavkaProduktRepository.deleteByProdukt_IdProduktInAndObjednavka_StavObjednavkyIn(
                zoznamIDProduktov,
                Arrays.asList(StavObjednavky.ZAMIETNUTA, StavObjednavky.VRATENA)
        );

        List<Objednavka> prazdneObjednavky = objednavkaRepository.findAllByObjednavkaProduktyIsEmpty();

        for (Objednavka objednavka : prazdneObjednavky) {
            System.out.println(objednavka.getObjednavkaProdukty().size());
        }

        produktRepository.deleteProduktsByIdProduktIn(zoznamIDProduktov);

        return 0;
    }
}
