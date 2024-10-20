package back_end.audio_video.entity;

import back_end.audio_video.details.Rola;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
public class Pouzivatel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID idPouzivatel;
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
    @Enumerated(EnumType.STRING)
    private Rola rola;
}
