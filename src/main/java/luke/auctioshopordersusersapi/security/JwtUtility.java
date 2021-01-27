package luke.auctioshopordersusersapi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import luke.auctioshopordersusersapi.user.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * This class creates JSON Web Token during user login. It uses the Authentication object to set claims like
 * - subject, credentials and authorities.
 *
 * It also validates JSON Web token send by the client for:
 * - subject (user username)
 * - credentials (password)
 * - authorities (admin, user authorities etc..)
 *
 * It parses the token for claims ane extracts this values as the method names say.
 */
@Service
public class JwtUtility {

    @Value("${shop.token}")
    private String SECRET_KEY;

    public String generateJSONToken(Authentication authentication) {
        Claims claims = generateClaims(authentication);
        return createToken(authentication, claims);
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

    private String createToken(Authentication authentication, Claims claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(authentication.getName())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 24)))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    // validating methods:
    public String extractSubject(String token) {
        return extractClaim(token).getSubject();
    }

    public String extractCredentials(String token){
        return extractClaim(token).get("credentials", String.class);
    }

    public Set<GrantedAuthority> extractAuthorities(String token){
        String[] scope = extractClaim(token)
                .get("authority", String.class)
                .split(",");

        return Arrays.stream(scope)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    private Claims extractClaim(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }
}
