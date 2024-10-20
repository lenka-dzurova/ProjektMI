package back_end.audio_video.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
public class VerificationToken {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @Column(nullable = false, unique = true)
    private String token;

    @OneToOne(targetEntity = DocasnyPouzivatel.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "id_docasny_pouzivatel")
    private DocasnyPouzivatel docasnyPouzivatel;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    public VerificationToken() {
    }

    public VerificationToken(DocasnyPouzivatel docasnyPouzivatel, String token) {
        this.docasnyPouzivatel = docasnyPouzivatel;
        this.token = token;
        this.expiryDate = LocalDateTime.now().plusHours(24);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiryDate);
    }
}
