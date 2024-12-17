package back_end.audio_video.component;

import back_end.audio_video.details.PouzivatelDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    private SecretKey secretKey;

    public JwtUtil(@Value("${jwt.secret}") String secrete) {
        this.secretKey = new SecretKeySpec(secrete.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS512.getJcaName());
    }

    public String extractMeno(String token) {
        return this.extractClaims(token, claims -> claims.get("meno", String.class));
    }

    public String extractPriezvisko(String token) {
        return this.extractClaims(token, claims -> claims.get("priezvisko", String.class));
    }

    public String extractRola(String token) {
        return this.extractClaims(token, claims -> claims.get("rola", String.class));
    }

    public String extractID(String token) {
        return this.extractClaims(token, claims -> claims.get("id", String.class));
    }

    public String extractEmail(String token) {
        return this.extractClaims(token, claims -> claims.get("email", String.class));
    }

//    public String extractUlica(String token) {
//        return this.extractClaims(token, claims -> claims.get("ulica", String.class));
//    }

    public Date extractExpiration(String token) {
        return this.extractClaims(token, Claims::getExpiration);
    }

    public <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = this.extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(this.secretKey)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new RuntimeException("Nedokazem rozparsovat TOKEN: " + e.getMessage());
        }
    }

    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, String email) {
        final String extractEmail = extractEmail(token);
        return extractEmail.equals(email) && !isTokenExpired(token);
    }

    public String generateToken(PouzivatelDetails pouzivatelDetails) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", pouzivatelDetails.getId());
        claims.put("meno", pouzivatelDetails.getMeno());
        claims.put("priezvisko", pouzivatelDetails.getPriezvisko());
        claims.put("email", pouzivatelDetails.getEmail());
        claims.put("rola", pouzivatelDetails.getRola());
        //SEM DOPLNUJ CLAIMS PRE PUZIVATELA AK TREBA


        return Jwts.builder()
                .setClaims(claims)
                .setSubject(pouzivatelDetails.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 10000 * 60 * 60 * 10))
                .signWith(this.secretKey)
                .compact();
    }
}
