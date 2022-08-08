package com.leoguedex.libraryapi.services;

import com.leoguedex.libraryapi.entities.Book;

import java.util.Optional;

public interface BookService {

    Book save(Book book);

    Optional<Book> getById(Long id);

    void deletBook(Book book);

    Book update(Book book);

    Optional<Book> getBookByIsbn(String isbn);
}
