package back_end.audio_video.request;


import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class AktualizaciaObjednavkyRequest {
    private UUID idObjednavka;
    private List<AktualizaciaDatumVrateniaRequest> produkty;
}
