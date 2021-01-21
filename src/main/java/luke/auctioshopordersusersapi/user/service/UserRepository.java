package luke.auctioshopordersusersapi.user.service;

import luke.auctioshopordersusersapi.user.enums.ShopRole;
import luke.auctioshopordersusersapi.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {

    @Query(name = "User.findAllWithoutAdmin")
    Page<User> findAllWithoutAdmin(ShopRole role, Pageable pageable);

    @EntityGraph(value = "User.fetch.roles")
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);
}
