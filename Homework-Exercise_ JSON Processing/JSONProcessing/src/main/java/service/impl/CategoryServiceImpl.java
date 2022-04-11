package service.impl;

import com.google.gson.Gson;
import model.dto.CategorySeedDto;
import model.entities.Category;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import repository.CategoryRepository;
import service.CategoryService;
import util.ValidationUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;


@Service
public class CategoryServiceImpl implements CategoryService {

    private final String CATEGORY_FILE_PATH = "JSONProcessing/src/main/resources/files/categories.json";
    private final Gson gson;
    private final ValidationUtil validationUtil;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public CategoryServiceImpl(Gson gson, ValidationUtil validationUtil, CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.gson = gson;
        this.validationUtil = validationUtil;
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public void seedCategories() throws IOException {

        if (categoryRepository.count() > 0) {
            return;
        }

        String content = Files.readString(Path.of(CATEGORY_FILE_PATH));

        CategorySeedDto[] categorySeedDto = gson.fromJson(content, CategorySeedDto[].class);

        Arrays.stream(categorySeedDto).filter(validationUtil::isValid)
                .map(categorySeedDto1 -> modelMapper.map(categorySeedDto1, Category.class)).forEach(categoryRepository::save);

    }

    @Override
    public Set<Category> findRandomCategories() {

        Set<Category> categories = new HashSet<>();
        int categoryCount = ThreadLocalRandom.current().nextInt(1, 3);
        long totalCategories = categoryRepository.count();

        for (int i = 0; i < categoryCount; i++) {
            long randomId = ThreadLocalRandom.current().nextLong(1, totalCategories + 1);

            categories.add(categoryRepository.findById(randomId).orElse(null));
        }

        return categories;
    }

}