package luke.auctioshopordersusersapi.security.controller;

import luke.auctioshopordersusersapi.security.JwtUtility;
import luke.auctioshopordersusersapi.security.model.AuthenticationRequest;
import luke.auctioshopordersusersapi.security.model.AuthenticationResponse;
import luke.auctioshopordersusersapi.user.model.User;
import luke.auctioshopordersusersapi.user.service.UserServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/login")
public class JwtAuthenticationController {

    private final UserServiceImpl userServiceImpl;
    private final AuthenticationManager authenticationManager;
    private final JwtUtility jwtUtility;
    private final PasswordEncoder encoder;

    public JwtAuthenticationController(
            UserServiceImpl userServiceImpl,
            AuthenticationManager authenticationManager,
            JwtUtility jwtUtility,
            PasswordEncoder encoder) {
        this.userServiceImpl = userServiceImpl;
        this.authenticationManager = authenticationManager;
        this.jwtUtility = jwtUtility;
        this.encoder = encoder;
    }

    @PostMapping
    public ResponseEntity<AuthenticationResponse> createAuthenticationToken(
            @RequestBody AuthenticationRequest authenticationRequest) {

        User user = userServiceImpl.getUserByUsername(authenticationRequest.getUsername());
        Collection<GrantedAuthority> authorities = getUserAuthorities(user);

        if (!encoder.matches(authenticationRequest.getPassword(), user.getPassword()))
            throw new BadCredentialsException("Hasło albo nazwa użytkownika nie prawidłowe.");

        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUsername(),
                        authenticationRequest.getPassword(),
                        authorities
                );

        authenticationManager.authenticate(token);
        final String jwtToken = jwtUtility.generateJSONToken(token);

        return ResponseEntity.ok(new AuthenticationResponse(
                jwtToken,
                user.getId(),
                user.getUsername(),
                authorities));
    }

    /**
     *
     * @param user -> User entity class. Contains id, username, password and email.
     * @return Collection of GrantedAuthorities from Role entity ShopRole class.
     */
    private Collection<GrantedAuthority> getUserAuthorities(User user) {
        return user.getRoles()
                .stream()
                .map(auth -> new SimpleGrantedAuthority(auth.getRole().toString()))
                .collect(Collectors.toSet());
    }
}
