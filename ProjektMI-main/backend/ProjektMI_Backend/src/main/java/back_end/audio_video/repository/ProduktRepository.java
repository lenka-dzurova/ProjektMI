package back_end.audio_video.repository;

import back_end.audio_video.entity.Produkt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface ProduktRepository extends JpaRepository<Produkt, String> {
    boolean existsById(String id);
    Optional<Produkt> getProduktById(String id);
    int deleteProduktsByIdIn(List<String> ids);
}
