package services;
import entities.Author;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repositories.AuthorRepository;

import java.util.Random;

@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    @Autowired
    public AuthorServiceImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    public Author getRandomAuthor() {

        long size = authorRepository.count();

        int authorId = new Random().nextInt((int) size) + 1;

        return this.authorRepository.findById(authorId).get();
    }
}