package luke.auctioshopordersusersapi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import luke.auctioshopordersusersapi.user.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.stream.Collectors;


@Service
public class GenerateJwtUtil {

    @Value("${shop.token}")
    private String SECRET_KEY;

    public String generateJSONToken(User user, Authentication authentication) {
        Claims claims = generateClaims(authentication);
        return createToken(user.getUsername(), claims);
    }

    private Claims generateClaims(Authentication authentication){
        String authorities = getAuthorities(authentication);
        Claims claims = Jwts.claims();
        claims.put("authority", authorities);
        claims.put("credentials", authentication.getCredentials().toString());
        return claims;
    }

    private String getAuthorities(Authentication authentication) {
        return authentication
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }

    private String createToken(String username, Claims claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 12)))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }
}
