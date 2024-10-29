package back_end.audio_video.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Setter
public class ObjednavkaProduktDTO {
    private UUID id;
    private String produktId;
    private LocalDateTime datumVypozicania;
    private LocalDateTime datumVratenia;
}
