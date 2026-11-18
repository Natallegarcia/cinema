package com.fatesg.cinema.web;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fatesg.cinema.dto.DadosPagamento;
import com.fatesg.cinema.dto.ResumoCompra;
import com.fatesg.cinema.exception.RegraNegocioException;
import com.fatesg.cinema.model.FormaPagamento;
import com.fatesg.cinema.model.Ingresso;
import com.fatesg.cinema.model.Sessao;
import com.fatesg.cinema.service.CompraService;
import com.fatesg.cinema.service.SessaoService;

/**
 * Fluxo de compra: escolha de assentos -> checkout -> confirmacao.
 */
@Controller
@RequestMapping("/compra")
public class CompraWebController {

    private final CompraService compraService;
    private final SessaoService sessaoService;

    public CompraWebController(CompraService compraService, SessaoService sessaoService) {
        this.compraService = compraService;
        this.sessaoService = sessaoService;
    }

    @GetMapping("/sessao/{id}")
    public String assentos(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        Sessao sessao = sessaoService.buscarPorId(id);
        if (sessao.getDataHora().isBefore(LocalDateTime.now())) {
            redirect.addFlashAttribute("erro", "Esta sessão já foi iniciada.");
            return "redirect:/catalogo";
        }
        model.addAttribute("sessao", sessao);
        model.addAttribute("mapa", compraService.mapaAssentos(sessao));
        model.addAttribute("maxIngressos", CompraService.MAX_INGRESSOS_POR_COMPRA);
        return "compra/assentos";
    }

    @PostMapping("/checkout")
    public String checkout(@RequestParam Long sessaoId,
                           @RequestParam(required = false) List<String> assentos,
                           Model model, RedirectAttributes redirect) {
        Sessao sessao = sessaoService.buscarPorId(sessaoId);
        try {
            List<String> selecionados = compraService.validarSelecao(sessao, assentos);
            preencherCheckout(model, sessao, selecionados, FormaPagamento.CREDITO);
            return "compra/checkout";
        } catch (RegraNegocioException ex) {
            redirect.addFlashAttribute("erro", ex.getMessage());
            return "redirect:/compra/sessao/" + sessaoId;
        }
    }

    @PostMapping("/pagar")
    public String pagar(@RequestParam Long sessaoId,
                        @RequestParam(required = false) List<String> assentos,
                        @RequestParam(required = false) FormaPagamento forma,
                        @RequestParam(required = false) String numeroCartao,
                        @RequestParam(required = false) String nomeTitular,
                        @RequestParam(required = false) String validade,
                        @RequestParam(required = false) String cvv,
                        Authentication auth, Model model) {
        try {
            ResumoCompra resumo = compraService.comprar(sessaoId, assentos, forma,
                    new DadosPagamento(numeroCartao, nomeTitular, validade, cvv), auth.getName());
            return "redirect:/compra/confirmacao/" + resumo.codigoPedido();
        } catch (RegraNegocioException ex) {
            Sessao sessao = sessaoService.buscarPorId(sessaoId);
            preencherCheckout(model, sessao, assentos == null ? List.of() : assentos,
                    forma == null ? FormaPagamento.CREDITO : forma);
            model.addAttribute("erro", ex.getMessage());
            return "compra/checkout";
        }
    }

    @GetMapping("/confirmacao/{codigo}")
    public String confirmacao(@PathVariable String codigo, Authentication auth, Model model) {
        List<Ingresso> ingressos = compraService.buscarPedido(codigo, auth.getName());
        BigDecimal total = ingressos.stream().map(Ingresso::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("ingressos", ingressos);
        model.addAttribute("sessao", ingressos.get(0).getSessao());
        model.addAttribute("forma", ingressos.get(0).getFormaPagamento());
        model.addAttribute("codigo", codigo);
        model.addAttribute("total", total);
        return "compra/confirmacao";
    }

    private void preencherCheckout(Model model, Sessao sessao, List<String> assentos,
                                   FormaPagamento forma) {
        model.addAttribute("sessao", sessao);
        model.addAttribute("assentos", assentos);
        model.addAttribute("total", compraService.calcularTotal(sessao, assentos.size()));
        model.addAttribute("forma", forma.name());
        model.addAttribute("formas", FormaPagamento.values());
    }
}
