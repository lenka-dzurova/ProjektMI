package back_end.audio_video.repository;


import back_end.audio_video.entity.ObjednavkaProdukt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ObjednavkaProduktRepository extends JpaRepository<ObjednavkaProdukt, UUID> {
}
