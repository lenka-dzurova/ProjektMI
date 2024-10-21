package back_end.audio_video.service;


import back_end.audio_video.repository.VerificationTokenRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String fromEmail;
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    @Autowired
    private PouzivatelService pouzivatelService;
    @Autowired
    private SpringTemplateEngine templateEngine;

    //  public void sendMail(String to, String subject, String htmlContent) {
    public void sendMail(String to, String subject,String meno, String priezvisko, String verificationURL) {
        Context context = new Context();
        context.setVariable("meno", meno);
        context.setVariable("priezvisko", priezvisko);
        context.setVariable("verificationURL", verificationURL);

        String htmlContent = templateEngine.process("email-template", context);

//        MimeMessage message = javaMailSender.createMimeMessage();
        //        try {
//            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//            helper.setFrom(fromEmail);
//            helper.setTo(to);
//            helper.setSubject(subject);
//            helper.setText(htmlContent, true);
//            javaMailSender.send(message);
//        } catch (MessagingException e) {
//            System.err.println("Zaslanie emailu bolo neuspesne!");
//        }
        //TODO POTOM ESTE RAZ OTESTUJ AK FUNGUJE NECHAJ TOTO
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom(fromEmail);
            messageHelper.setTo(to);
            messageHelper.setSubject(subject);
            messageHelper.setText(htmlContent, true);
        };
        javaMailSender.send(messagePreparator);
    }
}
