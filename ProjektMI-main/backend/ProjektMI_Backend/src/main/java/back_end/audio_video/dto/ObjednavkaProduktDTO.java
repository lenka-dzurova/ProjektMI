package back_end.audio_video.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;


@Getter
@Setter
public class ObjednavkaProduktDTO {
    private UUID id;
    private String produktId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "d. M. yyyy")
    private LocalDate datumVypozicania;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "d. M. yyyy")
    private LocalDate datumVratenia;
}
