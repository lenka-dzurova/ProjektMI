package back_end.audio_video.service;

import back_end.audio_video.entity.Objednavka;
import com.lowagie.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class PDFGeneratorService {

    @Autowired
    private TemplateEngine templateEngine;


    public void generatePDF(Objednavka objednavka, HttpServletResponse response) {
        Context context = new Context();
        context.setVariable("objednavka", objednavka);


        String hmtmlContent = templateEngine.process("zoznam-objednavok", context);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "filename=zoznam_objednavok.pdf");

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(hmtmlContent);
            renderer.layout();

            renderer.createPDF(outputStream);

            response.getOutputStream().write(outputStream.toByteArray());
        } catch (DocumentException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
