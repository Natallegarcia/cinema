package com.fatesg.cinema.dto;

/**
 * Assento exibido no mapa da sala (codigo como "A1" e se ja foi vendido).
 */
public class AssentoView {

    private final String codigo;
    private final boolean ocupado;

    public AssentoView(String codigo, boolean ocupado) {
        this.codigo = codigo;
        this.ocupado = ocupado;
    }

    public String getCodigo() {
        return codigo;
    }

    public boolean isOcupado() {
        return ocupado;
    }
}
