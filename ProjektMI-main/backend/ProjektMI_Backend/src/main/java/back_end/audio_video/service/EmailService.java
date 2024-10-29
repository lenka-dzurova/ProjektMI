package back_end.audio_video.service;


import back_end.audio_video.entity.Objednavka;
import back_end.audio_video.entity.ObjednavkaProdukt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class EmailService {

    @Value("${backend.url}")
    private String backendUrl;
    @Autowired
    private JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String adminMail;
    @Autowired
    private SpringTemplateEngine templateEngine;

    public void sendMail(String to, String subject,String meno, String priezvisko, String verificationURL) {
        Context context = new Context();
        context.setVariable("meno", meno);
        context.setVariable("priezvisko", priezvisko);
        context.setVariable("verificationURL", verificationURL);

        String htmlContent = templateEngine.process("email-template", context);

        MimeMessagePreparator messagePreparator = this.createMail(adminMail, to, subject, htmlContent);

        javaMailSender.send(messagePreparator);
    }

    public void poslatEmailAdministratorovi(Objednavka objednavka) {
        String schvalitUrl = String.format("%s/objednavka/schvalit/%s", backendUrl,objednavka.getIdObjednavka());
        String zamietnutUrl = String.format("%s/objednavka/zamietnut/%s", backendUrl,objednavka.getIdObjednavka());

        Context context = new Context();
        context.setVariable("idObjednavka", objednavka.getIdObjednavka().toString());
        context.setVariable("schvalitUrl", schvalitUrl);
        context.setVariable("zamietnutUrl", zamietnutUrl);
        context.setVariable("menoObjednavatela", objednavka.getPouzivatel().getMeno());
        context.setVariable("priezviskoObjednavatel", objednavka.getPouzivatel().getPriezvisko());

        List<String> produktList = new ArrayList<>();

        for (ObjednavkaProdukt objednavkaProdukt : objednavka.getObjednavkaProdukty()) {
            String objednavkaObsah = String.format("Produkt ID: %s, Dátum vypožičania: %s, Dátum vrátenia: %s",
                    objednavkaProdukt.getProdukt().getIdProdukt(),
                    objednavkaProdukt.getDatumVypozicania(),
                    objednavkaProdukt.getDatumVratenia());

            produktList.add(objednavkaObsah);
        }

        context.setVariable("produkty", produktList);


        String htmlContext = templateEngine.process("objednavka-schvalenie", context);

        MimeMessagePreparator messagePreparator = this.createMail(objednavka.getPouzivatel().getEmail(), adminMail, "Nová objednávka na schválenie", htmlContext);
        javaMailSender.send(messagePreparator);
    }


    private MimeMessagePreparator createMail(String from, String to, String subject, String htmlContent) {
        return mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom(from);
            messageHelper.setTo(to);
            messageHelper.setSubject(subject);
            messageHelper.setText(htmlContent, true);
        };
    }
}
