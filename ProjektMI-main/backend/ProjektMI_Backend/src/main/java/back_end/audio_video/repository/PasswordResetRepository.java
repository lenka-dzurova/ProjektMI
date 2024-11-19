package back_end.audio_video.repository;

import back_end.audio_video.entity.PasswordResetToken;
import jakarta.persistence.GeneratedValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;


@Repository
public interface PasswordResetRepository extends JpaRepository<PasswordResetToken, UUID> {
    PasswordResetToken findByToken(String token);
    void deleteByExpirationDateBefore(LocalDateTime now);
}
