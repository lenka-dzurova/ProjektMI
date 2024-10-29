package back_end.audio_video.service;

import back_end.audio_video.component.JwtUtil;
import back_end.audio_video.details.PouzivatelDetails;
import back_end.audio_video.details.Rola;
import back_end.audio_video.entity.DocasnyPouzivatel;
import back_end.audio_video.entity.Pouzivatel;
import back_end.audio_video.entity.VerificationToken;
import back_end.audio_video.repository.DocasnyPouzivatelRepository;
import back_end.audio_video.repository.PouzivatelRepository;
import back_end.audio_video.repository.VerificationTokenRepository;
import back_end.audio_video.request.LoginRequest;
import back_end.audio_video.response.PouzivatelResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class PouzivatelService {

    @Autowired
    private PouzivatelRepository pouzivatelRepository;
    @Autowired
    private DocasnyPouzivatelRepository docasnyPouzivatelRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    @Lazy
    private EmailService emailService;

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private SpringTemplateEngine templateEngine;


    public Boolean pouzivatelExistuje(String email) {
        return pouzivatelRepository.findByEmail(email).isPresent();
    }

    public Boolean registerPouzivatel(Pouzivatel pouzivatel) {
        Optional<DocasnyPouzivatel> docasnyPouzivatelDatabaza = docasnyPouzivatelRepository.findByEmail(pouzivatel.getEmail());
        Optional<Pouzivatel> pouzivatelDatabaza = pouzivatelRepository.findByEmail(pouzivatel.getEmail());

        if (pouzivatelDatabaza.isPresent() || docasnyPouzivatelDatabaza.isPresent()) {
            return false;
        } else {
            if (pouzivatel.getEmail().contains("uniza.sk")) {
                DocasnyPouzivatel docasnyPouzivatel = new DocasnyPouzivatel();
                docasnyPouzivatel.setMeno(pouzivatel.getMeno());
                docasnyPouzivatel.setPriezvisko(pouzivatel.getPriezvisko());
                docasnyPouzivatel.setEmail(pouzivatel.getEmail());
                docasnyPouzivatel.setHeslo(passwordEncoder.encode(pouzivatel.getHeslo()));
                docasnyPouzivatel.setTelCislo(pouzivatel.getTelCislo());
                docasnyPouzivatel.setUlica(pouzivatel.getUlica());
                docasnyPouzivatel.setPsc(pouzivatel.getPsc());
                docasnyPouzivatel.setMesto(pouzivatel.getMesto());

                docasnyPouzivatelRepository.save(docasnyPouzivatel);

                String token = UUID.randomUUID().toString();

                VerificationToken verificationToken = new VerificationToken(docasnyPouzivatel, token);

                verificationTokenRepository.save(verificationToken);

                String verificationURL = "http://localhost:8080/verify?token=" + token;
                emailService.sendMailVerification(docasnyPouzivatel.getEmail(), "Potvrdenie registrácie",
                        docasnyPouzivatel.getMeno(), docasnyPouzivatel.getPriezvisko(), verificationURL);
                return true;
            } else {
                return false;
            }
        }
    }

    private String vratObsahEmailu(String meno, String priezvisko, String verificationURL) {
        return "<html>"
                + "<body>"
                + "<h1>Potvrdenie registrácie</h1>"
                + "<p>Dobrý deň " + meno + " " + priezvisko + ",</p>"
                + "<p>Prosím, potvrďte svoju registráciu kliknutím na tento odkaz:</p>"
                + "<p><a href='" + verificationURL + "'>Potvrdiť registráciu</a></p>"
                + "<p>Ďakujeme!</p>"
                + "</body>"
                + "</html>";
    }


    public ResponseEntity<?> verifyUser(String token) {
        Optional<VerificationToken> verificationTokenOpt = verificationTokenRepository.findByToken(token);

        System.out.println(verificationTokenOpt);

        if (verificationTokenOpt.isPresent()) {
            VerificationToken verificationToken = verificationTokenOpt.get();

            if (verificationToken.isExpired()) {
                return ResponseEntity.badRequest().body("Overovací kód vypršal.");
            }

            DocasnyPouzivatel docasnyPouzivatel = docasnyPouzivatelRepository.findByEmail(verificationToken.getDocasnyPouzivatel().getEmail()).orElse(null);

            if (docasnyPouzivatel == null) {
                return ResponseEntity.badRequest().body("Dočasný používateľ nebol nájdený.");
            }

            Pouzivatel novyPouzivatel = new Pouzivatel();
            novyPouzivatel.setMeno(docasnyPouzivatel.getMeno());
            novyPouzivatel.setPriezvisko(docasnyPouzivatel.getPriezvisko());
            novyPouzivatel.setEmail(docasnyPouzivatel.getEmail());
            novyPouzivatel.setHeslo(docasnyPouzivatel.getHeslo());
            novyPouzivatel.setTelCislo(docasnyPouzivatel.getTelCislo());
            novyPouzivatel.setUlica(docasnyPouzivatel.getUlica());
            novyPouzivatel.setPsc(docasnyPouzivatel.getPsc());
            novyPouzivatel.setMesto(docasnyPouzivatel.getMesto());

            if (novyPouzivatel.getEmail().contains("stud")) {
                novyPouzivatel.setRola(Rola.STUDENT);
            } else {
                novyPouzivatel.setRola(Rola.UCITEL);
            }

            pouzivatelRepository.save(novyPouzivatel);

            verificationTokenRepository.delete(verificationToken);
            docasnyPouzivatelRepository.delete(docasnyPouzivatel);



            String htmlContent = templateEngine.process("email-confirmation", new Context());
            return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(htmlContent);
        }

        return ResponseEntity.badRequest().body("Neplatný overovací kód.");
    }

    public PouzivatelDetails loginPouzivatel(LoginRequest loginRequest) {
        Optional<Pouzivatel> pouzivatelDatabaza = pouzivatelRepository.findByEmail(loginRequest.getEmail());


        if (pouzivatelDatabaza.isEmpty()) {
            throw new UsernameNotFoundException("Nesprávny email");
        }

        if (!passwordEncoder.matches(loginRequest.getHeslo(), pouzivatelDatabaza.get().getHeslo())) {
            throw new BadCredentialsException("Nesprávne heslo");
        }

        Pouzivatel pouzivatel = pouzivatelDatabaza.get();

        return new PouzivatelDetails(
                pouzivatel.getIdPouzivatel(),
                pouzivatel.getMeno(),
                pouzivatel.getPriezvisko(),
                pouzivatel.getEmail(),
                pouzivatel.getRola()
        );
    }


    public ResponseEntity<?> resendVerificationEmail(String email) {
        Optional<DocasnyPouzivatel> docasnyPouzivatelDatabaza = docasnyPouzivatelRepository.findByEmail(email);

        if (docasnyPouzivatelDatabaza.isPresent()) {
            DocasnyPouzivatel docasnyPouzivatel = docasnyPouzivatelDatabaza.get();
            VerificationToken verificationToken = verificationTokenRepository.findByDocasnyPouzivatel(docasnyPouzivatel);

            if (verificationToken != null && !verificationToken.isExpired()) {
                String token = verificationToken.getToken();
                String verificationURL = "http://localhost:8080/verify?token=" + token;
                String htmlContent = this.vratObsahEmailu(docasnyPouzivatel.getMeno(), docasnyPouzivatel.getPriezvisko(), verificationURL);
                    //TODO POTOM ODKOMENTUJ
//                emailService.sendMail(docasnyPouzivatel.getEmail(), "Potvrdenie registrácie", htmlContent);
                return ResponseEntity.ok("E-mail na potvrdenie bol odoslaný.");
            } else {
                String newToken = UUID.randomUUID().toString();
                VerificationToken newVerificationToken = new VerificationToken(docasnyPouzivatel, newToken);
                verificationTokenRepository.save(newVerificationToken);

                String verificationURL = "http://localhost:8080/verify?token=" + newToken;  // TODO toto asi zmen aby http://localhost:8080/ vedeli zmenit v properities
                String htmlContent = this.vratObsahEmailu(docasnyPouzivatel.getMeno(), docasnyPouzivatel.getPriezvisko(), verificationURL);
                //TODO POTOM ODKOMENTUJ
//                emailService.sendMail(docasnyPouzivatel.getEmail(), "Potvrdenie registrácie", htmlContent);

                return ResponseEntity.ok("E-mail na potvrdenie bol odoslaný s novým tokenom.");
            }
        }
        return ResponseEntity.badRequest().body("Používateľ nebol nájdený.");
    }

    public PouzivatelResponse getPozivatelZTokena(String token) {
        try {

            PouzivatelResponse pouzivatelResponse = new PouzivatelResponse();
            pouzivatelResponse.setId(jwtUtil.extractID(token));
            pouzivatelResponse.setMeno(jwtUtil.extractMeno(token));
            pouzivatelResponse.setPriezvisko(jwtUtil.extractPriezvisko(token));
            pouzivatelResponse.setEmail(jwtUtil.extractEmail(token));
            pouzivatelResponse.setRola(Rola.valueOf(jwtUtil.extractRola(token).toUpperCase()));

            return pouzivatelResponse;
        } catch (Exception e) {
            return  null;
        }
    }
    public List<Pouzivatel> vratVsetkychPouzivatelov() {
        return pouzivatelRepository.findAll();
    }

    public ResponseEntity<?> updateRola(UUID id, Rola rola) {
        Optional<Pouzivatel> pouzivatel = pouzivatelRepository.findByIdPouzivatel(id);
        if (pouzivatel.isPresent()) {
            pouzivatel.get().setRola(rola);
            return ResponseEntity.ok(pouzivatelRepository.save(pouzivatel.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}