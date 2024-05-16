package sellik.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import sellik.models.UserDetailsImpl;

import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.lifetime}")
    private int lifetime;

    public String generateToken(String subject) {
        return Jwts.builder()
                .subject(subject)
                .expiration(new Date((new Date()).getTime() + 1 * lifetime))
                .issuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public String generateRefreshToken(String subject) {;
        return Jwts.builder()
                .subject(subject)
                .expiration(new Date((new Date()).getTime() + 2 * lifetime))
                .issuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public String getUsername(String token) {
        return Jwts.parser().setSigningKey(secret).build().parseSignedClaims(token).getBody().getSubject();
    }
}
