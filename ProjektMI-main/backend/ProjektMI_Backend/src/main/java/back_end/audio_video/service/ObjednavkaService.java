package back_end.audio_video.service;


import back_end.audio_video.component.ObjednavkaMapper;
import back_end.audio_video.details.StavObjednavky;
import back_end.audio_video.dto.ObjednavkaDTO;
import back_end.audio_video.dto.ObjednavkaProduktDTO;
import back_end.audio_video.entity.Objednavka;
import back_end.audio_video.entity.ObjednavkaProdukt;
import back_end.audio_video.entity.Pouzivatel;
import back_end.audio_video.entity.Produkt;
import back_end.audio_video.repository.ObjednavkaProduktRepository;
import back_end.audio_video.repository.ObjednavkaRepository;
import back_end.audio_video.repository.PouzivatelRepository;
import back_end.audio_video.repository.ProduktRepository;
import back_end.audio_video.request.AktualizaciaDatumVrateniaRequest;
import back_end.audio_video.request.AktualizaciaObjednavkyRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    public ObjednavkaDTO vytvorObjednavku(UUID pouzivatelId, List<ObjednavkaProduktDTO> objednavkaProduktyDTO) {
        Optional<Pouzivatel> pouzivatel = pouzivatelRepository.findById(pouzivatelId);

        if (pouzivatel.isEmpty()) {
            throw new RuntimeException("Pouzivatel nenajdeny");
        }

        Objednavka objednavka = new Objednavka();
        objednavka.setPouzivatel(pouzivatel.get());
        objednavka.setStavObjednavky(StavObjednavky.CAKAJUCA);
        objednavka.setDatumObjednavky(LocalDateTime.now());

        objednavkaRepository.save(objednavka);

        List<ObjednavkaProdukt> objednavkaProdukty = new ArrayList<>();

        for (ObjednavkaProduktDTO produktDTO : objednavkaProduktyDTO) {
            Optional<Produkt> produkt = produktRepository.getProduktByIdProdukt(produktDTO.getProduktId());

            if (produkt.isEmpty()) {
                throw new RuntimeException("Produkt nenajdeny");
            }

            ObjednavkaProdukt objednavkaProdukt = new ObjednavkaProdukt();
            objednavkaProdukt.setObjednavka(objednavka);
            objednavkaProdukt.setProdukt(produkt.get());
            objednavkaProdukt.setDatumVypozicania(produktDTO.getDatumVypozicania());
            objednavkaProdukt.setDatumVratenia(produktDTO.getDatumVratenia());

            objednavkaProduktRepository.save(objednavkaProdukt);

            objednavkaProdukty.add(objednavkaProdukt);
        }

        objednavka.setObjednavkaProdukty(objednavkaProdukty);

        this.emailService.sendMailAdministrator(objednavka);

        return objednavkaMapper.objednavkaToDTO(objednavka);
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
        // Implementácia logiky na získanie objednávok podľa ID používateľa
        return objednavkaRepository.findAllByPouzivatelIdPouzivatel(userId);
    }

    public List<ObjednavkaProdukt> getProductsByOrderId(UUID orderId) {
        return objednavkaProduktRepository.findObjednavkaProduktByObjednavka_IdObjednavka(orderId);
    }

    public ResponseEntity<?> upravProduktyObjednavky(AktualizaciaObjednavkyRequest request) {
        List<ObjednavkaProdukt> produkty = objednavkaProduktRepository.findObjednavkaProduktByObjednavka_IdObjednavka(request.getIdObjednavka());

        if (produkty.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Žiadne produkty pre danú objednávku neboli nájdené");
        }

        for (AktualizaciaDatumVrateniaRequest aktualizaciaProduktRequest : request.getProdukty()) {
            for (ObjednavkaProdukt produkt : produkty) {
                if (produkt.getId().toString().equals(aktualizaciaProduktRequest.getIdObjednavkaProdukt().toString())) {

                    produkt.setDatumVratenia(aktualizaciaProduktRequest.getDatumVratenia());
                }
            }
        }

        objednavkaProduktRepository.saveAll(produkty);

        return ResponseEntity.ok("Produkty objednávky boli úspešne aktualizované");
    }
}
