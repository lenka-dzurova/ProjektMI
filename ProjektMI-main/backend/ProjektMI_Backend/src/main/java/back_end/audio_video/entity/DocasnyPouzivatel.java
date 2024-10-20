package back_end.audio_video.entity;

import back_end.audio_video.details.Rola;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
public class DocasnyPouzivatel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID idDocasnyPouzivatel;
    private String meno;
    private String priezvisko;
    @Column(unique = true)
    private String email;
    private String heslo;
    private String telCislo;
    private String ulica;
    @Column(length = 5)
    private String psc;
    private String mesto;
    private Rola rola;
}
