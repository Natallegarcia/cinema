package com.fatesg.cinema.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fatesg.cinema.dto.DadosPagamento;
import com.fatesg.cinema.exception.RegraNegocioException;
import com.fatesg.cinema.model.FormaPagamento;

/**
 * Testes unitarios (JUnit) do pagamento simulado.
 */
class PagamentoServiceTest {

    private final PagamentoService service = new PagamentoService();
    private final BigDecimal valor = new BigDecimal("50.00");

    @Test
    @DisplayName("Pix e cartao valido devem ser aprovados")
    void deveAprovarPixECartaoValido() {
        assertDoesNotThrow(() -> service.processar(FormaPagamento.PIX, null, valor));
        assertDoesNotThrow(() -> service.processar(FormaPagamento.DEBITO,
                new DadosPagamento("4111 1111 1111 1111", "Maria Silva", "12/99", "123"), valor));
    }

    @Test
    @DisplayName("Deve recusar cartao vencido, numero invalido e cartao terminado em 0000")
    void deveRecusarCartoesInvalidos() {
        RegraNegocioException vencido = assertThrows(RegraNegocioException.class,
                () -> service.processar(FormaPagamento.CREDITO,
                        new DadosPagamento("4111111111111111", "Maria", "01/20", "123"), valor));
        assertEquals("Cartão vencido", vencido.getMessage());

        assertThrows(RegraNegocioException.class, () -> service.processar(FormaPagamento.CREDITO,
                new DadosPagamento("1234", "Maria", "12/99", "123"), valor));

        RegraNegocioException recusado = assertThrows(RegraNegocioException.class,
                () -> service.processar(FormaPagamento.CREDITO,
                        new DadosPagamento("4111111111110000", "Maria", "12/99", "123"), valor));
        assertEquals("Pagamento recusado pela operadora do cartão", recusado.getMessage());
    }
}
