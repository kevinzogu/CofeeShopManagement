package al.sda.CofeeShopManagement.repository;

import al.sda.CofeeShopManagement.entity.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Products, Long> {
    List<Products> findByCategoryId(Long categoryId);
    
    long countByCategoryId(Long categoryId);
    
    @Query("SELECT p, COUNT(oi.id) as soldCount FROM Products p " +
            "JOIN OrderItem oi ON oi.productId.id = p.id " +
            "JOIN Orders o ON oi.order.id = o.id " +
            "WHERE o.createdAt BETWEEN :start AND :end " +
            "GROUP BY p.id ORDER BY soldCount DESC")
    List<Object[]> findTopSellingProducts(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
