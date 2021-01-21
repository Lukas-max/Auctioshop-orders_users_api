package luke.auctioshopordersusersapi.user.model;


import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

public class UserRequest {

    private Long id;

    @NotEmpty(message = "Pole nazwa użytkownika musi zawierać wartość.")
    @Size(min = 3, max = 45, message = "Nazwa użytkownika nie może mieć mniej niż 3 znaki i więcej niż 45 znaków")
    private String username;

    @NotEmpty(message = "Pole nazwa użytkownika musi zawierać wartość.")
    @Size(min = 3, max = 250, message = "Hasło nie może mieć mniej niż 3 znaki, a więcej niż 250 znaków")
    private String password;

    @NotEmpty(message = "Pole email nie może pozostać puste.")
    @Pattern(regexp = "^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$", message = "Musisz wpisać poprawny email")
    private String email;
    private Set<Role> roles = new HashSet<>();

    public UserRequest() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "UserRequest{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", roles=" + roles +
                '}';
    }
}
