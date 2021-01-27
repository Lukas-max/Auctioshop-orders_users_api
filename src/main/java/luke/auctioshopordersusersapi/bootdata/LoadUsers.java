package luke.auctioshopordersusersapi.bootdata;

import luke.auctioshopordersusersapi.user.enums.ShopRole;
import luke.auctioshopordersusersapi.user.model.Role;
import luke.auctioshopordersusersapi.user.model.User;
import luke.auctioshopordersusersapi.user.service.RoleRepository;
import luke.auctioshopordersusersapi.user.service.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

//@Component
public class LoadUsers implements CommandLineRunner {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value("${shop.admin.username}")
    private String adminUsername;
    @Value("${shop.admin.password}")
    private String adminPassword;

    public LoadUsers(UserRepository userRepository,
                     RoleRepository roleRepository,
                     PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        Role adminRole = new Role();
        adminRole.setRole(ShopRole.ADMIN);
        roleRepository.save(adminRole);

        Role userRole1 = new Role();
        userRole1.setRole(ShopRole.USER);
        roleRepository.save(userRole1);

        User user = new User();
        user.setUsername("jurek");
        user.setPassword(passwordEncoder.encode("user"));
        user.setEmail("jurek@interia.pl");
        user.getRoles().add(userRole1);
        userRepository.save(user);
        logger.info("Załadowano do bazy użytkowników i rolę użytkownika");

        User adminUser = new User();
        adminUser.setUsername(adminUsername);
        adminUser.setPassword(passwordEncoder.encode(adminPassword));
        adminUser.setEmail("abc@o2.pl");
        adminUser.getRoles().add(adminRole);
        userRepository.save(adminUser);
        logger.info("Załadowano do bazy admina i rolę admin");
    }
}
