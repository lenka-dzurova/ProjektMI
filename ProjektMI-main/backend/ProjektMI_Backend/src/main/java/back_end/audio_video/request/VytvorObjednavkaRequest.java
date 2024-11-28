package back_end.audio_video.request;


import back_end.audio_video.dto.ObjednavkaProduktDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class VytvorObjednavkaRequest {
    private UUID pouzivatelId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "d. M. yyyy")
    private LocalDate datumVypozicania;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "d. M. yyyy")
    private LocalDate datumVratenia;

    private List<ObjednavkaProduktDTO> objednavkaProduktyDTO;
}
