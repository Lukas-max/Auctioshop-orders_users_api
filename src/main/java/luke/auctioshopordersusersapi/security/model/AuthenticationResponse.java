package luke.auctioshopordersusersapi.security.model;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;


public class AuthenticationResponse {
    private final String jwt;
    private final Long userId;
    private final String username;
    private final Collection<GrantedAuthority> authorities;

    public AuthenticationResponse(String jwt, Long userId, String username, Collection<GrantedAuthority> authorities) {
        this.jwt = jwt;
        this.userId = userId;
        this.username = username;
        this.authorities = authorities;
    }

    public String getJwt() {
        return jwt;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public Collection<GrantedAuthority> getAuthorities() {
        return authorities;
    }
}
