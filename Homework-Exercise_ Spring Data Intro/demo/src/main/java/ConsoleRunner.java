import entities.Author;
import entities.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import repositories.AuthorRepository;
import repositories.BookRepository;
import services.SeedService;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ConsoleRunner implements CommandLineRunner {

    private final SeedService seedService;
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    @Autowired
    public ConsoleRunner(SeedService seedService, BookRepository bookRepository, AuthorRepository authorRepository) {
        this.seedService = seedService;
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        this.booksAfter2000();
        this.allAuthorsWithBookBefore1990();
        this.allAuthorsOrderedByBookCount();
        this.allBooksByAuthor();
    }

    private void allBooksByAuthor() {
        List<String> books =
                bookRepository.findAllByAuthor_FirstNameAndAuthor_LastNameOrderByReleaseDateDescTittle("George", "Powell")
                        .stream().map(book ->
                                String.format("Release date: %s | Tittle: %s | Copies: %d",
                                        book.getReleaseDate(), book.getTittle(), book.getCopies())).collect(Collectors.toList());
        books.forEach(System.out::println);
    }

    private void allAuthorsOrderedByBookCount() {
        List<Author> authors = this.authorRepository.findAll();
        authors.stream().sorted((l, r) -> r.getBooks().size() - l.getBooks().size())
                .forEach(a -> System.out.printf("%s %s -> %d%n",
                        a.getFirstName(), a.getLastName(), a.getBooks().size()));
    }

    private void allAuthorsWithBookBefore1990() {

        LocalDate year1990 = LocalDate.of(1990, 1, 1);

        List<Author> authors = this.authorRepository.findDistinctByBooksReleaseDateBefore(year1990);
        authors.forEach(a -> System.out.println(a.getFirstName() + " " + a.getLastName()));
    }

    private void booksAfter2000() {
        LocalDate after2000 = LocalDate.of(2000, 1, 1);

        List<Book> books = this.bookRepository.findByReleaseDateAfter(after2000);

        for (entities.Book book : books) {
            System.out.println(book.getTittle());
        }
    }
}