package repository;

import model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE (SELECT COUNT(p) FROM Product p " +
            " WHERE p.seller.id = u.id) > 0 ORDER BY u.lastName, u.firstName")
    List<User> findAllByUsersWithMoreThanOneSoldProductsOrderByLastNameThenFirstName();

    @Query("SELECT DISTINCT  u FROM User u JOIN Product p ON u.id = p.seller.id " +
            "WHERE p.buyer.id IS NOT NULL AND u.soldProducts.size > 0 " +
            "ORDER BY u.soldProducts.size desc ,u.lastName")
    List<User> findAllByUsersWithMoreThanOneSoldProductOrderByProductsSold();
}