package service;

import model.dto.CategoryProductStatsDto;
import model.dto.ProductInRangeDto;
import java.util.List;


import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public interface ProductService {

    void seedProducts() throws IOException;

    List<ProductInRangeDto> findAllProductsInRangeOrderByPrice(BigDecimal lower, BigDecimal upper);

    List<CategoryProductStatsDto> getCategoryStats();
}