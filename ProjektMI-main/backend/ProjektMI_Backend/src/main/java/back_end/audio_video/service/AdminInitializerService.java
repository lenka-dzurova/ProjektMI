package back_end.audio_video.service;

import back_end.audio_video.config.SecurityConfig;
import back_end.audio_video.details.Rola;
import back_end.audio_video.entity.Pouzivatel;
import back_end.audio_video.repository.PouzivatelRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AdminInitializerService {
    @Value("${admin.email}")
    private String email;
    @Value("${admin.heslo}")
    private String heslo;
    @Value("${admin.meno}")
    private String meno;
    @Value("${admin.priezvisko}")
    private String priezvisko;

    @Autowired
    private SecurityConfig securityConfig;

    @Autowired
    private PouzivatelRepository pouzivatelRepository;

    @PostConstruct
    public void initAdmin() {
        if (pouzivatelRepository.count() == 0) {
            Pouzivatel admin = new Pouzivatel();

            admin.setEmail(email);
            admin.setHeslo(securityConfig.passwordEncoder().encode(heslo));
            admin.setMeno(meno);
            admin.setPriezvisko(priezvisko);
            admin.setRola(Rola.ADMIN);

            pouzivatelRepository.save(admin);
        }
    }
}
