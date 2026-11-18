package com.fatesg.cinema.dto;

/**
 * Dados de cartao informados no checkout. Usados apenas para validar o
 * pagamento simulado; nunca sao gravados no banco.
 */
public record DadosPagamento(String numeroCartao, String nomeTitular, String validade, String cvv) {
}
