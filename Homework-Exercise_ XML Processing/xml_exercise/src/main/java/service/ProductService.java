package service;

import model.dto.ProductSeedDto;
import model.dto.ProductViewRootDto;

import java.util.List;

public interface ProductService {

    long getCount();

    void seedProducts(List<ProductSeedDto> products);

    ProductViewRootDto findProductInRange();
}