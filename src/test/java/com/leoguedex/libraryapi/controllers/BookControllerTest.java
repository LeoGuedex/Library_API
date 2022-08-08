package com.leoguedex.libraryapi.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leoguedex.libraryapi.dtos.BookDto;
import com.leoguedex.libraryapi.entities.Book;
import com.leoguedex.libraryapi.exception.BusinessException;
import com.leoguedex.libraryapi.services.BookService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class BookControllerTest {

    static String BOOK_API = "/api/books";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BookService bookService;

    @Test
    @DisplayName("Deve criar um livro com sucesso")
    public void createBookTest() throws Exception {
        // Cenário
        BookDto bookDto = creatBookDto();

        String json = new ObjectMapper().writeValueAsString(bookDto);

        Book saveBook = creatBook();

        BDDMockito.given(bookService.save(Mockito.any(Book.class))).willReturn(saveBook);


        // Execução
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        // Validação
        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("title").value(bookDto.getTitle()))
                .andExpect(MockMvcResultMatchers.jsonPath("author").value(bookDto.getAuthor()))
                .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(bookDto.getIsbn()));

    }

    @Test
    @DisplayName("Deve lançar um erro de validação quando não houver dados suficientes para a criação do livro")
    public void createInvalidBookTest() throws Exception {
        // Cenário
        String json = new ObjectMapper().writeValueAsString(new BookDto());

        // Execução
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        // Validação
        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(3)));
    }

    @Test
    @DisplayName("Deve lancar um erro ao tentar cadastrar um ISBN que já foi utilizado por outro")
    public void createBookWithDuplicatedIsbn() throws Exception {
        // Cenário
        BookDto bookDto = creatBookDto();
        String message = "ISBN já cadastrado";

        String json = new ObjectMapper().writeValueAsString(bookDto);

        BDDMockito.given(bookService.save(Mockito.any(Book.class))).willThrow(new BusinessException(message));

        // Execução
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        // Validação
        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("errors[0]").value(message));
    }


    @Test
    private BookDto creatBookDto() {
      return BookDto.builder()
              .title("Titulo")
              .author("Autor")
              .isbn("123456")
              .build();
    }

    @Test
    private Book creatBook() {
        return Book.builder()
                .id(1L)
                .title("Titulo")
                .author("Autor")
                .isbn("123456")
                .build();
    }

}
