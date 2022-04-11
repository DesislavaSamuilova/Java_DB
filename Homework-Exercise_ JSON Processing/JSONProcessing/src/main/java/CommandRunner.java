import com.google.gson.Gson;
import model.dto.CategoryProductStatsDto;
import model.dto.ProductInRangeDto;
import model.dto.UserSoldDto;
import model.dto.UserWithSoldProductsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import service.CategoryService;
import service.ProductService;
import service.UserService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

@Component
public class CommandRunner implements CommandLineRunner {

    private static final String OUTPUT_FILES_PATH = "JSONProcessing/src/main/resources/files/out/";
    private static final String PRODUCT_IN_RANGE = "product-in-range.json";
    private static final String USERS_SOLD_PRODUCTS = "users-sold-products.json";
    private static final String PRODUCTS_STATS = "product-stats.json";

    private final CategoryService categoryService;
    private final UserService userService;
    private final ProductService productService;
    private final BufferedReader bufferedReader;
    private final Gson gson;

    @Autowired
    public CommandRunner(CategoryService categoryService, UserService userService,
                                ProductService productService, Gson gson) {
        this.categoryService = categoryService;
        this.userService = userService;
        this.productService = productService;
        this.gson = gson;
        this.bufferedReader = new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    public void run(String... args) throws Exception {

        System.out.println("Enter exercise number: ");
        int exerciseNumber = Integer.parseInt(bufferedReader.readLine());

        switch (exerciseNumber) {
            case 1 -> productsInRange();
            case 2 -> soldProducts();
            case 3 -> productsStatistics();
            case 4 -> usersAndProducts();
        }

    }

    private void usersAndProducts() {
        List<UserWithSoldProductsDto> userWithSoldProductsDto = this.userService.findAllUsersWithSoldProducts();
        String content = gson.toJson(userWithSoldProductsDto);

        System.out.println(content);
    }

    private void productsStatistics() throws IOException {

        List<CategoryProductStatsDto> categoryStats = this.productService.getCategoryStats();

        String content = gson.toJson(categoryStats);

        writeToFiles(OUTPUT_FILES_PATH + PRODUCTS_STATS, content);
    }

    private void soldProducts() throws IOException {

        List<UserSoldDto> userSoldDto = this.userService.findAllUsersWithMoreThanOneSoldProducts();

        String content = gson.toJson(userSoldDto);

        writeToFiles(OUTPUT_FILES_PATH + USERS_SOLD_PRODUCTS, content);


    }

    private void productsInRange() throws IOException {

        List<ProductInRangeDto> productDto = this.productService.
                findAllProductsInRangeOrderByPrice(BigDecimal.valueOf(500L), BigDecimal.valueOf(1000L));

        String content = gson.toJson(productDto);

        writeToFiles(OUTPUT_FILES_PATH + PRODUCT_IN_RANGE, content);
    }

    private void writeToFiles(String filePath, String content) throws IOException {

        Files.write(Path.of(filePath), Collections.singleton(content));
    }


    private void seedData() throws IOException {
        this.categoryService.seedCategories();
        this.userService.seedUsers();
        this.productService.seedProducts();
    }
}