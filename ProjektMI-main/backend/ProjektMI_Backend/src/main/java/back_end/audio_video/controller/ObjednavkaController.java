package back_end.audio_video.controller;


import back_end.audio_video.dto.ObjednavkaDTO;
import back_end.audio_video.dto.ObjednavkaProduktDTO;
import back_end.audio_video.entity.Objednavka;
import back_end.audio_video.exception.ObjednavkaNotFoundException;
import back_end.audio_video.service.ObjednavkaService;
import back_end.audio_video.service.PouzivatelService;
import jakarta.persistence.GeneratedValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/objednavka")
public class ObjednavkaController {
    @Autowired
    private ObjednavkaService objednavkaService;

    @PostMapping("/vytvor")//TODO POTOM SKUS CI SA TO NEDA CEZ REQUEST BODY
    public ResponseEntity<ObjednavkaDTO> vytvorObjednavku(@RequestParam UUID pouzivatelId, @RequestBody List<ObjednavkaProduktDTO> objednavkaProduktyDTO) {
        ObjednavkaDTO objednavkaDTO = objednavkaService.vytvorObjednavku(pouzivatelId, objednavkaProduktyDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(objednavkaDTO);
    }

    @GetMapping("/schvalit/{id}")
    public ResponseEntity<?> schvalit(@PathVariable UUID id) {
        System.out.println("ID " + id);
        try {
            objednavkaService.schvalitObjednavku(id);
            return ResponseEntity.ok("Objednávka schválená");
        } catch (ObjednavkaNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Objednávka nebola nájdená: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Nastala chyba pri spracovaní požiadavky");
        }
    }

    @GetMapping("/zamietnut/{id}")
    public ResponseEntity<?> neschvalit(@PathVariable UUID id) {
        try {
            objednavkaService.zamietnutObjednavku(id);
            return ResponseEntity.ok("Objednávka bola zamietnutá");
        } catch (ObjednavkaNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Objednávka nebola nájdená: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Nastala chyba pri spracovaní požiadavky");
        }
    }
}
