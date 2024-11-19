package back_end.audio_video.controller;

import back_end.audio_video.entity.PasswordResetToken;
import back_end.audio_video.entity.Pouzivatel;
import back_end.audio_video.repository.PasswordResetTokenRepository;
import back_end.audio_video.request.EmailRequest;
import back_end.audio_video.request.PasswordResetRequest;
import back_end.audio_video.service.PouzivatelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;


@RestController
@RequestMapping("/reset-password")
public class PasswordResetController {
    @Autowired
    private PouzivatelService pouzivatelService;
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Value("${spring.mail.username}")
    private String emailAdmin;
    @Autowired
    private SpringTemplateEngine templateEngine;
    @Value("${backend.url}")
    private String backendUrl;

    @PostMapping("/request")
    public String requestPasswordReset(@RequestBody EmailRequest request) {
        String email = request.getEmail();
        String token = pouzivatelService.generateResetToken(email);
        sendPasswordResetEmail(email, token);
        return "Email bol odoslany";
    }

    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest request) {
        String token = request.getToken();
        if (pouzivatelService.validateResetToken(token)) {
            PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token);
            Pouzivatel pouzivatel = resetToken.getPouzivatel();
            pouzivatelService.updatePassword(pouzivatel, request.getNewPassword());

            passwordResetTokenRepository.delete(resetToken);

            return ResponseEntity.ok("Heslo bolo úspešne resetované.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token je neplatný alebo vypršaný.");
        }
    }

    @GetMapping("/reset/{token}")
    public ResponseEntity<?> showResetPasswordForm(@PathVariable String token) {
        if (!pouzivatelService.validateResetToken(token)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token je neplatný alebo vypršaný.");
        }

        Context context = new Context();
        context.setVariable("token", token);
        context.setVariable("backendUrl", backendUrl);
        String htmlContent = templateEngine.process("reset-password-form", context);

        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(htmlContent);
    }


    private void sendPasswordResetEmail(String email, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailAdmin);
        message.setTo(email);
        message.setSubject("Resetovanie hesla");
        message.setText("Kliknite na tento odkaz na resetovanie hesla: " +
                "http://localhost:8080/reset-password/reset/" + token);
        javaMailSender.send(message);
    }
}
