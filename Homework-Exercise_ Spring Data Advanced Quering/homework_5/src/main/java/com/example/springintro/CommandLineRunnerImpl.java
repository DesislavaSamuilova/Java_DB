package com.example.springintro;

import com.example.springintro.model.entity.AgeRestriction;
import com.example.springintro.model.entity.Book;
import com.example.springintro.service.CategoryService;
import com.example.springintro.service.AuthorService;
import com.example.springintro.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class CommandLineRunnerImpl implements CommandLineRunner {

    private final CategoryService categoryService;
    private final AuthorService authorService;
    private final BookService bookService;
    private final BufferedReader bufferedReader;

    @Autowired
    public CommandLineRunnerImpl(CategoryService categoryService,
                                 AuthorService authorService,
                                 BookService bookService, BufferedReader bufferedReader) {
        this.categoryService = categoryService;
        this.authorService = authorService;
        this.bookService = bookService;
        this.bufferedReader = bufferedReader;
    }

    @Override
    public void run(String... args) throws Exception {

        System.out.println("Enter exercise number: ");
        int exerciseNumber = Integer.parseInt(bufferedReader.readLine());
        switch (exerciseNumber) {
            case 1:
                bookTittlesByAgeRestriction();
                break;
            case 2:
                getGoldenBook();
                break;
            case 3:
                getBookByPrice();
                break;
            case 4:
                notReleasedBooks();
                break;
            case 5:
                getBooksBeforeReleaseDate();
                break;
            case 6:
                authorsSearch();
                break;
            case 7:
                bookSearch();
                break;
            case 8:
                findAuthorBooksLastNameStartsWith();
                break;
            case 9:
                countBooks();
            case 10:
                getTotalBookCopies();
                break;
            case 11:
                reducedBook();
                break;
            case 12:
                increaseBookCopies();
                break;
            case 13:
                removeBooks();
                break;
            case 14:
                storedProcedure();
                break;
        }
    }

    private void storedProcedure() throws IOException {
        System.out.println("Enter author name: ");
        String[] authorName = bufferedReader.readLine().split(" ");
        String firstName = authorName[0];
        String lastName = authorName[1];

        int total = this.bookService.findAuthorTotalBooks(firstName, lastName);

        System.out.printf("%s %s has written %d books%n", firstName, lastName, total);

    }

    private void removeBooks() throws IOException {
        System.out.println("Enter amount: ");
        int amount = Integer.parseInt(bufferedReader.readLine());
        int deleted = this.bookService.deleteWithLessCopiesThan(amount);
        System.out.println(deleted + " books were deleted.");
    }

    private void reducedBook() throws IOException {
        System.out.println("Enter book tittle: ");
        String book = bufferedReader.readLine();
        this.bookService.getBookInfo(book).forEach(System.out::println);
    }

    private void increaseBookCopies() throws IOException {
        System.out.println("Enter input: ");
        String date = bufferedReader.readLine();
        int copies = Integer.parseInt(bufferedReader.readLine());
        int updatedBooks = this.bookService.addCopiesToBook(date, copies);

        System.out.printf("%s books are released after %s, so total of %d book copies were added%n",
                updatedBooks, date, copies * updatedBooks);
    }

    private void getTotalBookCopies() {
        this.authorService.findAllAuthorsAndTotalCopies().forEach(System.out::println);
    }

    private void findAuthorBooksLastNameStartsWith() throws IOException {
        System.out.println("Enter string: ");

        String line = bufferedReader.readLine();

        bookService.findAuthorLastnameStartsWith(line).forEach(System.out::println);
    }

    private void countBooks() throws IOException {
        System.out.println("Enter tittle length: ");

        int tittleLength = Integer.parseInt(bufferedReader.readLine());

        System.out.println(bookService.findCountOfBooksWithTittleLongerThan(tittleLength));


    }

    private void bookSearch() throws IOException {
        System.out.println("Enter string: ");

        String line = bufferedReader.readLine();

        bookService.findBooksByContainString(line).forEach(System.out::println);
    }

    private void authorsSearch() throws IOException {
        System.out.println("Enter string: ");

        String line = bufferedReader.readLine();

        authorService.findAllAuthorsWithFirstNameEndWith(line).forEach(System.out::println);
    }

    private void getBooksBeforeReleaseDate() throws IOException {
        System.out.println("Enter release date: ");

        LocalDate localDate = LocalDate.parse(bufferedReader.readLine(), DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        bookService.findAllBookBeforeDate(localDate).forEach(System.out::println);
    }

    private void notReleasedBooks() throws IOException {
        System.out.println("Enter year: ");

        int year = Integer.parseInt(bufferedReader.readLine());

        bookService.findNotReleasedBooksInYear(year).forEach(System.out::println);
    }

    private void getBookByPrice() {
        this.bookService.findAllBookTittlesWithPriceLessThan5OrMoreThan40()
                .forEach(System.out::println);
    }

    private void getGoldenBook() {
        bookService.findAllGoldBookTittlesWithLessThan5000Copies().forEach(System.out::println);
    }

    private void bookTittlesByAgeRestriction() throws IOException {
        System.out.println("Enter age restriction: ");

        AgeRestriction age = AgeRestriction.valueOf(bufferedReader.readLine().toUpperCase());

        List<Book> books = this.bookService.getBooksByAgeRestriction(age);
        books.forEach(b -> System.out.printf("%s%n", b.getTitle()));
    }


    private void seedData() throws IOException {
        categoryService.seedCategories();
        authorService.seedAuthors();
        bookService.seedBooks();
    }
}