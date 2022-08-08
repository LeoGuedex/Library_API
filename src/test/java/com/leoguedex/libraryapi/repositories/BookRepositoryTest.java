package com.leoguedex.libraryapi.repositories;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataJpaTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
public class BookRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("Deve retornar verdadeiro, quando existir um livro na base com isbn informado")
    public void turnBackTrueWhenIsbnExists(){
        //cenário
        String isbn = "123456789";

        //execucao
        Boolean exists = bookRepository.existsByIsbn(isbn);

        //Validacao
        Assertions.assertThat(exists).isTrue();

    }

}
