package service.impl;

import model.dto.ProductSeedDto;
import model.dto.ProductViewRootDto;
import model.dto.ProductWithSellerDto;
import model.entity.Product;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import repository.ProductRepository;
import service.CategoryService;
import service.ProductService;
import service.UserService;
import util.ValidationUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final UserService userService;
    private final CategoryService categoryService;

    public ProductServiceImpl(ProductRepository productRepository,
                              ModelMapper modelMapper, ValidationUtil validationUtil,
                              UserService userService, CategoryService categoryService) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.userService = userService;
        this.categoryService = categoryService;
    }

    @Override
    public long getCount() {
        return productRepository.count();
    }

    @Override
    public void seedProducts(List<ProductSeedDto> products) {

        products.stream().filter(validationUtil::isValid).map(productSeedDto -> {

            Product product = modelMapper.map(productSeedDto, Product.class);
            product.setSeller(userService.getRandomUser());

            if (product.getPrice().compareTo(BigDecimal.valueOf(800L)) > 0) {
                product.setBuyer(userService.getRandomUser());
            }
            product.setCategories(categoryService.getRandomCategories());

            return product;

        }).forEach(productRepository::save);
    }

    @Override
    public ProductViewRootDto findProductInRange() {

        ProductViewRootDto productViewRootDto = new ProductViewRootDto();

        productViewRootDto.setProducts(productRepository.findAllByPriceBetweenAndBuyerIsNull(BigDecimal.valueOf(500L),
                BigDecimal.valueOf(1000L)).stream().map(product -> {

            ProductWithSellerDto productWithSellerDto = modelMapper.map(product, ProductWithSellerDto.class);

            String name = product.getSeller().getFirstName() == null ? "" : product.getSeller().getFirstName();

            productWithSellerDto.setSeller(String.format("%s %s", name,
                    product.getSeller().getLastName()));

            return productWithSellerDto;

        }).collect(Collectors.toList()));

        return productViewRootDto;
    }
}