package services;


import entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repositories.AuthorRepository;
import repositories.BookRepository;
import repositories.CategoryRepository;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SeedServiceImp implements SeedService {

    private static final String AUTHORS_FILE_PATH = "SpringDataExercise/src/main/resources/files/authors.txt";
    private static final String CATEGORIES_FILE_PATH = "SpringDataExercise/src/main/resources/files/categories.txt";
    private static final String BOOKS_FILE_PATH = "SpringDataExercise/src/main/resources/files/books.txt";

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private CategoryService categoryService;

    @Override
    public void seedAuthors() throws IOException {
        Files.readAllLines(Path.of(AUTHORS_FILE_PATH))
                .stream().filter(s -> !s.isBlank())
                .map(s -> s.split(" "))
                .map(names -> new Author(names[0], names[1]))
                .forEach(authorRepository::save);
    }

    @Override
    public void seedCategories() throws IOException {
        Files.readAllLines(Path.of(CATEGORIES_FILE_PATH))
                .stream().filter(s -> !s.isBlank())
                .map(Category::new)
                .forEach(categoryRepository::save);
    }

    @Override
    public void seedBooks() throws IOException {
        Files.readAllLines(Path.of(BOOKS_FILE_PATH))
                .stream().filter(b -> !b.isBlank())
                .map(this::getBookObject)
                .forEach(bookRepository::save);
    }

    private Book getBookObject(String line) {
        String[] bookParts = line.split(" ");

        int index = Integer.parseInt(bookParts[0]);
        EditionType type = EditionType.values()[index];

        LocalDate date = LocalDate.parse(bookParts[1], DateTimeFormatter.ofPattern("d/M/yyyy"));

        int copies = Integer.parseInt(bookParts[2]);

        BigDecimal price = new BigDecimal(bookParts[3]);

        int ageRestrictionIndex = Integer.parseInt(bookParts[4]);

        AgeRestriction ageRestriction = AgeRestriction.values()[ageRestrictionIndex];

        String tittle = Arrays.stream(bookParts).skip(5).collect(Collectors.joining(" "));

        Author author = authorService.getRandomAuthor();

        Set<Category> categories = categoryService.getRandomCategories();

        return new Book(tittle, type, price, copies, date, ageRestriction, author, categories);

    }
}