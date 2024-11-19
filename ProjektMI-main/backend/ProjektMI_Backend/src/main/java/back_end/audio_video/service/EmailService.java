package back_end.audio_video.service;


import back_end.audio_video.entity.Objednavka;
import back_end.audio_video.entity.ObjednavkaProdukt;
import back_end.audio_video.entity.Pouzivatel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

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

        List<ObjednavkaProdukt> produktList = new ArrayList<>();

        for (ObjednavkaProdukt objednavkaProdukt : objednavka.getObjednavkaProdukty()) {
            ObjednavkaProdukt produktDetail = new ObjednavkaProdukt();
            produktDetail.setProdukt(objednavkaProdukt.getProdukt());
            produktDetail.setDatumVypozicania(objednavkaProdukt.getDatumVypozicania());
            produktDetail.setDatumVratenia(objednavkaProdukt.getDatumVratenia());

            produktList.add(produktDetail);
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

        List<ObjednavkaProdukt> obsahObjednavky = new ArrayList<>();

        for (ObjednavkaProdukt objednavkaProdukt : objednavka.getObjednavkaProdukty()) {
            ObjednavkaProdukt produktDetail = new ObjednavkaProdukt();
            produktDetail.setProdukt(objednavkaProdukt.getProdukt());
            produktDetail.setDatumVypozicania(objednavkaProdukt.getDatumVypozicania());
            produktDetail.setDatumVratenia(objednavkaProdukt.getDatumVratenia());

            obsahObjednavky.add(produktDetail);
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

        List<ObjednavkaProdukt> obsahObjednavky = new ArrayList<>();

        for (ObjednavkaProdukt objednavkaProdukt : objednavka.getObjednavkaProdukty()) {
            ObjednavkaProdukt produktDetail = new ObjednavkaProdukt();
            produktDetail.setProdukt(objednavkaProdukt.getProdukt());
            produktDetail.setDatumVypozicania(objednavkaProdukt.getDatumVypozicania());
            produktDetail.setDatumVratenia(objednavkaProdukt.getDatumVratenia());

            obsahObjednavky.add(produktDetail);
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

    public void sendResetPasswordMail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom(adminMail);

        javaMailSender.send(message);
    }
}
