package back_end.audio_video.repository;

import back_end.audio_video.details.Rola;
import back_end.audio_video.details.StavProduktu;
import back_end.audio_video.details.Technika;
import back_end.audio_video.entity.Produkt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProduktRepository extends JpaRepository<Produkt, String> {
    boolean existsByIdProdukt(String id);
    Optional<Produkt> getProduktByIdProdukt(String id);
    int deleteProduktsByIdProduktIn(List<String> ids);
    List<Produkt> findAllByTypTechniky(Technika typTechniky);
    List<Produkt> findAllByRolaProduktuAndStavProduktuAndTypTechniky(Rola rola, StavProduktu stavProduktu, Technika typTechniky);
    List<Produkt> findAllByStavProduktuAndTypTechniky(StavProduktu stavProduktu, Technika typTechniky);
    List<Produkt> findAllByTypTechnikyAndNazovContainingIgnoreCase(Technika typTechniky, String nazov);
    List<Produkt> findAllByRolaProduktuAndStavProduktuAndTypTechnikyAndNazovContainingIgnoreCase(Rola rolaProduktu, StavProduktu stavProduktu, Technika typTechniky, String nazov);
}
