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

import com.fatesg.cinema.model.Sessao;
import com.fatesg.cinema.service.SessaoService;

import jakarta.validation.Valid;

/**
 * API REST para o CRUD de Sessao (venda de ingressos).
 */
@RestController
@RequestMapping("/api/sessoes")
public class SessaoApiController {

    private final SessaoService service;

    public SessaoApiController(SessaoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Sessao> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Sessao buscar(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Sessao criar(@Valid @RequestBody Sessao sessao) {
        return service.salvar(sessao);
    }

    @PutMapping("/{id}")
    public Sessao atualizar(@PathVariable Long id, @Valid @RequestBody Sessao sessao) {
        return service.atualizar(id, sessao);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
