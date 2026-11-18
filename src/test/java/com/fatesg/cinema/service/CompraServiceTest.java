package com.fatesg.cinema.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fatesg.cinema.dto.DadosPagamento;
import com.fatesg.cinema.dto.ResumoCompra;
import com.fatesg.cinema.exception.RegraNegocioException;
import com.fatesg.cinema.model.Filme;
import com.fatesg.cinema.model.FormaPagamento;
import com.fatesg.cinema.model.Ingresso;
import com.fatesg.cinema.model.Sala;
import com.fatesg.cinema.model.Sessao;
import com.fatesg.cinema.repository.IngressoRepository;
import com.fatesg.cinema.repository.SessaoRepository;

/**
 * Testes unitarios (JUnit + Mockito) das regras de compra de ingressos.
 */
@ExtendWith(MockitoExtension.class)
class CompraServiceTest {

    @Mock
    private IngressoRepository ingressoRepository;
    @Mock
    private SessaoRepository sessaoRepository;
    @Mock
    private SessaoService sessaoService;

    private CompraService service;
    private Sessao sessao;

    @BeforeEach
    void setUp() {
        service = new CompraService(ingressoRepository, sessaoRepository, sessaoService,
                new PagamentoService());
        Filme filme = new Filme("Filme Teste", "Drama", 100, "12", LocalDate.of(2020, 1, 1));
        Sala sala = new Sala("Sala 1", 20, "2D", true);
        sessao = new Sessao(filme, sala, LocalDateTime.now().plusDays(1), new BigDecimal("30.00"), 20);
        sessao.setId(1L);
    }

    @Test
    @DisplayName("Deve gerar assentos por fila e calcular o total a partir do preco da sessao")
    void deveGerarAssentosECalcularTotal() {
        List<String> assentos = service.gerarAssentos(12);

        assertEquals(12, assentos.size());
        assertEquals("A1", assentos.get(0));
        assertEquals("A10", assentos.get(9));
        assertEquals("B1", assentos.get(10));
        assertEquals(new BigDecimal("90.00"), service.calcularTotal(sessao, 3));
    }

    @Test
    @DisplayName("Deve emitir ingressos, descontar assentos disponiveis e aprovar Pix")
    void deveComprarComPix() {
        when(sessaoService.buscarPorId(1L)).thenReturn(sessao);
        when(ingressoRepository.findBySessaoId(1L)).thenReturn(List.of());

        ResumoCompra resumo = service.comprar(1L, List.of("a1", "A2"), FormaPagamento.PIX, null, "user");

        assertEquals(new BigDecimal("60.00"), resumo.total());
        assertEquals(2, resumo.ingressos().size());
        assertEquals("A1", resumo.ingressos().get(0).getAssento());
        assertEquals(18, sessao.getAssentosDisponiveis());
        verify(ingressoRepository).saveAllAndFlush(any());
        verify(sessaoRepository).save(sessao);
    }

    @Test
    @DisplayName("Nao deve vender assento ja ocupado")
    void naoDeveVenderAssentoOcupado() {
        when(sessaoService.buscarPorId(1L)).thenReturn(sessao);
        Ingresso existente = new Ingresso(sessao, "outro", "A1", sessao.getPreco(),
                FormaPagamento.PIX, LocalDateTime.now(), "XYZ");
        when(ingressoRepository.findBySessaoId(1L)).thenReturn(List.of(existente));

        RegraNegocioException ex = assertThrows(RegraNegocioException.class,
                () -> service.comprar(1L, List.of("A1"), FormaPagamento.PIX, null, "user"));

        assertTrue(ex.getMessage().contains("A1"));
        verify(ingressoRepository, never()).saveAllAndFlush(any());
    }

    @Test
    @DisplayName("Nao deve gravar ingressos quando o cartao e recusado")
    void naoDeveGravarQuandoPagamentoRecusado() {
        when(sessaoService.buscarPorId(1L)).thenReturn(sessao);
        when(ingressoRepository.findBySessaoId(1L)).thenReturn(List.of());
        DadosPagamento recusado = new DadosPagamento("4111111111110000", "Fulano", "12/99", "123");

        assertThrows(RegraNegocioException.class,
                () -> service.comprar(1L, List.of("B1"), FormaPagamento.CREDITO, recusado, "user"));

        verify(ingressoRepository, never()).saveAllAndFlush(any());
        assertEquals(20, sessao.getAssentosDisponiveis());
    }

    @Test
    @DisplayName("Deve rejeitar assento inexistente e mais de 6 ingressos por compra")
    void deveRejeitarSelecaoInvalida() {
        when(ingressoRepository.findBySessaoId(1L)).thenReturn(List.of());

        assertThrows(RegraNegocioException.class,
                () -> service.validarSelecao(sessao, List.of("Z99")));
        assertThrows(RegraNegocioException.class,
                () -> service.validarSelecao(sessao, List.of("A1", "A2", "A3", "A4", "A5", "A6", "A7")));
        assertThrows(RegraNegocioException.class,
                () -> service.validarSelecao(sessao, List.of()));
    }
}
