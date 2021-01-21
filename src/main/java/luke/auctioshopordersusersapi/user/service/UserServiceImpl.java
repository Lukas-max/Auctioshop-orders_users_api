package luke.auctioshopordersusersapi.user.service;

import luke.auctioshopordersusersapi.order.model.entity.CustomerOrder;
import luke.auctioshopordersusersapi.order.service.CustomerOrderRepository;
import luke.auctioshopordersusersapi.user.enums.ShopRole;
import luke.auctioshopordersusersapi.user.model.Role;
import luke.auctioshopordersusersapi.user.model.User;
import luke.auctioshopordersusersapi.user.model.UserRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomerOrderRepository orderRepository;

    public UserServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            CustomerOrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.orderRepository = orderRepository;
    }

    /**
     * @return All users in database not containing ROLE_ADMIN.
     */
    @Override
    public Page<User> getAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAllWithoutAdmin(ShopRole.ROLE_ADMIN, pageable);
        if (users.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie ma użytkowników w bazie danych");

        return  users;
    }

    /**
     * Returns the page of User data with order and address.
     */
    @Override
    public Page<CustomerOrder> findUserAndOrderWithDataByUserId(Long id, Pageable pageable){
        return orderRepository.getCustomerOrderByUserId(id, pageable);
    }

    /**
     * @return User from database. Find by injected username.
     */
    @Override
    public User getUserByUsername(String username){
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Nie znaleziono w bazie użytkownika o nazwie: " + username);

        return userOptional.get();
    }

    @Override
    public User getUserById(Long id){
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Nie znaleziono w bazie użytkownika o id: " + id);

        return userOptional.get();
    }

    /**
     *
     * Deletes User, all orders(CustomerOrder entity) that belong to User and address (Customer entity)
     */
    @Override
    public void deleteUserAndAllUserDataByUserId(Long id){
        getUserById(id);

        orderRepository.deleteCustomerFromCustomerOrderByUserId(id);
        orderRepository.deleteCustomerOrderByUserId(id);
        userRepository.deleteById(id);
    }

    /**
     *
     * Will:
     *   - validate data from user, using helper methods
     *   - add ROLE_USER to new user
     *   - encrypt the user send password
     *   - persist user in database
     */
    @Override
    public User addUser(UserRequest userRequest) {
        validateRegisterData(userRequest);

        Role role = getUserRole();
        User user = new User(userRequest);
        user.getRoles().add(role);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    /**
     *
     * Will throw exception if:
     * a) user has set ID
     * b) username already exists in database
     * c) email already exists in database
     */
    protected void validateRegisterData(UserRequest request) {
        if (request.getId() != null)
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,
                    "Użytkownik z ustawionym ID nie może być zapisany w bazie.");

        Optional<User> optionalUser1 = userRepository.findByUsername(request.getUsername());
        if (optionalUser1.isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Użytkownik z takim imieniem już istnieje w bazie.");

        Optional<User> optionalUser2 = userRepository.findByEmail(request.getEmail());
        if (optionalUser2.isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Taki email już istnieje w bazie");
    }

    /**
     *
     * @return  ROLE_ADMIN if ShopRole.ROLE_ADMIN is passed. Otherwise return ROLE_USER.
     */
    private Role getUserRole(){
        Optional<Role> optional = roleRepository.findById(2L);

        if (optional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not find user roles");
        }
        return optional.get();
    }
}
