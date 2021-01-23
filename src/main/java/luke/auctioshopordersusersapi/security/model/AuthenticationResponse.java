package luke.auctioshopordersusersapi.security.model;

import java.util.HashSet;
import java.util.Set;


public class AuthenticationResponse {
    private final String jwt;
    private final Long userId;
    private final String username;
    private final Set<String> roles;

    public AuthenticationResponse(String jwt, Long userId, String username, Set<String> roles) {
        this.jwt = jwt;
        this.userId = userId;
        this.username = username;
        this.roles = new HashSet<>(roles);
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

    public Set<String> getRoles() {
        return roles;
    }
}
