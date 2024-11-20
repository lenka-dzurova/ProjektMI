package back_end.audio_video.request;


import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AktualizujPouzivatelRequest {
    private UUID idPouzivatel;
    private String meno;
    private String priezvisko;
    private String telCislo;
    private String ulica;
    private String psc;
    private String mesto;
}
