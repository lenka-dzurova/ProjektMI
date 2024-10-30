package back_end.audio_video.repository;


import back_end.audio_video.entity.Objednavka;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ObjednavkaRepository extends JpaRepository<Objednavka, UUID> {
    Optional<Objednavka> findByIdObjednavka(UUID id);
}
