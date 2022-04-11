package com.example.springintro.service;

import com.example.springintro.model.entity.AgeRestriction;
import com.example.springintro.model.entity.Book;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface BookService {
    void seedBooks() throws IOException;

    List<Book> findAllBooksAfterYear(int year);

    List<String> findAllAuthorsWithBooksWithReleaseDateBeforeYear(int year);

    List<String> findAllBooksByAuthorFirstAndLastNameOrderByReleaseDate(String firstName, String lastName);

    int findAuthorTotalBooks(String firstName, String lastName);

    int deleteWithLessCopiesThan(int amount);

    Iterable<Object> getBookInfo(String book);

    int addCopiesToBook(String date, int copies);

    Iterable<Object> findAuthorLastnameStartsWith(String line);

    boolean findCountOfBooksWithTittleLongerThan(int tittleLength);

    Iterable<Object> findBooksByContainString(String line);

    Iterable<Object> findAllBookBeforeDate(LocalDate localDate);

    Iterable<Object> findNotReleasedBooksInYear(int year);

    Iterable<Object> findAllBookTittlesWithPriceLessThan5OrMoreThan40();

    Iterable<Object> findAllGoldBookTittlesWithLessThan5000Copies();

    List<Book> getBooksByAgeRestriction(AgeRestriction age);
}
