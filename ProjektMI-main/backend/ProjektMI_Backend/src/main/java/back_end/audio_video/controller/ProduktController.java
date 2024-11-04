package back_end.audio_video.controller;

import back_end.audio_video.details.Rola;
import back_end.audio_video.entity.Produkt;
import back_end.audio_video.request.AktualizujProduktRequest;
import back_end.audio_video.request.ProduktRequest;
import back_end.audio_video.request.RolaRequest;
import back_end.audio_video.service.ProduktService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/produkt")
public class ProduktController {
    @Autowired
    private ProduktService produktService;
    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @PostMapping("/pridat")
    public ResponseEntity<?> pridajProdukt(@ModelAttribute ProduktRequest produktRequest) {
        String id = produktRequest.getId();
        String nazov = produktRequest.getNazov();
        String popis = produktRequest.getPopis();
        String typTechniky = produktRequest.getTypTechniky();
        Rola rola = produktRequest.getRolaProduktu();
        MultipartFile obrazok = produktRequest.getObrazok();

        if (!produktService.obsahujeProdukt(id)) {
            try {
                Produkt newProdukt = new Produkt();

                if (!id.isEmpty()) {
                    newProdukt.setIdProdukt(id);
                } else {
                    newProdukt.setIdProdukt(UUID.randomUUID().toString());
                }
                newProdukt.setNazov(nazov);
                newProdukt.setPopis(popis);
                newProdukt.setTypTechniky(typTechniky);
                newProdukt.setRolaProduktu(rola);
                newProdukt.setObrazok(obrazok.getBytes());

                produktService.pridajProdukt(newProdukt);

                return ResponseEntity.status(HttpStatus.CREATED).body(newProdukt);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Produkt sa nepodarilo pridat");
            }
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Produkt s danym ID uz existuje");
        }
    }


    @GetMapping("/get-produkt/{id}")
    public ResponseEntity<?> getProdukt(@PathVariable String id) {
        Optional<Produkt> produkt = produktService.vratProdukt(id);

        if (produkt.isPresent()) {
            return ResponseEntity.ok().body(produkt.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produkt s ID sa nenasiel");
        }
    }

    @PostMapping("/get-all-by-rola")
    public List<Produkt> getAllProducts(@RequestBody RolaRequest rolaRequest) {
        return produktService.vratVsetkyProdukty(rolaRequest.getRolaProduktu());
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> odstranProdukt(@PathVariable String id) {
        produktService.odstranProdukt(id);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/delete-produkty")
    public ResponseEntity<?> deleteProdukts(@RequestBody List<String> produktIDs) {
        try {
            int pocetOdstranenych = produktService.odstraProduktyPodlaID(produktIDs);

            if (pocetOdstranenych == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Žiadny produkt nebol nájdený na vymazanie.");
            }
            return ResponseEntity.ok("Produkty boli úspešne vymazané.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Nastala chyba pri odstraňovaní produktov.");
        }
    }

    @PutMapping("/update")
    public ResponseEntity<?> aktualizujProdukt(@ModelAttribute AktualizujProduktRequest aktualizujProduktRequest) {

        String id = aktualizujProduktRequest.getIdProdukt();
        String json = aktualizujProduktRequest.getProduktJSON();
        MultipartFile obrazok = aktualizujProduktRequest.getObrazok();
        try {
            Produkt novyProdukt = jacksonObjectMapper.readValue(json, Produkt.class);

            if (obrazok != null && !obrazok.isEmpty()) {
                novyProdukt.setObrazok(obrazok.getBytes());
            }

            Produkt aktualizovanyProdukt = produktService.aktualizujProdukt(id, novyProdukt);
            return ResponseEntity.ok().body(aktualizovanyProdukt);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Chyba pri spracovani dat");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produkt s id " + id + " neexistuje");
        }
    }
}
