package back_end.audio_video.service;


import back_end.audio_video.entity.Objednavka;
import back_end.audio_video.entity.ObjednavkaProdukt;
import back_end.audio_video.entity.Pouzivatel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

    public void sendMailVerification(String to, String subject, String meno, String priezvisko, String verificationURL) {
        Context context = new Context();
        context.setVariable("meno", meno);
        context.setVariable("priezvisko", priezvisko);
        context.setVariable("verificationURL", verificationURL);

        String htmlContent = templateEngine.process("email-template", context);

        MimeMessagePreparator messagePreparator = this.createMail(adminMail, to, subject, htmlContent);

        javaMailSender.send(messagePreparator);
    }

    public void sendMailAdministrator(Objednavka objednavka) {
        String schvalitUrl = String.format("%s/objednavka/schvalit/%s", backendUrl, objednavka.getIdObjednavka());
        String zamietnutUrl = String.format("%s/objednavka/zamietnut/%s", backendUrl, objednavka.getIdObjednavka());

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
                    objednavkaProdukt.getDatumVypozicania().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                    objednavkaProdukt.getDatumVratenia().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));

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

    public void zaslanieMailuSchvalenejObjednavky(Objednavka objednavka) {
        Pouzivatel pouzivatel = objednavka.getPouzivatel();
        String email = pouzivatel.getEmail();
        String meno= pouzivatel.getMeno();
        String priezviko = pouzivatel.getPriezvisko();
        UUID id = objednavka.getIdObjednavka();
        String subject = "Schválená objednávka";

        List<String> obsahObjednavky = new ArrayList<>();

        for (ObjednavkaProdukt objednavkaProdukt : objednavka.getObjednavkaProdukty()) {
            String produkt = String.format("Produkt ID: %s\n, Dátum vypožičania: %s\n, Dátum vrátenia: %s",
                    objednavkaProdukt.getProdukt().getNazov(),
                    objednavkaProdukt.getDatumVypozicania().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                    objednavkaProdukt.getDatumVratenia().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
            obsahObjednavky.add(produkt);
        }

        Context context = new Context();
        context.setVariable("meno", meno);
        context.setVariable("priezvisko", priezviko);
        context.setVariable("idObjednavky", id);
        context.setVariable("obsahObjednavky", obsahObjednavky);

        String htmlContent = templateEngine.process("objednavka-schvalena-objednavatel", context);

        MimeMessagePreparator messagePreparator = this.createMail(adminMail, email, subject, htmlContent);

        javaMailSender.send(messagePreparator);
    }

    public void zaslanieMailuZamietnutejObjednavky(Objednavka objednavka) {
        Pouzivatel pouzivatel = objednavka.getPouzivatel();
        String email = pouzivatel.getEmail();
        String meno= pouzivatel.getMeno();
        String priezviko = pouzivatel.getPriezvisko();
        UUID id = objednavka.getIdObjednavka();
        String subject = "Zamietnutá objednávka";

        List<String> obsahObjednavky = new ArrayList<>();

        for (ObjednavkaProdukt objednavkaProdukt : objednavka.getObjednavkaProdukty()) {
            String produkt = String.format("Produkt ID: %s%n, Dátum vypožičania: %s%n, Dátum vrátenia: %s",
                    objednavkaProdukt.getProdukt().getNazov(),
                    objednavkaProdukt.getDatumVypozicania().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                    objednavkaProdukt.getDatumVratenia().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
            obsahObjednavky.add(produkt);
        }

        Context context = new Context();
        context.setVariable("meno", meno);
        context.setVariable("priezvisko", priezviko);
        context.setVariable("idObjednavky", id);
        context.setVariable("obsahObjednavky", obsahObjednavky);

        String htmlContent = templateEngine.process("objednavka-zamietnuta-objednavatel", context);

        MimeMessagePreparator messagePreparator = this.createMail(adminMail, email, subject, htmlContent);

        javaMailSender.send(messagePreparator);
    }
}
