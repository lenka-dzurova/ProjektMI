package back_end.audio_video.controller;



import back_end.audio_video.entity.Objednavka;
import back_end.audio_video.entity.ObjednavkaProdukt;
import back_end.audio_video.exception.ObjednavkaNotFoundException;
import back_end.audio_video.request.*;
import back_end.audio_video.response.ProduktDatumyResponse;
import back_end.audio_video.service.ObjednavkaService;
import back_end.audio_video.service.PDFGeneratorService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/objednavka")
public class ObjednavkaController {
    @Autowired
    private ObjednavkaService objednavkaService;
    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
    private PDFGeneratorService pdfGeneratorService;


    @PostMapping("/vytvor")
    public ResponseEntity<?> vytvorObjednavku(@RequestBody VytvorObjednavkaRequest request) {
        return objednavkaService.vytvorObjednavku(request);
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


    //TODO ZISTI OD LENKY CI TO POUZIVAME
    @PostMapping("/datumy-objednavok")
    public ResponseEntity<?> vratZoznamDatumyObjednavok(@RequestBody IdProduktRequest idProduktRequest) {
        List<ObjednavkaProdukt> objednavky = objednavkaService.getObjednavkyPodlaProduktId(idProduktRequest.getIdProdukt());

        if (objednavky.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<ProduktDatumyResponse> datumyResponses = new ArrayList<>();

        for (ObjednavkaProdukt objednavkaProdukt : objednavky) {
            ProduktDatumyResponse produktDatumyResponse = new ProduktDatumyResponse();
            produktDatumyResponse.setDatumVypozicania(objednavkaProdukt.getObjednavka().getDatumVypozicania());
            produktDatumyResponse.setDatumVratenia(objednavkaProdukt.getObjednavka().getDatumVratenia());
            datumyResponses.add(produktDatumyResponse);
        }



        return ResponseEntity.ok(datumyResponses);
    }

    @PostMapping("/get-all-by-user-id")
    public List<Objednavka> getAllOrdersByUserId(@RequestBody PouzivatelRequest pouzivatelRequest) {
        return objednavkaService.getOrdersByUserId(pouzivatelRequest.getIdPouzivatel());
    }

    @PostMapping("/get-products-by-order-id")
    public List<ObjednavkaProdukt> getProductsByOrderId(@RequestBody ObjednavkaProduktRequest objednavkaProduktRequest) {
        return objednavkaService.getProductsByOrderId(objednavkaProduktRequest.getIdObjednavka());
    }

    @PostMapping("/update-objednavka")
    public ResponseEntity<?> updateProduktOreders(@RequestBody List<AktualizaciaObjednavkyRequest> request) {
        return objednavkaService.upravObjednavku(request);
    }

    @PostMapping("/generate")
    public void downloadPDF(HttpServletResponse response, @RequestBody IdObjednavkyRequest request) {
        try {
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=products.pdf");

            Objednavka objednavka = objednavkaService.zoznamObjednavok(request);

            pdfGeneratorService.generatePDF(objednavka, response);
        } catch (Exception e) {
            throw new RuntimeException("Problem " + e);
        }
    }
}
