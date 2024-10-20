package back_end.audio_video.service;

import back_end.audio_video.entity.Produkt;
import back_end.audio_video.repository.ProduktRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ProduktService {

    @Autowired
    private ProduktRepository produktRepository;

    public Produkt pridajProdukt(Produkt produkt) {
        return produktRepository.save(produkt);
    }

    public Boolean obsahujeProdukt(String id) {
        return produktRepository.existsById(id);
    }

    public Optional<Produkt> vratProdukt(String id) {
        return produktRepository.getProduktById(id);
    }

    public List<Produkt> vratVsetkyProdukty() {
        return produktRepository.findAll();
    }

    public ResponseEntity<?> odstranProdukt(String id) {
        if (produktRepository.existsById(id)) {
            Optional<Produkt> vratenyProdukt = produktRepository.getProduktById(id);
            System.out.println(vratenyProdukt.get().getNazov());
             produktRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body("Produkt bol odstraneny");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produkt s danym ID neexistuje");
        }
    }

    @Transactional
    public Produkt aktualizujProdukt(String id, Produkt novyProdukt) {
        Optional<Produkt> existujuciProduktOptional = produktRepository.getProduktById(id);


        if (existujuciProduktOptional.isPresent()) {
            Produkt existujuciProdukt = existujuciProduktOptional.get();

            existujuciProdukt.setNazov(novyProdukt.getNazov());
            existujuciProdukt.setPopis(novyProdukt.getPopis());
            existujuciProdukt.setTypTechniky(novyProdukt.getTypTechniky());
            existujuciProdukt.setPocetKusov(novyProdukt.getPocetKusov());

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
       return produktRepository.deleteProduktsByIdIn(zoznamIDProduktov);
    }
}
