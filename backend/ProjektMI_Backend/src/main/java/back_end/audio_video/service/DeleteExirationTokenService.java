package back_end.audio_video.service;

import back_end.audio_video.entity.DocasnyPouzivatel;
import back_end.audio_video.entity.VerificationToken;
import back_end.audio_video.repository.DocasnyPouzivatelRepository;
import back_end.audio_video.repository.VerificationTokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class DeleteExirationTokenService {
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    @Autowired
    private DocasnyPouzivatelRepository docasnyPouzivatelRepository;

    @Transactional
    public void removeAllExpiredUsersAndTokens() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiryThreshold = now.minusHours(24);

        List<VerificationToken> tokens = verificationTokenRepository.findAllByExpiryDateIsBefore(expiryThreshold);

        for (VerificationToken token : tokens) {
            DocasnyPouzivatel docasnyPouzivatel = token.getDocasnyPouzivatel();
            verificationTokenRepository.delete(token);
            if (docasnyPouzivatel != null) {
                docasnyPouzivatelRepository.delete(docasnyPouzivatel);
            }
        }
    }
}
