package back_end.audio_video.repository;

import back_end.audio_video.details.Rola;
import back_end.audio_video.details.StavProduktu;
import back_end.audio_video.details.Technika;
import back_end.audio_video.entity.Produkt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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
    List<Produkt> findAllByNazovContainingIgnoreCase(String nazov);
    List<Produkt> findAllByRolaProduktuAndStavProduktuAndNazovContainingIgnoreCase(Rola rolaProduktu, StavProduktu stavProduktu, String nazov);
    List<Produkt> findAllByStavProduktuAndNazovContainingIgnoreCase(StavProduktu stavProduktu, String nazov);


    @Query("""
    SELECT p FROM Produkt p
    WHERE p NOT IN (
        SELECT DISTINCT op.produkt FROM ObjednavkaProdukt op
        JOIN op.objednavka o
        WHERE o.datumVypozicania <= :koniec AND o.datumVratenia >= :zaciatok
    )
    AND p.rolaProduktu = :rolaProduktu
    AND p.stavProduktu = :stavProduktu
    """)
    List<Produkt> findVolneProduktyByRola(
            @Param("zaciatok") LocalDate zaciatok,
            @Param("koniec") LocalDate koniec,
            @Param("rolaProduktu") Rola rolaProduktu,
            @Param("stavProduktu") StavProduktu stavProduktu);

    @Query("""
    SELECT p FROM Produkt p
    WHERE p NOT IN (
        SELECT DISTINCT op.produkt FROM ObjednavkaProdukt op
        JOIN op.objednavka o
        WHERE o.datumVypozicania <= :koniec AND o.datumVratenia >= :zaciatok
    )
    AND p.stavProduktu = :stavProduktu
    """)
    List<Produkt> findVolneProdukty(
            @Param("zaciatok") LocalDate zaciatok,
            @Param("koniec") LocalDate koniec,
            @Param("stavProduktu") StavProduktu stavProduktu);



    @Query("""
    SELECT DISTINCT p FROM Produkt p
    WHERE (
        p NOT IN (
            SELECT op.produkt FROM ObjednavkaProdukt op
            JOIN op.objednavka o
            WHERE o.datumVypozicania <= :koniec AND o.datumVratenia >= :zaciatok
        )
    )
    AND LOWER(p.nazov) LIKE LOWER(CONCAT('%', :nazov, '%'))
    AND p.rolaProduktu = :rolaProduktu
    AND p.stavProduktu = :stavProduktu
    """)
    List<Produkt> findProduktyByVolneDatumNazovAndFiltersByRola(
            @Param("zaciatok") LocalDate zaciatok,
            @Param("koniec") LocalDate koniec,
            @Param("nazov") String nazov,
            @Param("rolaProduktu") Rola rolaProduktu,
            @Param("stavProduktu") StavProduktu stavProduktu);


    @Query("""
    SELECT DISTINCT p FROM Produkt p
    WHERE (
        p NOT IN (
            SELECT op.produkt FROM ObjednavkaProdukt op
            JOIN op.objednavka o
            WHERE o.datumVypozicania <= :koniec AND o.datumVratenia >= :zaciatok
        )
    )
    AND LOWER(p.nazov) LIKE LOWER(CONCAT('%', :nazov, '%'))
    AND p.stavProduktu = :stavProduktu
    """)
    List<Produkt> findProduktyByVolneDatumNazovAndFilters(
            @Param("zaciatok") LocalDate zaciatok,
            @Param("koniec") LocalDate koniec,
            @Param("nazov") String nazov,
            @Param("stavProduktu") StavProduktu stavProduktu);

}
