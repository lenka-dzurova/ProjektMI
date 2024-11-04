package back_end.audio_video.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;


@Getter
@Setter
public class AktualizujProduktRequest {
    private String idProdukt;
    private String produktJSON;
    private MultipartFile obrazok;
}
