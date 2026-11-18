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
import com.fatesg.cinema.model.Sessao;
import com.fatesg.cinema.service.FilmeService;
import com.fatesg.cinema.service.SalaService;
import com.fatesg.cinema.service.SessaoService;

import jakarta.validation.Valid;

/**
 * Controller web (Thymeleaf) para o CRUD de Sessao (venda de ingressos).
 */
@Controller
@RequestMapping("/sessoes")
public class SessaoWebController {

    private final SessaoService service;
    private final FilmeService filmeService;
    private final SalaService salaService;

    public SessaoWebController(SessaoService service, FilmeService filmeService,
                               SalaService salaService) {
        this.service = service;
        this.filmeService = filmeService;
        this.salaService = salaService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("sessoes", service.listar());
        return "sessoes/lista";
    }

    @GetMapping("/nova")
    public String nova(Model model) {
        model.addAttribute("sessao", new Sessao());
        model.addAttribute("filmes", filmeService.listar());
        model.addAttribute("salas", salaService.listar());
        return "sessoes/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("sessao", service.buscarPorId(id));
        model.addAttribute("filmes", filmeService.listar());
        model.addAttribute("salas", salaService.listar());
        return "sessoes/form";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("sessao") Sessao sessao,
                         BindingResult result, Model model) {
        if (!result.hasErrors()) {
            try {
                if (sessao.getId() == null) {
                    service.salvar(sessao);
                } else {
                    service.atualizar(sessao.getId(), sessao);
                }
            } catch (RegraNegocioException ex) {
                result.rejectValue("sala", "regra", ex.getMessage());
            }
        }
        if (result.hasErrors()) {
            model.addAttribute("filmes", filmeService.listar());
            model.addAttribute("salas", salaService.listar());
            return "sessoes/form";
        }
        return "redirect:/sessoes";
    }

    @GetMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            service.excluir(id);
        } catch (RegraNegocioException ex) {
            redirect.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/sessoes";
    }
}
