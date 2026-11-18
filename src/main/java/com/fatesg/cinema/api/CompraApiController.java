package com.fatesg.cinema.api;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fatesg.cinema.dto.CompraRequest;
import com.fatesg.cinema.dto.ResumoCompra;
import com.fatesg.cinema.model.Ingresso;
import com.fatesg.cinema.model.Sessao;
import com.fatesg.cinema.service.CompraService;
import com.fatesg.cinema.service.SessaoService;

import jakarta.validation.Valid;

/**
 * API REST de compra de ingressos (disponivel para qualquer usuario autenticado).
 */
@RestController
public class CompraApiController {

    private final CompraService compraService;
    private final SessaoService sessaoService;

    public CompraApiController(CompraService compraService, SessaoService sessaoService) {
        this.compraService = compraService;
        this.sessaoService = sessaoService;
    }

    @GetMapping("/api/sessoes/{id}/assentos")
    public Map<String, Object> assentos(@PathVariable Long id) {
        Sessao sessao = sessaoService.buscarPorId(id);
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("sessaoId", sessao.getId());
        corpo.put("capacidade", sessao.getSala().getCapacidade());
        corpo.put("assentosDisponiveis", sessao.getAssentosDisponiveis());
        corpo.put("ocupados", new TreeSet<>(compraService.assentosOcupados(id)));
        return corpo;
    }

    @PostMapping("/api/compras")
    @ResponseStatus(HttpStatus.CREATED)
    public ResumoCompra comprar(@Valid @RequestBody CompraRequest request, Authentication auth) {
        return compraService.comprar(request.sessaoId(), request.assentos(),
                request.formaPagamento(), request.dadosPagamento(), auth.getName());
    }

    @GetMapping("/api/compras/meus")
    public List<Ingresso> meusIngressos(Authentication auth) {
        return compraService.listarDoUsuario(auth.getName());
    }
}
