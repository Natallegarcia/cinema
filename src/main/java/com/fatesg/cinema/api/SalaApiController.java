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

import com.fatesg.cinema.model.Sala;
import com.fatesg.cinema.service.SalaService;

import jakarta.validation.Valid;

/**
 * API REST para o CRUD de Sala (somente ADMIN).
 */
@RestController
@RequestMapping("/api/salas")
public class SalaApiController {

    private final SalaService service;

    public SalaApiController(SalaService service) {
        this.service = service;
    }

    @GetMapping
    public List<Sala> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Sala buscar(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Sala criar(@Valid @RequestBody Sala sala) {
        return service.salvar(sala);
    }

    @PutMapping("/{id}")
    public Sala atualizar(@PathVariable Long id, @Valid @RequestBody Sala sala) {
        return service.atualizar(id, sala);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
