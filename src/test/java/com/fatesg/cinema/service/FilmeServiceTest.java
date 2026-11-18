package com.fatesg.cinema.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fatesg.cinema.exception.RecursoNaoEncontradoException;
import com.fatesg.cinema.model.Filme;
import com.fatesg.cinema.repository.FilmeRepository;

/**
 * Testes unitarios (JUnit + Mockito) das regras de negocio de Filme.
 */
@ExtendWith(MockitoExtension.class)
class FilmeServiceTest {

    @Mock
    private FilmeRepository repository;

    @InjectMocks
    private FilmeService service;

    @Test
    @DisplayName("Deve salvar um filme chamando o repositorio")
    void deveSalvarFilme() {
        Filme filme = new Filme("Matrix", "Ficcao", 136, "14", LocalDate.of(1999, 5, 21));
        when(repository.save(filme)).thenReturn(filme);

        Filme salvo = service.salvar(filme);

        assertSame(filme, salvo);
        verify(repository).save(filme);
    }

    @Test
    @DisplayName("Deve lancar excecao ao buscar filme inexistente")
    void deveLancarQuandoFilmeNaoEncontrado() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        RecursoNaoEncontradoException ex = assertThrows(
                RecursoNaoEncontradoException.class,
                () -> service.buscarPorId(99L));

        assertEquals("Filme nao encontrado com id 99", ex.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar os dados de um filme existente")
    void deveAtualizarFilme() {
        Filme existente = new Filme("Antigo", "Drama", 100, "12", LocalDate.of(2000, 1, 1));
        existente.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(repository.save(any(Filme.class))).thenAnswer(inv -> inv.getArgument(0));

        Filme dados = new Filme("Novo Titulo", "Acao", 150, "16", LocalDate.of(2024, 6, 10));
        Filme atualizado = service.atualizar(1L, dados);

        assertEquals("Novo Titulo", atualizado.getTitulo());
        assertEquals("Acao", atualizado.getGenero());
        assertEquals(150, atualizado.getDuracaoMinutos());
        assertEquals("16", atualizado.getClassificacaoIndicativa());
        assertEquals(LocalDate.of(2024, 6, 10), atualizado.getDataEstreia());
    }
}
