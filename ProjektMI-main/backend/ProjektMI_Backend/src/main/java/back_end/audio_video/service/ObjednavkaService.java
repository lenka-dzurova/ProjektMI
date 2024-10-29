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
import org.springframework.beans.factory.annotation.Autowired;
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

        this.emailService.poslatEmailAdministratorovi(objednavka);

        return objednavkaMapper.objednavkaToDTO(objednavka);
    }

    public void schvalitObjednavku(UUID id) {
        Optional<Objednavka> najdenaObjednavka = objednavkaRepository.findByIdObjednavka(id);

        if (najdenaObjednavka.isPresent()) {
            Objednavka objednavka = najdenaObjednavka.get();
            objednavka.setStavObjednavky(StavObjednavky.SCHVALENA);
            objednavkaRepository.save(objednavka);
        } else {
            throw new RuntimeException("Objednávka nenájdená.");
        }
    }

    public void zamietnutObjednavku(UUID id) {
        Optional<Objednavka> najdenaObjednavka = objednavkaRepository.findByIdObjednavka(id);

        if (najdenaObjednavka.isPresent()) {
            Objednavka objednavka = najdenaObjednavka.get();
            objednavka.setStavObjednavky(StavObjednavky.ZAMIETNUTA);
            objednavkaRepository.saveAndFlush(objednavka);
        } else {
            throw new RuntimeException("Objednávka nenájdená.");
        }
    }
}
