package com.fatesg.cinema.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fatesg.cinema.model.Filme;
import com.fatesg.cinema.service.FilmeService;

import jakarta.validation.Valid;

/**
 * API REST para o CRUD de Filme.
 */
@RestController
@RequestMapping("/api/filmes")
public class FilmeApiController {

    private final FilmeService service;

    public FilmeApiController(FilmeService service) {
        this.service = service;
    }

    @GetMapping
    public List<Filme> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Filme buscar(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Filme criar(@Valid @RequestBody Filme filme) {
        return service.salvar(filme);
    }

    @PutMapping("/{id}")
    public Filme atualizar(@PathVariable Long id, @Valid @RequestBody Filme filme) {
        return service.atualizar(id, filme);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
