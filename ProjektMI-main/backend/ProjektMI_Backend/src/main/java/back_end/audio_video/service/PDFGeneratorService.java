package back_end.audio_video.service;

import back_end.audio_video.entity.Objednavka;
import back_end.audio_video.entity.ObjednavkaProdukt;
import back_end.audio_video.entity.Pouzivatel;
import back_end.audio_video.entity.Produkt;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import com.itextpdf.text.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class PDFGeneratorService {

    @Autowired
    private TemplateEngine templateEngine;

//    public void generatePDFForOrders(List<Objednavka> objednavky, HttpServletResponse response) throws IOException, DocumentException {
//        // Inicializácia PDF
//        Document document = new Document();
//        PdfWriter.getInstance(document, response.getOutputStream());
//
//        response.setContentType("application/pdf");
//        response.setHeader("Content-Disposition", "attachment; filename=zoznam_objednavok.pdf");
//
//        document.open();
//
//        // Nadpis dokumentu
//        document.add(new Paragraph("Zoznam objednávok", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
//        document.add(new Paragraph("\n"));
//
//        // Iterácia cez objednávky
//        for (Objednavka objednavka : objednavky) {
//            // Pridanie detailov objednávky
//            document.add(new Paragraph("Objednávka ID: " + objednavka.getIdObjednavka(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
//            document.add(new Paragraph("Dátum objednávky: " + objednavka.getDatumObjednavky()));
//            document.add(new Paragraph("Zákazník: " + objednavka.getPouzivatel().getMeno() + " " + objednavka.getPouzivatel().getPriezvisko()));
//            document.add(new Paragraph("Produkty:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
//
//            // Iterácia cez produkty objednávky
//            for (ObjednavkaProdukt objednavkaProdukt : objednavka.getObjednavkaProdukty()) {
//                document.add(new Paragraph(" - " + objednavkaProdukt.getProdukt().getNazov()));
//            }
//
//            // Pridať prázdny riadok medzi objednávkami
//            document.add(new Paragraph("\n"));
//        }
//
//        document.close();
//    }



    public void generatePDF(List<Objednavka> objednavky, HttpServletResponse response) {
        Context context = new Context();
        context.setVariable("objednavky", objednavky);


        String hmtmlContent = templateEngine.process("zoznam-objednavok", context);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "filename=zoznam_objednavok.pdf");

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(hmtmlContent);
            renderer.layout();

            renderer.createPDF(outputStream);

            response.getOutputStream().write(outputStream.toByteArray());
        } catch (com.lowagie.text.DocumentException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
