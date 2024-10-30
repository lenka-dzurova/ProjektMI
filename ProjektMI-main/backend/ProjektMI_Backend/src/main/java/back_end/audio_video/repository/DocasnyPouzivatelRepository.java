package back_end.audio_video.repository;

import back_end.audio_video.entity.DocasnyPouzivatel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface DocasnyPouzivatelRepository extends JpaRepository<DocasnyPouzivatel, UUID> {
    Optional<DocasnyPouzivatel> findByEmail(String email);
}
