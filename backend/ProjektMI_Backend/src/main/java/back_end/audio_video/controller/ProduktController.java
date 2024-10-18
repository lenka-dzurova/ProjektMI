package back_end.audio_video.controller;

import back_end.audio_video.entity.Produkt;
import back_end.audio_video.service.ProduktService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/produkt")
public class ProduktController {
    @Autowired
    private ProduktService produktService;
    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @PostMapping("/pridat")
    public ResponseEntity<?> pridajProdukt(@RequestParam String id, @RequestParam String nazov, @RequestParam String popis,
                                           @RequestParam MultipartFile obrazok, @RequestParam String typTechniky,
                                           @RequestParam Integer pocetKusov) {

        if (!produktService.obsahujeProdukt(id)) {
            try {
                Produkt newProdukt = new Produkt();
                if (!id.isEmpty()) {
                    newProdukt.setId(id);
                } else {
                    newProdukt.setId(UUID.randomUUID().toString()); // ZMEN AK NEBUDU CHCIET UUID
                }
                newProdukt.setNazov(nazov);
                newProdukt.setPopis(popis);
                newProdukt.setTypTechniky(typTechniky);
                newProdukt.setPocetKusov(pocetKusov);
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

    @GetMapping("/get-all")
    public List<Produkt> getAllProducts() {
        return produktService.vratVsetkyProdukty();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> odstranProdukt(@PathVariable String id) {
        return produktService.odstranProdukt(id);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> aktualizujProdukt(@PathVariable String id, @RequestParam("produkt") String produktJson, @RequestParam(required = false) MultipartFile obrazok) {

        try {
            Produkt novyProdukt = jacksonObjectMapper.readValue(produktJson, Produkt.class);

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
