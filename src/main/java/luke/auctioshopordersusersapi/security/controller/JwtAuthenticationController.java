package luke.auctioshopordersusersapi.security.controller;

import luke.auctioshopordersusersapi.security.JwtUtil;
import luke.auctioshopordersusersapi.security.model.AuthenticationRequest;
import luke.auctioshopordersusersapi.security.model.AuthenticationResponse;
import luke.auctioshopordersusersapi.user.model.User;
import luke.auctioshopordersusersapi.user.service.UserServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/login")
public class JwtAuthenticationController {

    private final UserServiceImpl userServiceImpl;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public JwtAuthenticationController(
            UserServiceImpl userServiceImpl,
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil) {
        this.userServiceImpl = userServiceImpl;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public ResponseEntity<AuthenticationResponse> createAuthenticationToken(
            @RequestBody AuthenticationRequest authenticationRequest) {

        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()
                );
        authenticationManager.authenticate(token);

        User user = userServiceImpl.getUserByUsername(authenticationRequest.getUsername());
        final String jwtToken = jwtUtil.generateToken(user);

        Set<String> roles = user.getRoles()
                .stream()
                .map(r -> r.getRole().toString())
                .collect(Collectors.toSet());

        return ResponseEntity.ok(new AuthenticationResponse(
                jwtToken,
                user.getId(),
                user.getUsername(),
                roles));
    }
}
