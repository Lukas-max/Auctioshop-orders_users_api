package luke.auctioshopordersusersapi.security.user_details;

import luke.auctioshopordersusersapi.user.model.User;
import luke.auctioshopordersusersapi.user.service.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class JpaUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public JpaUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isPresent()){
            return user.map(GetUserDetails::new).get();
//            albo:
//            return new GetUserDetails(user.get());

        }else {
            throw new UsernameNotFoundException("Not found: " + username);
        }
    }
}
