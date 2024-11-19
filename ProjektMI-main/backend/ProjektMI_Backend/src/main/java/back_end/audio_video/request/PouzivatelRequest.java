package back_end.audio_video.request;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class PouzivatelRequest {
    private UUID idPouzivatel;
    private String meno;
    private String priezvisko;
}
