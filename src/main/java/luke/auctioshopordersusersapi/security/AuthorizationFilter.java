package luke.auctioshopordersusersapi.security;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

public class AuthorizationFilter extends BasicAuthenticationFilter {

    private final JwtUtility jwtUtility;

    public AuthorizationFilter(AuthenticationManager authenticationManager, JwtUtility jwtUtility) {
        super(authenticationManager);
        this.jwtUtility = jwtUtility;
    }

    /**
     * This filter method checks if the token send by the client is valid by parsing the token by the
     * ValidateJwtUtility class.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = authHeader.replace("Bearer ", "");
        if (!validateToken(token)){
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authenticationToken = getAuthenticationToken(token);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        chain.doFilter(request, response);
    }

    private boolean validateToken(String token){
        boolean isValid = true;
        String subject = null;
        String credentials = null;
        Set<GrantedAuthority> authorities = null;

        try{
            subject = jwtUtility.extractSubject(token);
            credentials = jwtUtility.extractCredentials(token);
            authorities = jwtUtility.extractAuthorities(token);
        }catch (Exception ex){
            isValid = false;
        }

        if (subject == null || subject.isEmpty() || credentials == null || credentials.isEmpty() || authorities == null)
            isValid = false;

        if (jwtUtility.isTokenExpired(token))
            isValid = false;

        return isValid;
    }

    /**
     *
     * @return UsernamePasswordAuthenticationToken used to set Spring security context.
     */
    private UsernamePasswordAuthenticationToken getAuthenticationToken(String token) {
        return new UsernamePasswordAuthenticationToken(
                jwtUtility.extractSubject(token),
                jwtUtility.extractCredentials(token),
                jwtUtility.extractAuthorities(token));
    }
}
