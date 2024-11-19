package back_end.audio_video.controller;


import back_end.audio_video.dto.ObjednavkaDTO;
import back_end.audio_video.dto.ObjednavkaProduktDTO;
import back_end.audio_video.entity.Objednavka;
import back_end.audio_video.entity.ObjednavkaProdukt;
import back_end.audio_video.exception.ObjednavkaNotFoundException;
import back_end.audio_video.request.*;
import back_end.audio_video.service.ObjednavkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/objednavka")
public class ObjednavkaController {
    @Autowired
    private ObjednavkaService objednavkaService;
    @Autowired
    private SpringTemplateEngine templateEngine;

    @PostMapping("/vytvor")
    public ResponseEntity<ObjednavkaDTO> vytvorObjednavku(@RequestBody VytvorObjednavkaRequest request) {
        ObjednavkaDTO objednavkaDTO = objednavkaService.vytvorObjednavku(request.getPouzivatelId(), request.getObjednavkaProduktyDTO());
        return ResponseEntity.status(HttpStatus.CREATED).body(objednavkaDTO);
    }

    @GetMapping("/schvalit/{id}")
    public ResponseEntity<?> schvalit(@PathVariable UUID id) {
        try {
            objednavkaService.schvalitObjednavku(id);
            Context context = new Context();
            context.setVariable("idObjednavka", id);
            String htmlContent = templateEngine.process("objednavka-schvalena-admin", context);
            return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(htmlContent);
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
            Context context = new Context();
            context.setVariable("idObjednavka", id);
            String htmlContent = templateEngine.process("objednavka-zamietnuta-admin", context);
            return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(htmlContent);
        } catch (ObjednavkaNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Objednávka nebola nájdená: " + id);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Nastala chyba pri spracovaní požiadavky");
        }
    }

    @PostMapping("/datumy-objednavok")
    public ResponseEntity<?> vratZoznamDatumyObjednavok(@RequestBody IdProduktRequest idProduktRequest) {
        List<ObjednavkaProdukt> objednavky = objednavkaService.getObjednavkyPodlaProduktId(idProduktRequest.getIdProdukt());

        if (objednavky.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<ObjednavkaProduktDTO> zoznamDatumov = objednavky.stream().map(objednavkaProdukt -> {
            ObjednavkaProduktDTO objednavkaProduktDTO = new ObjednavkaProduktDTO();
            objednavkaProduktDTO.setId(objednavkaProdukt.getId());
            objednavkaProduktDTO.setProduktId(objednavkaProdukt.getProdukt().getIdProdukt());
            objednavkaProduktDTO.setDatumVypozicania(objednavkaProdukt.getDatumVypozicania());
            objednavkaProduktDTO.setDatumVratenia(objednavkaProdukt.getDatumVratenia());
            return objednavkaProduktDTO;
        }).toList();


        return ResponseEntity.ok(zoznamDatumov);
    }

    @PostMapping("/get-all-by-user-id")
    public List<Objednavka> getAllOrdersByUserId(@RequestBody PouzivatelRequest pouzivatelRequest) {
        return objednavkaService.getOrdersByUserId(pouzivatelRequest.getIdPouzivatel());
    }

    @PostMapping("/get-products-by-order-id")
    public List<ObjednavkaProdukt> getProductsByOrderId(@RequestBody ObjednavkaProduktRequest objednavkaProduktRequest) {
        return objednavkaService.getProductsByOrderId(objednavkaProduktRequest.getIdObjednavka());
    }

    @PostMapping("/update-date")
    public ResponseEntity<?> updateProduktOreders(@RequestBody AktualizaciaObjednavkyRequest request) {
        return objednavkaService.upravProduktyObjednavky(request);
    }
}
