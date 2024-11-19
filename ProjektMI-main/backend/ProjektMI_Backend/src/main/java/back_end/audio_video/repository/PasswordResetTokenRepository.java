package back_end.audio_video.repository;

import back_end.audio_video.entity.PasswordResetToken;
import back_end.audio_video.entity.Pouzivatel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {
    PasswordResetToken findByToken(String token);
    Optional<PasswordResetToken> findByPouzivatel(Pouzivatel pouzivatel);
    void removePasswordResetTokenByExpirationDateBefore(LocalDateTime expiration);
}
