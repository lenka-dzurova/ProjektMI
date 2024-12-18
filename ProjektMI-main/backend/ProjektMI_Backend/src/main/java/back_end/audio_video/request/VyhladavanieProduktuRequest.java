package back_end.audio_video.request;


import back_end.audio_video.details.Rola;
import back_end.audio_video.details.Technika;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class VyhladavanieProduktuRequest {
    private Rola rola;
    private String nazov;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate datumVypozicania;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate datumVratenia;
    private Technika typTechniky;
}
