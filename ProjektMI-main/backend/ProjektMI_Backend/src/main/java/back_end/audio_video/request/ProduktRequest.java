package back_end.audio_video.request;

import back_end.audio_video.details.Rola;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;


@Getter
@Setter
public class ProduktRequest {
    private String id;
    private String nazov;
    private String popis;
    private MultipartFile obrazok;
    private String typTechniky;
    private Rola rolaProduktu;
}
