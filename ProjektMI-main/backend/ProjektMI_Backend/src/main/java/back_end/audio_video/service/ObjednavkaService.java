package back_end.audio_video.service;


import back_end.audio_video.component.ObjednavkaMapper;
import back_end.audio_video.details.StavObjednavky;
import back_end.audio_video.dto.ObjednavkaProduktDTO;
import back_end.audio_video.entity.Objednavka;
import back_end.audio_video.entity.ObjednavkaProdukt;
import back_end.audio_video.entity.Pouzivatel;
import back_end.audio_video.entity.Produkt;
import back_end.audio_video.repository.ObjednavkaProduktRepository;
import back_end.audio_video.repository.ObjednavkaRepository;
import back_end.audio_video.repository.PouzivatelRepository;
import back_end.audio_video.repository.ProduktRepository;
import back_end.audio_video.request.AktualizaciaObjednavkyRequest;
import back_end.audio_video.request.VytvorObjednavkaRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ObjednavkaService {
    @Autowired
    private ObjednavkaRepository objednavkaRepository;
    @Autowired
    private PouzivatelRepository pouzivatelRepository;
    @Autowired
    private ProduktRepository produktRepository;
    @Autowired
    private ObjednavkaProduktRepository objednavkaProduktRepository;
    @Autowired
    private ObjednavkaMapper objednavkaMapper;
    @Autowired
    private EmailService emailService;

    public ResponseEntity<?> vytvorObjednavku(VytvorObjednavkaRequest request) {
        Optional<Pouzivatel> pouzivatel = pouzivatelRepository.findById(request.getPouzivatelId());

        if (pouzivatel.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Pouzivatel sa nenasiel");
        } else {

            System.out.println(request.getDatumVratenia());

            Objednavka objednavka = new Objednavka();
            objednavka.setPouzivatel(pouzivatel.get());
            objednavka.setStavObjednavky(StavObjednavky.CAKAJUCA);
            objednavka.setDatumVypozicania(request.getDatumVypozicania());
            objednavka.setDatumVratenia(request.getDatumVratenia());

            objednavkaRepository.save(objednavka);

            List<ObjednavkaProdukt> objednavkaProdukty = new ArrayList<>();

            for (ObjednavkaProduktDTO produktDTO : request.getObjednavkaProduktyDTO()) {
                Optional<Produkt> produkt = produktRepository.getProduktByIdProdukt(produktDTO.getProduktId());

                if (produkt.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produkty sa nenasli");
                }

                ObjednavkaProdukt objednavkaProdukt = new ObjednavkaProdukt();
                objednavkaProdukt.setObjednavka(objednavka);
                objednavkaProdukt.setProdukt(produkt.get());

                objednavkaProduktRepository.save(objednavkaProdukt);

                objednavkaProdukty.add(objednavkaProdukt);
            }

            objednavka.setObjednavkaProdukty(objednavkaProdukty);

            this.emailService.sendMailAdministrator(objednavka);

            return ResponseEntity.ok(objednavkaMapper.objednavkaToDTO(objednavka));
        }
    }

    public void schvalitObjednavku(UUID id) {
        Optional<Objednavka> najdenaObjednavka = objednavkaRepository.findByIdObjednavka(id);
        if (najdenaObjednavka.isPresent()) {
            Objednavka objednavka = najdenaObjednavka.get();
            objednavka.setStavObjednavky(StavObjednavky.SCHVALENA);
            objednavkaRepository.save(objednavka);
            emailService.zaslanieMailuSchvalenejObjednavky(objednavka);
        } else {
            throw new RuntimeException("Objednávka nenájdená.");
        }
    }

    public void zamietnutObjednavku(UUID id) {
        Optional<Objednavka> najdenaObjednavka = objednavkaRepository.findByIdObjednavka(id);

        if (najdenaObjednavka.isPresent()) {
            Objednavka objednavka = najdenaObjednavka.get();
            objednavka.setStavObjednavky(StavObjednavky.ZAMIETNUTA);
            objednavkaRepository.save(objednavka);
            emailService.zaslanieMailuZamietnutejObjednavky(objednavka);
        } else {
            throw new RuntimeException("Objednávka nenájdená.");
        }
    }

    public List<ObjednavkaProdukt> getObjednavkyPodlaProduktId(String id) {
        return objednavkaProduktRepository.findObjednavkaProduktByProduktIdProduktAndObjednavka_StavObjednavky(id, StavObjednavky.valueOf(StavObjednavky.SCHVALENA.toString()));
    }


    public List<Objednavka> getOrdersByUserId(UUID userId) {
        return objednavkaRepository.findAllByPouzivatelIdPouzivatel(userId);
    }

    public List<ObjednavkaProdukt> getProductsByOrderId(UUID orderId) {
        return objednavkaProduktRepository.findObjednavkaProduktByObjednavka_IdObjednavka(orderId);
    }

    public ResponseEntity<?> upravObjednavku(AktualizaciaObjednavkyRequest request) {
        Optional<Objednavka> objednavkaOptional = objednavkaRepository.findByIdObjednavka(request.getIdObjednavka());

        if (objednavkaOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Objednavka nebola najdena");
        }

        Objednavka objednavka = objednavkaOptional.get();
        objednavka.setDatumVratenia(request.getDatumVratenia());

        objednavkaRepository.save(objednavka);

        return ResponseEntity.ok().body("Produkty objednávky boli úspešne aktualizované");
    }


    //TODO POTOM AK TAK ODSTRAN ALE AK BUDE FUNGOVAT VRAT ZOZNAM PRE KONKRETNY DEN
    public List<Objednavka> zoznamObjednavok() {
        return objednavkaRepository.findAll();
    }
}
