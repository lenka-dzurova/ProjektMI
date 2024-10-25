package back_end.audio_video.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private SpringTemplateEngine templateEngine;

    public void sendMail(String to, String subject,String meno, String priezvisko, String verificationURL) {
        Context context = new Context();
        context.setVariable("meno", meno);
        context.setVariable("priezvisko", priezvisko);
        context.setVariable("verificationURL", verificationURL);

        String htmlContent = templateEngine.process("email-template", context);

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
