import model.dto.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import service.CategoryService;
import service.ProductService;
import service.UserService;
import util.XmlParser;

import javax.xml.bind.JAXBException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

@Component
public class CommandRunner implements CommandLineRunner {

    private static final String FILE_PATH_CATEGORY = "XMLExercise/src/main/resources/files/categories.xml";
    private static final String FILE_PATH_USERS = "XMLExercise/src/main/resources/files/users.xml";
    private static final String FILE_PATH_PRODUCTS = "XMLExercise/src/main/resources/files/products.xml";
    private static final String FILES_OUTPUT_DIRECTORY = "XMLExercise/src/main/resources/files/output";
    private static final String PRODUCTS_IN_RANGE_FILE = "/products-in-range.xml";
    private static final String SOLD_PRODUCTS = "/users-sold-products.xml";
    private XmlParser xmlParser;
    private CategoryService categoryService;
    private UserService userService;
    private ProductService productService;
    private BufferedReader bufferedReader;

    public CommandRunner(XmlParser xmlParser,
                         CategoryService categoryService,
                         UserService userService, ProductService productService) {
        this.xmlParser = xmlParser;
        this.categoryService = categoryService;
        this.userService = userService;
        this.productService = productService;
        this.bufferedReader = new BufferedReader(new InputStreamReader(System.in));
    }

    public CommandRunner() {
    }

    @Override
    public void run(String... args) throws Exception {
        //seedData();
        System.out.println("Enter exercise number: ");
        int exNumber = Integer.parseInt(bufferedReader.readLine());
        switch (exNumber) {
            case 1 -> productsInRange();
            case 2 -> soldProducts();
        }

    }

    private void soldProducts() throws JAXBException {
        UserViewRootDto userViewRootDtos = userService.findUsersWithMoreThanOneSoldProduct();
        xmlParser.writeToFile(FILES_OUTPUT_DIRECTORY + SOLD_PRODUCTS, userViewRootDtos);

    }

    private void productsInRange() throws JAXBException {
        ProductViewRootDto productViewRootDto = productService.findProductInRange();
        xmlParser.writeToFile(FILES_OUTPUT_DIRECTORY + PRODUCTS_IN_RANGE_FILE, productViewRootDto);
    }

    private void seedData() throws JAXBException, FileNotFoundException {
        if (categoryService.getEntityCount() == 0) {
            CategoryRootSeedDto categoryRootSeedDto = xmlParser.fromFile(FILE_PATH_CATEGORY, CategoryRootSeedDto.class);
            categoryService.seedCategories(categoryRootSeedDto.getCategories());
        }

        if (userService.getEntityCount() == 0) {
            UserRootSeedDto userRootSeedDto = xmlParser.fromFile(FILE_PATH_USERS, UserRootSeedDto.class);
            userService.seedUsers(userRootSeedDto.getUsers());
        }

        if (productService.getCount() == 0) {
            ProductRootSeedDto productRootSeedDto = xmlParser.fromFile(FILE_PATH_PRODUCTS, ProductRootSeedDto.class);
            productService.seedProducts(productRootSeedDto.getProducts());

        }
    }
}