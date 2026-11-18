package com.fatesg.cinema.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fatesg.cinema.exception.RecursoNaoEncontradoException;
import com.fatesg.cinema.model.Filme;
import com.fatesg.cinema.repository.FilmeRepository;

/**
 * Regras de negocio do CRUD de Filme.
 */
@Service
public class FilmeService {

    private final FilmeRepository repository;

    public FilmeService(FilmeRepository repository) {
        this.repository = repository;
    }

    public List<Filme> listar() {
        return repository.findAll();
    }

    public Filme buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Filme nao encontrado com id " + id));
    }

    public Filme salvar(Filme filme) {
        return repository.save(filme);
    }

    public Filme atualizar(Long id, Filme dados) {
        Filme filme = buscarPorId(id);
        filme.setTitulo(dados.getTitulo());
        filme.setGenero(dados.getGenero());
        filme.setDuracaoMinutos(dados.getDuracaoMinutos());
        filme.setClassificacaoIndicativa(dados.getClassificacaoIndicativa());
        filme.setDataEstreia(dados.getDataEstreia());
        return repository.save(filme);
    }

    public void excluir(Long id) {
        Filme filme = buscarPorId(id);
        repository.delete(filme);
    }
}
