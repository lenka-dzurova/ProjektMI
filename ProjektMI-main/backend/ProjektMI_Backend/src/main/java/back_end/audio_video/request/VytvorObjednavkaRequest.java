package back_end.audio_video.request;


import back_end.audio_video.dto.ObjednavkaProduktDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class VytvorObjednavkaRequest {
    private UUID pouzivatelId;
    private List<ObjednavkaProduktDTO> objednavkaProduktyDTO;
}
