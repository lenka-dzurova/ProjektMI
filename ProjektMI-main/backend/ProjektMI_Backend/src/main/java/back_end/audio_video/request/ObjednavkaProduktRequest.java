package back_end.audio_video.request;

import back_end.audio_video.entity.Objednavka;
import back_end.audio_video.entity.Produkt;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class ObjednavkaProduktRequest {
    private UUID idObjednavka;
}
