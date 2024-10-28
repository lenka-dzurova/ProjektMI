package back_end.audio_video.repository;


import back_end.audio_video.entity.Pouzivatel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PouzivatelRepository extends JpaRepository<Pouzivatel, UUID> {
    Optional<Pouzivatel> findByEmail(String email);
    Optional<Pouzivatel> findByIdPouzivatel(UUID idPouzivatel);
}
