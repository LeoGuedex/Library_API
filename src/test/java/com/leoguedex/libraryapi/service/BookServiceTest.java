package com.leoguedex.libraryapi.service;

import com.leoguedex.libraryapi.entities.Book;
import com.leoguedex.libraryapi.exception.BusinessException;
import com.leoguedex.libraryapi.repositories.BookRepository;
import com.leoguedex.libraryapi.services.BookService;
import com.leoguedex.libraryapi.services.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class BookServiceTest {

    BookService bookService;

    @MockBean
    BookRepository bookRepository;

    @BeforeEach
    public void setup() {
        this.bookService = new BookServiceImpl(bookRepository);
    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBookTest() {
        //Cenario
        Long id = 1L;
        Book bookRequest = builderBook(null);
        Book bookResponse = builderBook(id);

        Mockito.when(bookRepository.existsByIsbn(Mockito.anyString())).thenReturn(false);
        Mockito.when(bookRepository.save(bookRequest)).thenReturn(bookResponse);

        //Execucao
        Book bookSaved = bookService.save(bookRequest);

        //Validacoes
        Assertions.assertThat(bookSaved.getId()).isNotNull();
        Assertions.assertThat(bookSaved.getId()).isEqualTo(1L);
        Assertions.assertThat(bookSaved.getTitle()).isEqualTo(bookRequest.getTitle());
        Assertions.assertThat(bookSaved.getAuthor()).isEqualTo(bookRequest.getAuthor());
        Assertions.assertThat(bookSaved.getIsbn()).isEqualTo(bookRequest.getIsbn());
    }

    @Test
    @DisplayName("Deve lancar um erro de negocio ao tentar salvar livro com ISBN duplicado")
    public void shouldNotSaveABookWithDuplicatedIsbn(){
        //cenario
        Book bookReceived = builderBook(null);
        Mockito.when(bookRepository.existsByIsbn(Mockito.anyString())).thenReturn(true);

        //Execucao
        Throwable exception = Assertions.catchThrowable(() -> bookService.save(bookReceived));

        //Validação
        Assertions.assertThat(exception).isInstanceOf(BusinessException.class).hasMessage("ISBN já cadastrado");
        //garantir que nunca rodou o save do book repository
        Mockito.verify(bookRepository, Mockito.never()).save(bookReceived);
    }

    @Test
    @DisplayName("Deve obter um livro por Id")
    public void getBookById(){
        //Cenario
        Long id = 1L;
        Book book = builderBook(id);
        book.setId(id);

        Mockito.when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        //Execucao
        Optional<Book> foundBook = bookService.getById(id);

        //Validacao
        Assertions.assertThat(foundBook.isPresent()).isTrue();
        Assertions.assertThat(foundBook.get().getId()).isEqualTo(id);
        Assertions.assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
        Assertions.assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
        Assertions.assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
    }

    @Test
    @DisplayName("Deve retornar vazio ao obter um livro por Id")
    public void bookNotFoundByIdTest(){
        //Cenario
        Long id = 1L;
        Mockito.when(bookRepository.findById(id)).thenReturn(Optional.empty());

        //Execucao
        Optional<Book> book = bookService.getById(id);

        //Validacao
        Assertions.assertThat(book.isPresent()).isFalse();
    }

    @Test
    @DisplayName("Deve deletar um livro")
    public void deletBookTest(){
        //Cenario
        Book book = builderBook(1L);
        //Execucao
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(()->bookService.deletBook(book));
        //Validacao
        Mockito.verify(bookRepository, Mockito.times(1)).delete(book);
    }

    @Test
    @DisplayName("Deve ocorrer um erro ao tentar deletar um livro inexistente")
    public void deletInValidBookTest(){
        Book book = new Book();

        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                ()->bookService.deletBook(book));

        Mockito.verify(bookRepository, Mockito.never()).delete(book);
    }

    @Test
    @DisplayName("Deve atualizar um livro")
    public void updateBookTest(){
        //cenario
        Long id = 1L;
        Book bookReceived = Book.builder().id(1L).build();
        Book bookReturned = builderBook(id);
        bookReturned.setId(id);

        Mockito.when(bookRepository.save(bookReceived)).thenReturn(bookReturned);

        Book bookUpdated = bookService.update(bookReceived);

        Assertions.assertThat(bookUpdated.getId()).isEqualTo(bookReturned.getId());
        Assertions.assertThat(bookUpdated.getIsbn()).isEqualTo(bookReturned.getIsbn());
        Assertions.assertThat(bookUpdated.getAuthor()).isEqualTo(bookReturned.getAuthor());
        Assertions.assertThat(bookUpdated.getTitle()).isEqualTo(bookReturned.getTitle());
    }

    @Test
    @DisplayName("Deve ocorrer um erro ao tentar deletar um livro inexistente")
    public void updateInValidBookTest(){
        Book book = new Book();

        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                ()->bookService.update(book));

        Mockito.verify(bookRepository, Mockito.never()).delete(book);
    }

    @Test
    @DisplayName("Deve obter um livro por ISBN")
    public void getBookByIsbn(){
        //Cenario
        String isbn = "123456789";
        Long id = 1L;
        Book bookReturned = Book.builder().id(id).isbn(isbn).build();
        Mockito.when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.of(bookReturned));

        //Execucao
        Optional<Book> bookFound = bookService.getBookByIsbn(isbn);

        //Validacao
        Assertions.assertThat(bookFound.isPresent()).isTrue();
        Assertions.assertThat(bookFound.get().getId()).isEqualTo(id);
        Assertions.assertThat(bookFound.get().getIsbn()).isEqualTo(isbn);

        Mockito.verify(bookRepository, Mockito.times(1)).findByIsbn(isbn);
    }

    private Book builderBook(Long id){
        return Book.builder()
                .id(id)
                .title("Titulo")
                .author("Autor")
                .isbn("123456")
                .build();
    }


}