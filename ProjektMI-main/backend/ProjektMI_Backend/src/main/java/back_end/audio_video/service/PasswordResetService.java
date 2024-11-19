package back_end.audio_video.service;

import back_end.audio_video.controller.PouzivatelController;
import back_end.audio_video.entity.PasswordResetToken;
import back_end.audio_video.entity.Pouzivatel;
import back_end.audio_video.repository.PasswordResetRepository;
import back_end.audio_video.repository.PouzivatelRepository;
import back_end.audio_video.request.PasswordResetRequest;
import lombok.Setter;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class PasswordResetService {

    @Autowired
    private PasswordResetRepository passwordResetRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${backend.url}")
    private String backendUrl;

    @Autowired
    private PouzivatelRepository pouzivatelRepository;


    public PasswordResetToken createPasswordResetToken(Pouzivatel pouzivatel) {
        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setPouzivatel(pouzivatel);
        resetToken.setExpirationDate(LocalDateTime.now().plusHours(1));
        passwordResetRepository.save(resetToken);
        return resetToken;
    }


    public void sendResetPasswordEmail(Pouzivatel pouzivatel, String token) {
        String resetUrl = String.format("%s/reset-password?token=%s", backendUrl, token);
        String message = "kliknite na nasledujuci odkaz na resetovanie vasho hesla: " + resetUrl;
        emailService.sendResetPasswordMail(pouzivatel.getEmail(), "Resetovanie hesla", message);
    }
}
