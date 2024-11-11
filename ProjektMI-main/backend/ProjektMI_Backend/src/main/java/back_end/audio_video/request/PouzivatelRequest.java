package back_end.audio_video.request;

import back_end.audio_video.details.Rola;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Getter
@Setter
public class PouzivatelRequest {

    private UUID idPouzivatel;
    private String meno;
    private String priezvisko;
}
