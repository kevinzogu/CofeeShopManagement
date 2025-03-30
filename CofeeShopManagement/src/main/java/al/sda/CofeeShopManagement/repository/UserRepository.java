package al.sda.CofeeShopManagement.repository;

import al.sda.CofeeShopManagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    List<User> findByRolesContaining(String role);
    long countByLastLoginAfter(LocalDateTime localDateTime);
}
