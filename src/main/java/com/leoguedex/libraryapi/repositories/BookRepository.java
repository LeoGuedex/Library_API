package com.leoguedex.libraryapi.repositories;

import com.leoguedex.libraryapi.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    Boolean existsByIsbn(String isbn);

    Optional<Book> findByIsbn(String isbn);
}
