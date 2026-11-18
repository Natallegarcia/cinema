package com.fatesg.cinema.service;

import java.math.BigDecimal;
import java.time.YearMonth;

import org.springframework.stereotype.Service;

import com.fatesg.cinema.dto.DadosPagamento;
import com.fatesg.cinema.exception.RegraNegocioException;
import com.fatesg.cinema.model.FormaPagamento;

/**
 * Gateway de pagamento SIMULADO. Nao ha cobranca real.
 * Regras: Pix e sempre aprovado; cartoes (credito/debito) sao validados no formato
 * e cartoes terminados em "0000" sao recusados, para permitir demonstrar o erro.
 */
@Service
public class PagamentoService {

    public void processar(FormaPagamento forma, DadosPagamento dados, BigDecimal valor) {
        if (forma == null) {
            throw new RegraNegocioException("Selecione a forma de pagamento");
        }
        if (!forma.exigeCartao()) {
            return;
        }
        if (dados == null) {
            throw new RegraNegocioException("Informe os dados do cartão");
        }

        String numero = dados.numeroCartao() == null ? "" : dados.numeroCartao().replaceAll("[\\s-]", "");
        if (!numero.matches("\\d{13,19}")) {
            throw new RegraNegocioException("Número do cartão inválido");
        }
        if (dados.nomeTitular() == null || dados.nomeTitular().isBlank()) {
            throw new RegraNegocioException("Informe o nome do titular");
        }
        validarValidade(dados.validade());
        if (dados.cvv() == null || !dados.cvv().matches("\\d{3,4}")) {
            throw new RegraNegocioException("CVV inválido");
        }
        if (numero.endsWith("0000")) {
            throw new RegraNegocioException("Pagamento recusado pela operadora do cartão");
        }
    }

    private void validarValidade(String validade) {
        if (validade == null || !validade.trim().matches("(0[1-9]|1[0-2])/\\d{2}")) {
            throw new RegraNegocioException("Validade inválida (use MM/AA)");
        }
        String[] partes = validade.trim().split("/");
        YearMonth venc = YearMonth.of(2000 + Integer.parseInt(partes[1]), Integer.parseInt(partes[0]));
        if (venc.isBefore(YearMonth.now())) {
            throw new RegraNegocioException("Cartão vencido");
        }
    }
}
