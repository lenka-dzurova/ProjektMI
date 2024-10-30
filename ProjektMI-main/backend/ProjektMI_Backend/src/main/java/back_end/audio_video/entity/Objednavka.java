package back_end.audio_video.entity;


import back_end.audio_video.details.StavObjednavky;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Objednavka {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID idObjednavka;

    private LocalDateTime datumObjednavky;

    @ManyToOne
    @JoinColumn(name = "id_pouzivatel")
    private Pouzivatel pouzivatel;

    @OneToMany(mappedBy = "objednavka", cascade = CascadeType.ALL)
    private List<ObjednavkaProdukt> objednavkaProdukty;

    @Enumerated(EnumType.STRING)
    private StavObjednavky stavObjednavky;
}
