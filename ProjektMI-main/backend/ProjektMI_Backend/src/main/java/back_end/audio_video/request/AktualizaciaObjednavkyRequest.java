package back_end.audio_video.request;


import back_end.audio_video.details.StavObjednavky;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class AktualizaciaObjednavkyRequest {
    private UUID idObjednavka;
    private LocalDate datumVratenia;
    private StavObjednavky stavObjednavky;
}
