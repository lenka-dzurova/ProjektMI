package back_end.audio_video.service;

import back_end.audio_video.details.PouzivatelDetails;
import back_end.audio_video.entity.Pouzivatel;
import back_end.audio_video.repository.PouzivatelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private PouzivatelRepository pouzivatelRepository; // Tvoje repository na načítanie používateľov

    @Override
    public PouzivatelDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Pouzivatel pouzivatel = pouzivatelRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Používateľ s emailom " + email + " neexistuje"));

        return new PouzivatelDetails(pouzivatel.getIdPouzivatel()
                ,pouzivatel.getMeno()
                ,pouzivatel.getPriezvisko()
                ,pouzivatel.getEmail()
                ,pouzivatel.getRola());
    }
}