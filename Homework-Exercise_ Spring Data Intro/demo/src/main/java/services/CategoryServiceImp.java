package services;

import entities.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repositories.CategoryRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Service
public class CategoryServiceImp implements CategoryService {

    private CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImp(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Set<Category> getRandomCategories() {
        long size = categoryRepository.count();
        Random random = new Random();

        int categoriesCount = random.nextInt((int) size) + 1;

        Set<Integer> categoryId = new HashSet<>();

        for (int i = 0; i < categoriesCount; i++) {
            int nextId = random.nextInt((int) size) + 1;

            categoryId.add(nextId);
        }


        List<Category> allById = this.categoryRepository.findAllById(categoryId);

        return new HashSet<>(allById);
    }
}