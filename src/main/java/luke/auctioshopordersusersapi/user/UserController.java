package luke.auctioshopordersusersapi.user;

import luke.auctioshopordersusersapi.order.model.entity.CustomerOrder;
import luke.auctioshopordersusersapi.user.model.User;
import luke.auctioshopordersusersapi.user.model.UserRequest;
import luke.auctioshopordersusersapi.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasAuthority('ADMIN')")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<Page<User>> getUsers(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {

        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userService.getAllUsers(pageable);
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @GetMapping(path = "/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
    public ResponseEntity<Page<CustomerOrder>> getUserWithAllDataById(
            @PathVariable Long id,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<CustomerOrder> orders = userService.findUserAndOrderWithDataByUserId(id, pageable);
        return ResponseEntity.ok(orders);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable Long id){
        userService.deleteUserAndAllUserDataByUserId(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/register")
    public ResponseEntity<?> addNewUser(@Valid @RequestBody UserRequest userRequest) {
        User savedUser = userService.addUser(userRequest);

        URI locationUri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedUser.getId())
                .toUri();
        return ResponseEntity.created(locationUri).body(savedUser);
    }
}
