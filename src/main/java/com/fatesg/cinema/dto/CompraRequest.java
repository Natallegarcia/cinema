package com.fatesg.cinema.dto;

import java.util.List;

import com.fatesg.cinema.model.FormaPagamento;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * Corpo da requisicao de compra pela API REST.
 */
public record CompraRequest(
        @NotNull(message = "A sessão é obrigatória") Long sessaoId,
        @NotEmpty(message = "Selecione ao menos um assento") List<String> assentos,
        @NotNull(message = "A forma de pagamento é obrigatória") FormaPagamento formaPagamento,
        String numeroCartao,
        String nomeTitular,
        String validade,
        String cvv) {

    public DadosPagamento dadosPagamento() {
        return new DadosPagamento(numeroCartao, nomeTitular, validade, cvv);
    }
}
