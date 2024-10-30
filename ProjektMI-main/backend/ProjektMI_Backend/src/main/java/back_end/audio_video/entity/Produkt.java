package back_end.audio_video.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
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
    private String typTechniky;
}
