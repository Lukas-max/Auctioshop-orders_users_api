package luke.auctioshopordersusersapi.user.service;

import luke.auctioshopordersusersapi.user.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
