package back_end.audio_video.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class AktualizaciaDatumVrateniaRequest {
    private UUID idObjednavkaProdukt;
    private LocalDate datumVratenia;
}
