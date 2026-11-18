package com.fatesg.cinema.dto;

import java.util.List;

import com.fatesg.cinema.model.Filme;
import com.fatesg.cinema.model.Sessao;

/**
 * Filme do catalogo junto com suas proximas sessoes.
 */
public class FilmeCatalogo {

    private final Filme filme;
    private final List<Sessao> sessoes;

    public FilmeCatalogo(Filme filme, List<Sessao> sessoes) {
        this.filme = filme;
        this.sessoes = sessoes;
    }

    public Filme getFilme() {
        return filme;
    }

    public List<Sessao> getSessoes() {
        return sessoes;
    }
}
