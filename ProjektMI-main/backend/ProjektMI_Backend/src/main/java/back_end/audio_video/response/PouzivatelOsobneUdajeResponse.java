package back_end.audio_video.response;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PouzivatelOsobneUdajeResponse {
    private String meno;
    private String priezvisko;
    private String email;
    private String telCislo;
    private String ulica;
    private String psc;
    private String mesto;
}
