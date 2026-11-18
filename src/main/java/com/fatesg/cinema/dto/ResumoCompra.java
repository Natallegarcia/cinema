package com.fatesg.cinema.dto;

import java.math.BigDecimal;
import java.util.List;

import com.fatesg.cinema.model.FormaPagamento;
import com.fatesg.cinema.model.Ingresso;

/**
 * Resultado de uma compra confirmada.
 */
public record ResumoCompra(String codigoPedido, BigDecimal total,
                           FormaPagamento formaPagamento, List<Ingresso> ingressos) {
}
