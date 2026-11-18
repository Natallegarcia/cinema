package com.fatesg.cinema.model;

/**
 * Formas de pagamento aceitas no checkout (simulado).
 */
public enum FormaPagamento {
    CREDITO("Cartão de crédito"),
    DEBITO("Cartão de débito"),
    PIX("Pix");

    private final String rotulo;

    FormaPagamento(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getRotulo() {
        return rotulo;
    }

    public boolean exigeCartao() {
        return this != PIX;
    }
}
