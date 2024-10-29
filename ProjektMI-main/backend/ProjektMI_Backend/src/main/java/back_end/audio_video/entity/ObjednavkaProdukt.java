package back_end.audio_video.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
public class ObjednavkaProdukt {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "id_objednavka")
    private Objednavka objednavka;

    @ManyToOne
    @JoinColumn(name = "id_produkt")
    private Produkt produkt;

    private LocalDateTime datumVypozicania;
    private LocalDateTime datumVratenia;
}
