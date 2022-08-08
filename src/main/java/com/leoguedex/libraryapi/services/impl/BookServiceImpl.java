package com.leoguedex.libraryapi.services.impl;

import com.leoguedex.libraryapi.entities.Book;
import com.leoguedex.libraryapi.exception.BusinessException;
import com.leoguedex.libraryapi.repositories.BookRepository;
import com.leoguedex.libraryapi.services.BookService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository
        bookRepository) {
        this.bookRepository
            = bookRepository;
    }



    @Override
    public Book save(Book book) {
        if (bookRepository.existsByIsbn(book.getIsbn())){
            throw new BusinessException   ("ISBN já cadastrado");
        }

        return bookRepository.save(book);
    }

    @Override
    public Optional<Book> getById(Long id) {
        return bookRepository.findById(id);
    }

    @Override
    public void deletBook(Book book) {
        if (book == null || book.getId() == null){
            throw new IllegalArgumentException("Book não pode ser null");
        }
        bookRepository.delete(book);
    }

    @Override
    public Book update(Book book) {
        if (book == null || book.getId() == null){
            throw new IllegalArgumentException("Book não pode ser null");
        }
        return bookRepository.save(book);
    }

    @Override
    public Optional<Book> getBookByIsbn(String isbn) {
        return bookRepository.findByIsbn(isbn);
    }

}
