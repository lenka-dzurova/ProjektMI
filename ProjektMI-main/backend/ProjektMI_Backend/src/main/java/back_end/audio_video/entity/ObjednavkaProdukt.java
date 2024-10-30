package back_end.audio_video.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "d. M. yyyy")
    private LocalDate datumVypozicania;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "d. M. yyyy")
    private LocalDate datumVratenia;
}
