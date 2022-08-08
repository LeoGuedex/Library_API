package com.leoguedex.libraryapi.controllers;

import com.leoguedex.libraryapi.dtos.BookDto;
import com.leoguedex.libraryapi.entities.Book;
import com.leoguedex.libraryapi.exception.ApiErrors;
import com.leoguedex.libraryapi.exception.BusinessException;
import com.leoguedex.libraryapi.services.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    BookService bookService;

    @Autowired
    ModelMapper modelMapper;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public BookDto create(@Valid @RequestBody BookDto bookDto) {
        Book toBook = modelMapper.map(bookDto, Book.class);
        Book bookSaved = bookService.save(toBook);
        return modelMapper.map(bookSaved, BookDto.class);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiErrors handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        return new ApiErrors(bindingResult);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)


    @ExceptionHandler(BusinessException.class)
    public ApiErrors handleBusinessErrors(BusinessException ex) {
        return new ApiErrors(ex);
    }


}