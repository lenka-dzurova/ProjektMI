package back_end.audio_video.repository;


import back_end.audio_video.entity.ObjednavkaProdukt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ObjednavkaProduktRepository extends JpaRepository<ObjednavkaProdukt, UUID> {
    List<ObjednavkaProdukt> findObjednavkaProduktByProduktIdProdukt(String id);
}