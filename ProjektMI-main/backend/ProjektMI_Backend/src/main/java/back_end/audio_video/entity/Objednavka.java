package back_end.audio_video.entity;


import back_end.audio_video.details.StavObjednavky;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private LocalDate datumVypozicania;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy")
    private LocalDate datumVratenia;

    @ManyToOne
    @JoinColumn(name = "id_pouzivatel")
    private Pouzivatel pouzivatel;

    @OneToMany(mappedBy = "objednavka", cascade = CascadeType.ALL)
    private List<ObjednavkaProdukt> objednavkaProdukty;

    @Enumerated(EnumType.STRING)
    @Column(name = "stav_objednavky", length = 15, nullable = false)
    private StavObjednavky stavObjednavky;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd.MM.yyyy HH:mm:ss")
    private LocalDateTime datumVytvorenia;
}
