package com.fatesg.cinema.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fatesg.cinema.exception.RegraNegocioException;
import com.fatesg.cinema.model.Sala;
import com.fatesg.cinema.service.SalaService;

import jakarta.validation.Valid;

/**
 * Controller web (Thymeleaf) para o CRUD de Sala (somente ADMIN).
 */
@Controller
@RequestMapping("/salas")
public class SalaWebController {

    private final SalaService service;

    public SalaWebController(SalaService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("salas", service.listar());
        return "salas/lista";
    }

    @GetMapping("/nova")
    public String nova(Model model) {
        model.addAttribute("sala", new Sala());
        return "salas/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("sala", service.buscarPorId(id));
        return "salas/form";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("sala") Sala sala, BindingResult result) {
        if (result.hasErrors()) {
            return "salas/form";
        }
        try {
            if (sala.getId() == null) {
                service.salvar(sala);
            } else {
                service.atualizar(sala.getId(), sala);
            }
        } catch (RegraNegocioException ex) {
            result.rejectValue("nome", "duplicado", ex.getMessage());
            return "salas/form";
        }
        return "redirect:/salas";
    }

    @GetMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            service.excluir(id);
        } catch (RegraNegocioException ex) {
            redirect.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/salas";
    }
}
