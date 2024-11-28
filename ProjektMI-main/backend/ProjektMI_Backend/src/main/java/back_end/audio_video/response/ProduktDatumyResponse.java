package back_end.audio_video.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ProduktDatumyResponse {
    private LocalDate datumVypozicania;
    private LocalDate datumVratenia;
}
