package back_end.audio_video.controller;


import back_end.audio_video.component.JwtUtil;
import back_end.audio_video.details.PouzivatelDetails;
import back_end.audio_video.details.Rola;
import back_end.audio_video.entity.Pouzivatel;
import back_end.audio_video.request.AktualizujPouzivatelRequest;
import back_end.audio_video.request.EmailRequest;
import back_end.audio_video.request.IdPouzivatelRequest;
import back_end.audio_video.request.LoginRequest;
import back_end.audio_video.response.PouzivatelResponse;
import back_end.audio_video.service.PouzivatelService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class PouzivatelController {
    @Autowired
    private PouzivatelService pouzivatelService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Pouzivatel pouzivatel) {

        if (pouzivatelService.pouzivatelExistuje(pouzivatel.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Pouzivatel existuje");
        } else {
            if (pouzivatelService.registerPouzivatel(pouzivatel)) {
                return ResponseEntity.status(HttpStatus.CREATED).body("Treba potvrdit ucet");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Zadany email nie je povoleny");
            }
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {

        try {
            PouzivatelDetails pouzivatelDetails = pouzivatelService.loginPouzivatel(loginRequest);

            String jwtToken = jwtUtil.generateToken(pouzivatelDetails);

            ResponseCookie cookie = ResponseCookie.from("JWT", jwtToken)
                    .httpOnly(true)
                    .path("/")
                    .maxAge(24 * 60 * 60)
                    .secure(false) //TODO Ked sa backend nasadi treba zmenit na TRUE
//                    .sameSite("None") //TODO Toto ked nastavis secure na TRUE tak to odkumentuj
                    .build();

            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body("Prihlásenie úspešné");
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nesprávny email");
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nesprávne heslo");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Chyba pri prihlásení");
        }
    }


    @PostMapping("/odhlasenie")
    public ResponseEntity<?> logout() {
        ResponseCookie cookie = ResponseCookie.from("JWT", "")
                .httpOnly(true)
                .path("/")
                .domain("localhost") // Zmeňte na vašu doménu, ak je iná
                .maxAge(0) // Nastavte maxAge na 0 na odstránenie cookie
                .secure(false) //TODO Ked sa backend nasadi treba zmenit na TRUE
                .build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).location(URI.create("/login")).body("Odhlásenie úspešné");
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String token) {
        return pouzivatelService.verifyUser(token);
    }

    @PostMapping("/resend-verification-email")
    public ResponseEntity<?> resendVerificationEmail(@RequestBody EmailRequest emailRequest) {
        return pouzivatelService.resendVerificationEmail(emailRequest.getEmail());
    }


    @GetMapping("/pouzivatel-udaje")
    public ResponseEntity<PouzivatelResponse> vratPouzivatelUdaje(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String token = null;

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("JWT")) {
                token = cookie.getValue();
                break;
            }
        }

        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        PouzivatelResponse pouzivatelResponse = pouzivatelService.getPozivatelZTokena(token);

        if (pouzivatelResponse == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok().body(pouzivatelResponse);
    }

    @GetMapping("/get-all-users")
    public List<Pouzivatel> getAllUsers() {
        return pouzivatelService.vratVsetkychPouzivatelov();
    }

    @PutMapping("/update-rola")
    public ResponseEntity<?> update(@RequestBody Map<UUID, Rola> userRoles) {
        if (!userRoles.isEmpty()) {
            userRoles.forEach((k, v) -> pouzivatelService.updateRola(k, v));
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/update-pouzivatel")
    public ResponseEntity<?> updatePouzivatel(@RequestBody AktualizujPouzivatelRequest request) {
        return pouzivatelService.updatePouzivatel(request);
    }

    @PostMapping("/get-pouzivatel")
    private ResponseEntity<?> getPouzivatel(@RequestBody IdPouzivatelRequest request) {
        return pouzivatelService.getPouzivatel(request);
    }

    @GetMapping("/pouzivatel-all-udaje")
    public ResponseEntity vratPouzivatelUdajeVsetky(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String token = null;

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("JWT")) {
                token = cookie.getValue();
                break;
            }
        }

        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        PouzivatelResponse pouzivatelResponse = pouzivatelService.getPozivatelZTokena(token);

        if (pouzivatelResponse == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String id =jwtUtil.extractID(token);

        return pouzivatelService.getPouzivatel(UUID.fromString(id));
    }
}