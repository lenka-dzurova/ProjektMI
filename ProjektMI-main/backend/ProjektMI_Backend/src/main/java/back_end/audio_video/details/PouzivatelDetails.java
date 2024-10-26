package back_end.audio_video.details;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.UUID;

public class PouzivatelDetails implements UserDetails {
    private UUID id;
    private String meno;
    private String priezvisko;
    private String email;
    private Rola rola;

    public PouzivatelDetails(UUID id, String meno, String priezvisko, String email, Rola rola) {
        this.id = id;
        this.meno = meno;
        this.priezvisko = priezvisko;
        this.email = email;
        this.rola = rola;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return "";
    }

    public UUID getId() {
        return this.id;
    }

    public String getMeno() {
        return this.meno;
    }

    public String getPriezvisko() {
        return this.priezvisko;
    }

    public String getEmail() {
        return this.email;
    }

    public Rola getRola() {
        return this.rola;
    }
}

