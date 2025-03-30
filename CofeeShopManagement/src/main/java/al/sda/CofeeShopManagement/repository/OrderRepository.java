package al.sda.CofeeShopManagement.repository;

import al.sda.CofeeShopManagement.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Orders, Long> {
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    List<Orders> findTop5ByOrderByCreatedAtDesc();
    List<Orders> findByStatus(String status);
    List<Orders> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    List<Orders> findByStatusAndCreatedAtBetween(String status, LocalDateTime start, LocalDateTime end);
}
