package back_end.audio_video.dto;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ObjednavkaDTO {
    private UUID idObjednavka;
    private LocalDateTime datumObjednavky;
    private UUID idPouzivatela;
    private List<ObjednavkaProduktDTO> objednavkaProdukty;
}
