package luke.auctioshopordersusersapi.user.service;

import luke.auctioshopordersusersapi.user.enums.ShopRole;
import luke.auctioshopordersusersapi.user.model.Role;
import luke.auctioshopordersusersapi.user.model.User;
import luke.auctioshopordersusersapi.user.model.UserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @BeforeEach
    public void setupMocks(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addUserShouldWorkSimple(){
        //given
        UserRequest userRequest = createUserRequest();
        given(userRepository.findByUsername(userRequest.getUsername())).willReturn(Optional.empty());
        given(userRepository.findByEmail(userRequest.getEmail())).willReturn(Optional.empty());
        given(roleRepository.findById(2L)).willReturn(Optional.of(createRole()));

        given(passwordEncoder.encode(userRequest.getPassword())).willReturn("Encoded:" + userRequest.getPassword());
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        //when
        userServiceImpl.addUser(userRequest);

        //then
        then(userRepository).should(times(1)).save(userCaptor.capture());

        assertAll(
                () -> assertThat(userRequest.getId(), equalTo(userCaptor.getValue().getId())),
                () -> assertThat(userCaptor.getValue().getId(), is(nullValue())),
                () -> assertThat(userRequest.getUsername(), equalTo(userCaptor.getValue().getUsername())),
                () -> assertThat(userRequest.getEmail(), equalTo(userCaptor.getValue().getEmail())),
                () -> assertThat(userRequest.getPassword(), not(equalTo(userCaptor.getValue().getPassword()))),
                () -> assertThat(userCaptor.getValue().getPassword(), is("Encoded:" + userRequest.getPassword()))
        );
    }

    /**
     * For .getUserByUsername(String username) ->
     */
    @Test
    void getUserByUsernameShouldThrowExceptionIfNoUserFound(){
        //given
        String username = "MyUser";
        given(userRepository.findByUsername(username)).willReturn(Optional.empty());

        //when
        //then
        ResponseStatusException e = assertThrows(ResponseStatusException.class,
                () -> userServiceImpl.getUserByUsername(username));

        assertThat(e.getStatus(), is(HttpStatus.NOT_FOUND));
        assertThat(e.getReason(), equalTo("Nie znaleziono w bazie użytkownika o nazwie: " + username));
    }

    /**
     * 3 methods below test if UserService().validateRegisterData throw exception when needed.
     */
    @Test
    void validateRegisterDataShouldThrowExceptionIfUserIdNotNull(){
        //given
        UserRequest userRequest = createUserRequest();
        userRequest.setId(2L);

        //when
        //then
        ResponseStatusException e = assertThrows(ResponseStatusException.class,
                () -> userServiceImpl.validateRegisterData(userRequest));

        assertThat(e.getStatus(), is(HttpStatus.NOT_ACCEPTABLE));
        assertThat(e.getReason(), equalTo("Użytkownik z ustawionym ID nie może być zapisany w bazie."));
    }

    @Test
    void validateRegisterDataShouldThrowExceptionIfUsernameAlreadyExists(){
        //given
        UserRequest userRequest = createUserRequest();
        User user = getUserOneFromDatabase();
        given(userRepository.findByUsername(user.getUsername())).willReturn(Optional.of(user));

        //when
        //then
        ResponseStatusException e = assertThrows(ResponseStatusException.class,
                () -> userServiceImpl.validateRegisterData(userRequest));

        assertThat(e.getStatus(), is(HttpStatus.BAD_REQUEST));
        assertThat(e.getReason(), equalTo("Użytkownik z takim imieniem już istnieje w bazie."));
    }

    @Test
    void validateRegisterDataShouldThrowExceptionIfEmailAlreadyExists(){
        //given
        UserRequest userRequest = createUserRequest();
        User user = getUserTwoFromDatabase();
        given(userRepository.findByEmail(user.getEmail())).willReturn(Optional.of(user));

        //when
        //then
        ResponseStatusException e = assertThrows(ResponseStatusException.class,
                () -> userServiceImpl.validateRegisterData(userRequest));

        assertThat(e.getStatus(), is(HttpStatus.BAD_REQUEST));
        assertThat(e.getReason(), equalTo("Taki email już istnieje w bazie"));
    }

    /**
     * Helper methods:
     */
    private UserRequest createUserRequest(){
        UserRequest user = new UserRequest();
        user.setUsername("Jacek");
        user.setPassword("NoweHaslo");
        user.setEmail("mail@o2.pl");
        user.getRoles().add(createRole());
        return user;
    }

    private User getUserOneFromDatabase(){
        User user = new User();
        user.setId(10L);
        user.setUsername("Jacek");
        user.setPassword("JegoHaslo");
        user.setEmail("innyEmail@gmail.com");
        user.getRoles().add(createRole());
        return user;
    }

    private User getUserTwoFromDatabase(){
        User user = new User();
        user.setId(10L);
        user.setUsername("Inny username");
        user.setPassword("JegoHaslo");
        user.setEmail("mail@o2.pl");
        user.getRoles().add(createRole());
        return user;
    }

    private Role createRole(){
        Role userRole1 = new Role();
        userRole1.setRole(ShopRole.ROLE_USER);
        return userRole1;
    }
}
