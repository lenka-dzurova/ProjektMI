package back_end.audio_video.response;

import back_end.audio_video.details.Rola;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PouzivatelResponse {
    private String id;
    private String meno;
    private String priezvisko;
    private String email;
    private Rola rola;
}
