package back_end.audio_video.request;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class IdPouzivatelRequest {
    private UUID idPouzivatel;
}