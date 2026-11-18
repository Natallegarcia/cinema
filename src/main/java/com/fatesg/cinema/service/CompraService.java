package com.fatesg.cinema.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatesg.cinema.dto.AssentoView;
import com.fatesg.cinema.dto.DadosPagamento;
import com.fatesg.cinema.dto.ResumoCompra;
import com.fatesg.cinema.exception.RecursoNaoEncontradoException;
import com.fatesg.cinema.exception.RegraNegocioException;
import com.fatesg.cinema.model.FormaPagamento;
import com.fatesg.cinema.model.Ingresso;
import com.fatesg.cinema.model.Sessao;
import com.fatesg.cinema.repository.IngressoRepository;
import com.fatesg.cinema.repository.SessaoRepository;

/**
 * Regras da compra de ingressos: mapa de assentos, validacao da selecao,
 * pagamento e emissao dos ingressos.
 */
@Service
public class CompraService {

    public static final int ASSENTOS_POR_FILA = 10;
    public static final int MAX_INGRESSOS_POR_COMPRA = 6;

    private final IngressoRepository ingressoRepository;
    private final SessaoRepository sessaoRepository;
    private final SessaoService sessaoService;
    private final PagamentoService pagamentoService;

    public CompraService(IngressoRepository ingressoRepository, SessaoRepository sessaoRepository,
                         SessaoService sessaoService, PagamentoService pagamentoService) {
        this.ingressoRepository = ingressoRepository;
        this.sessaoRepository = sessaoRepository;
        this.sessaoService = sessaoService;
        this.pagamentoService = pagamentoService;
    }

    /** Gera os codigos dos assentos da sala: A1..A10, B1..B10, ... */
    public List<String> gerarAssentos(int capacidade) {
        List<String> assentos = new ArrayList<>();
        for (int i = 0; i < capacidade; i++) {
            char fila = (char) ('A' + i / ASSENTOS_POR_FILA);
            assentos.add(fila + String.valueOf(i % ASSENTOS_POR_FILA + 1));
        }
        return assentos;
    }

    public Set<String> assentosOcupados(Long sessaoId) {
        return ingressoRepository.findBySessaoId(sessaoId).stream()
                .map(Ingresso::getAssento)
                .collect(Collectors.toSet());
    }

    /** Mapa de assentos agrupado por fila, preservando a ordem. */
    public Map<String, List<AssentoView>> mapaAssentos(Sessao sessao) {
        Set<String> ocupados = assentosOcupados(sessao.getId());
        Map<String, List<AssentoView>> mapa = new LinkedHashMap<>();
        for (String codigo : gerarAssentos(sessao.getSala().getCapacidade())) {
            mapa.computeIfAbsent(codigo.substring(0, 1), k -> new ArrayList<>())
                    .add(new AssentoView(codigo, ocupados.contains(codigo)));
        }
        return mapa;
    }

    /** Valida a selecao de assentos e devolve os codigos normalizados. */
    public List<String> validarSelecao(Sessao sessao, List<String> assentos) {
        if (sessao.getDataHora().isBefore(LocalDateTime.now())) {
            throw new RegraNegocioException("Esta sessão já foi iniciada");
        }
        if (assentos == null || assentos.isEmpty()) {
            throw new RegraNegocioException("Selecione ao menos um assento");
        }
        List<String> selecionados = assentos.stream()
                .map(a -> a == null ? "" : a.trim().toUpperCase())
                .distinct()
                .toList();
        if (selecionados.size() > MAX_INGRESSOS_POR_COMPRA) {
            throw new RegraNegocioException(
                    "É possível comprar no máximo " + MAX_INGRESSOS_POR_COMPRA + " ingressos por vez");
        }

        Set<String> existentes = new HashSet<>(gerarAssentos(sessao.getSala().getCapacidade()));
        Set<String> ocupados = assentosOcupados(sessao.getId());
        for (String assento : selecionados) {
            if (!existentes.contains(assento)) {
                throw new RegraNegocioException("O assento " + assento + " não existe nesta sala");
            }
            if (ocupados.contains(assento)) {
                throw new RegraNegocioException("O assento " + assento + " já foi vendido");
            }
        }
        if (sessao.getAssentosDisponiveis() < selecionados.size()) {
            throw new RegraNegocioException("Não há assentos disponíveis suficientes nesta sessão");
        }
        return selecionados;
    }

    public BigDecimal calcularTotal(Sessao sessao, int quantidade) {
        return sessao.getPreco().multiply(BigDecimal.valueOf(quantidade));
    }

    /**
     * Valida, processa o pagamento (simulado) e emite os ingressos.
     * O preco sempre vem do banco, nunca do cliente.
     */
    @Transactional
    public ResumoCompra comprar(Long sessaoId, List<String> assentos, FormaPagamento forma,
                                DadosPagamento dadosPagamento, String usuario) {
        Sessao sessao = sessaoService.buscarPorId(sessaoId);
        List<String> selecionados = validarSelecao(sessao, assentos);
        BigDecimal total = calcularTotal(sessao, selecionados.size());

        pagamentoService.processar(forma, dadosPagamento, total);

        String codigo = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        LocalDateTime agora = LocalDateTime.now();
        List<Ingresso> ingressos = selecionados.stream()
                .map(a -> new Ingresso(sessao, usuario, a, sessao.getPreco(), forma, agora, codigo))
                .toList();
        try {
            ingressoRepository.saveAllAndFlush(ingressos);
        } catch (DataIntegrityViolationException ex) {
            throw new RegraNegocioException(
                    "Um dos assentos acabou de ser vendido. Escolha outros assentos");
        }

        sessao.setAssentosDisponiveis(sessao.getAssentosDisponiveis() - selecionados.size());
        sessaoRepository.save(sessao);
        return new ResumoCompra(codigo, total, forma, ingressos);
    }

    public List<Ingresso> listarDoUsuario(String usuario) {
        return ingressoRepository.findByUsuarioOrderByDataCompraDescAssentoAsc(usuario);
    }

    public List<Ingresso> buscarPedido(String codigo, String usuario) {
        List<Ingresso> ingressos = ingressoRepository.findByCodigoPedidoAndUsuario(codigo, usuario);
        if (ingressos.isEmpty()) {
            throw new RecursoNaoEncontradoException("Pedido não encontrado: " + codigo);
        }
        return ingressos;
    }
}
