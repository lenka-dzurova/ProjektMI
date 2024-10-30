package back_end.audio_video.repository;

import back_end.audio_video.entity.DocasnyPouzivatel;
import back_end.audio_video.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, UUID> {
    Optional<VerificationToken> findByToken(String token);
    List<VerificationToken> findAllByExpiryDateIsBefore(LocalDateTime date);
    VerificationToken findByDocasnyPouzivatel(DocasnyPouzivatel docasnyPouzivatel);
}
