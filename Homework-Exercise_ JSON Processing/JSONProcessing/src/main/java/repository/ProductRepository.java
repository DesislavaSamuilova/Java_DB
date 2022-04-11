package repository;
import model.dto.CategoryProductStatsDto;
import model.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllByPriceBetweenAndBuyerIsNullOrderByPriceDesc(BigDecimal lower, BigDecimal upper);

    @Query("SELECT new model.dto.CategoryProductStatsDto(c.name, COUNT(p), AVG(p.price), " +
            " SUM(p.price)) FROM Product p JOIN p.categories c GROUP BY c")
    List<CategoryProductStatsDto> getCategoryStats();
}