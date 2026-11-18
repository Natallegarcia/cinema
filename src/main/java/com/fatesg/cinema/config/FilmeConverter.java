package com.fatesg.cinema.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.fatesg.cinema.model.Filme;
import com.fatesg.cinema.service.FilmeService;

/**
 * Converte o id (String) enviado pelo formulario web no objeto Filme correspondente,
 * permitindo o binding do <select> da tela de Sessao.
 */
@Component
public class FilmeConverter implements Converter<String, Filme> {

    private final FilmeService filmeService;

    public FilmeConverter(FilmeService filmeService) {
        this.filmeService = filmeService;
    }

    @Override
    public Filme convert(String source) {
        if (source == null || source.isBlank()) {
            return null;
        }
        return filmeService.buscarPorId(Long.valueOf(source));
    }
}
