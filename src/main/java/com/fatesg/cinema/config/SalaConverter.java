package com.fatesg.cinema.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.fatesg.cinema.model.Sala;
import com.fatesg.cinema.service.SalaService;

/**
 * Converte o id (String) do formulario web na Sala correspondente (select da tela de Sessao).
 */
@Component
public class SalaConverter implements Converter<String, Sala> {

    private final SalaService salaService;

    public SalaConverter(SalaService salaService) {
        this.salaService = salaService;
    }

    @Override
    public Sala convert(String source) {
        if (source == null || source.isBlank()) {
            return null;
        }
        return salaService.buscarPorId(Long.valueOf(source));
    }
}
