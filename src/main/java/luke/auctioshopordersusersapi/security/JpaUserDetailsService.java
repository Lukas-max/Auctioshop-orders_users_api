package luke.auctioshopordersusersapi.security;

import luke.auctioshopordersusersapi.user.model.User;
import luke.auctioshopordersusersapi.user.service.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class JpaUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public JpaUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalOfUser = userRepository.findByUsername(username);

        if (optionalOfUser.isEmpty())
            throw new UsernameNotFoundException("Nie znaleziono w bazie użytkownika: " + username);
        User user = optionalOfUser.get();

        return new org.springframework.security.core.userdetails
                .User(user.getUsername(), user.getPassword(), getUserAuthorities(user));
    }

    private Collection<GrantedAuthority> getUserAuthorities(User user) {
        return user.getRoles()
                .stream()
                .map(auth -> new SimpleGrantedAuthority(auth.getRole().toString()))
                .collect(Collectors.toSet());
    }
}
