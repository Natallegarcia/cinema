package com.fatesg.cinema.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fatesg.cinema.exception.RegraNegocioException;
import com.fatesg.cinema.model.Usuario;
import com.fatesg.cinema.service.UsuarioService;

import jakarta.validation.Valid;

/**
 * Controller web (Thymeleaf) para o CRUD de Usuario.
 */
@Controller
@RequestMapping("/usuarios")
public class UsuarioWebController {

    private final UsuarioService service;

    public UsuarioWebController(UsuarioService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", service.listar());
        return "usuarios/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuarios/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("usuario", service.buscarPorId(id));
        return "usuarios/form";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("usuario") Usuario usuario,
                         BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "usuarios/form";
        }
        try {
            if (usuario.getId() == null) {
                service.salvar(usuario);
            } else {
                service.atualizar(usuario.getId(), usuario);
            }
        } catch (RegraNegocioException ex) {
            result.rejectValue("username", "duplicado", ex.getMessage());
            return "usuarios/form";
        }
        return "redirect:/usuarios";
    }

    @GetMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id) {
        service.excluir(id);
        return "redirect:/usuarios";
    }
}
