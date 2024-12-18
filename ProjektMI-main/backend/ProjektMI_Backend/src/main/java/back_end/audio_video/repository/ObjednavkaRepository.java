package back_end.audio_video.repository;


import back_end.audio_video.entity.Objednavka;
import back_end.audio_video.entity.ObjednavkaProdukt;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ObjednavkaRepository extends JpaRepository<Objednavka, UUID> {
    Optional<Objednavka> findByIdObjednavka(UUID id);

    List<Objednavka> findAllByPouzivatelIdPouzivatelOrderByDatumVytvoreniaDesc(UUID userId);

    List<Objednavka> findAllByObjednavkaProduktyIsEmpty();

}
