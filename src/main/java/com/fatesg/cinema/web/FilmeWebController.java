package com.fatesg.cinema.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fatesg.cinema.model.Filme;
import com.fatesg.cinema.service.FilmeService;

import jakarta.validation.Valid;

/**
 * Controller web (Thymeleaf) para o CRUD de Filme.
 */
@Controller
@RequestMapping("/filmes")
public class FilmeWebController {

    private final FilmeService service;

    public FilmeWebController(FilmeService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("filmes", service.listar());
        return "filmes/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("filme", new Filme());
        return "filmes/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("filme", service.buscarPorId(id));
        return "filmes/form";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("filme") Filme filme, BindingResult result) {
        if (result.hasErrors()) {
            return "filmes/form";
        }
        if (filme.getId() == null) {
            service.salvar(filme);
        } else {
            service.atualizar(filme.getId(), filme);
        }
        return "redirect:/filmes";
    }

    @GetMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id) {
        service.excluir(id);
        return "redirect:/filmes";
    }
}
