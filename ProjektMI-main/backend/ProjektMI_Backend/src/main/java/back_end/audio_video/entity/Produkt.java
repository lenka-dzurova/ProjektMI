package back_end.audio_video.entity;

import back_end.audio_video.details.Rola;
import back_end.audio_video.details.StavProduktu;
import back_end.audio_video.details.Technika;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Produkt {
    @Id
    private String idProdukt;
    private String nazov;
    @Lob
    private String popis;
    @Lob
    private byte[] obrazok;
    @Enumerated(EnumType.STRING)
    private Technika typTechniky;
    @Enumerated(EnumType.STRING)
    private Rola rolaProduktu;
    @Enumerated(EnumType.STRING)
    private StavProduktu stavProduktu;
}
