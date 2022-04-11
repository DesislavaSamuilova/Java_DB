package service.impl;

import com.google.gson.Gson;
import model.dto.CategoryProductStatsDto;
import model.dto.ProductInRangeDto;
import model.dto.ProductSeedDto;
import model.entities.Product;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import repository.ProductRepository;
import service.CategoryService;
import service.ProductService;
import service.UserService;
import util.ValidationUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final String PRODUCTS_FILE_PATH = "JSONProcessing/src/main/resources/files/products.json";
    private final Gson gson;
    private final ModelMapper modelMapper;
    private final ProductRepository productRepository;
    private final ValidationUtil validationUtil;
    private final UserService userService;
    private final CategoryService categoryService;

    public ProductServiceImpl(Gson gson, ModelMapper modelMapper,
                              ProductRepository productRepository,
                              ValidationUtil validationUtil, UserService userService, CategoryService categoryService) {
        this.gson = gson;
        this.modelMapper = modelMapper;
        this.productRepository = productRepository;
        this.validationUtil = validationUtil;
        this.userService = userService;
        this.categoryService = categoryService;
    }

    @Override
    public void seedProducts() throws IOException {

        if (productRepository.count() > 0) {
            return;
        }

        String productContent = Files.readString(Path.of(PRODUCTS_FILE_PATH));

        ProductSeedDto[] productSeedDto = gson.fromJson(productContent, ProductSeedDto[].class);

        Arrays.stream(productSeedDto).filter(validationUtil::isValid)
                .map(productSeedDtos -> {

                    Product product = modelMapper.map(productSeedDtos, Product.class);
                    product.setSeller(userService.findRandomUser());

                    if (product.getPrice().compareTo(BigDecimal.valueOf(900L)) > 0) {
                        product.setBuyer(userService.findRandomUser());
                    }
                    product.setCategories(categoryService.findRandomCategories());

                    return product;
                }).forEach(productRepository::save);
    }

    @Override
    public List<ProductInRangeDto> findAllProductsInRangeOrderByPrice(BigDecimal lower, BigDecimal upper) {

        return productRepository.findAllByPriceBetweenAndBuyerIsNullOrderByPriceDesc(lower, upper)
                .stream().map(product -> {
                    ProductInRangeDto productInRangeDto = modelMapper.map(product, ProductInRangeDto.class);

                    productInRangeDto.setSeller(String.format("%s %s",
                            product.getSeller().getFirstName(),
                            product.getSeller().getLastName()));

                    return productInRangeDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryProductStatsDto> getCategoryStats() {
        return productRepository.getCategoryStats();
    }
}