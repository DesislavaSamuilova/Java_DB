package com.example.springintro.service;

import com.example.springintro.model.entity.Author;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.io.IOException;
import java.util.List;
@SpringBootApplication
public interface AuthorService {
    void seedAuthors() throws IOException;

    Author getRandomAuthor();

    List<String> getAllAuthorsOrderByCountOfTheirBooks();

    Iterable<Object> findAllAuthorsAndTotalCopies();

    Iterable<Object> findAllAuthorsWithFirstNameEndWith(String line);
}
