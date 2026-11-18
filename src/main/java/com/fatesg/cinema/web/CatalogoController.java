package com.fatesg.cinema.web;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fatesg.cinema.dto.FilmeCatalogo;
import com.fatesg.cinema.model.Sessao;
import com.fatesg.cinema.service.CompraService;
import com.fatesg.cinema.service.FilmeService;
import com.fatesg.cinema.service.SessaoService;

/**
 * Area do cliente: catalogo de filmes com sessoes e historico de ingressos.
 */
@Controller
public class CatalogoController {

    private final FilmeService filmeService;
    private final SessaoService sessaoService;
    private final CompraService compraService;

    public CatalogoController(FilmeService filmeService, SessaoService sessaoService,
                              CompraService compraService) {
        this.filmeService = filmeService;
        this.sessaoService = sessaoService;
        this.compraService = compraService;
    }

    @GetMapping("/catalogo")
    public String catalogo(@RequestParam(required = false) String q, Model model) {
        LocalDateTime agora = LocalDateTime.now();
        Map<Long, List<Sessao>> sessoesPorFilme = sessaoService.listar().stream()
                .filter(s -> s.getDataHora().isAfter(agora) && s.getSala().isAtiva())
                .sorted(Comparator.comparing(Sessao::getDataHora))
                .collect(Collectors.groupingBy(s -> s.getFilme().getId()));

        String termo = q == null ? "" : q.trim().toLowerCase();
        List<FilmeCatalogo> catalogo = filmeService.listar().stream()
                .filter(f -> termo.isEmpty()
                        || f.getTitulo().toLowerCase().contains(termo)
                        || f.getGenero().toLowerCase().contains(termo))
                .map(f -> new FilmeCatalogo(f, sessoesPorFilme.getOrDefault(f.getId(), List.of())))
                .toList();

        model.addAttribute("catalogo", catalogo);
        model.addAttribute("q", q == null ? "" : q);
        return "catalogo/lista";
    }

    @GetMapping("/meus-ingressos")
    public String meusIngressos(Authentication auth, Model model) {
        model.addAttribute("ingressos", compraService.listarDoUsuario(auth.getName()));
        return "compra/meus-ingressos";
    }
}
