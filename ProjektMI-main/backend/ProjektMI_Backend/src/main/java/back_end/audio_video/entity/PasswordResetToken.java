package back_end.audio_video.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String token;
    private LocalDateTime expirationDate;

    @ManyToOne
    @JoinColumn(name = "id_pouzivatel")
    private Pouzivatel pouzivatel;
}
