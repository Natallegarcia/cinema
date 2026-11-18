package com.fatesg.cinema.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.fatesg.cinema.model.Filme;
import com.fatesg.cinema.model.Sala;
import com.fatesg.cinema.model.Sessao;
import com.fatesg.cinema.model.Usuario;
import com.fatesg.cinema.model.Usuario.Perfil;
import com.fatesg.cinema.repository.FilmeRepository;
import com.fatesg.cinema.repository.SalaRepository;
import com.fatesg.cinema.repository.SessaoRepository;
import com.fatesg.cinema.service.UsuarioService;

/**
 * Carrega dados iniciais para facilitar o uso e os testes.
 * Usuario padrao: admin / admin123
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final UsuarioService usuarioService;
    private final FilmeRepository filmeRepository;
    private final SessaoRepository sessaoRepository;
    private final SalaRepository salaRepository;

    public DataSeeder(UsuarioService usuarioService,
                      FilmeRepository filmeRepository,
                      SessaoRepository sessaoRepository,
                      SalaRepository salaRepository) {
        this.salaRepository = salaRepository;
        this.usuarioService = usuarioService;
        this.filmeRepository = filmeRepository;
        this.sessaoRepository = sessaoRepository;
    }

    @Override
    public void run(String... args) {
        if (usuarioService.listar().isEmpty()) {
            usuarioService.salvar(new Usuario(
                    "Administrador", "admin", "admin@cinema.com", "admin123", Perfil.ADMIN));
            usuarioService.salvar(new Usuario(
                    "Usuario Comum", "user", "user@cinema.com", "user123", Perfil.USER));
        }

        if (salaRepository.count() == 0) {
            salaRepository.save(new Sala("Sala 1", 80, "2D", true));
            salaRepository.save(new Sala("Sala 2", 60, "3D", true));
            salaRepository.save(new Sala("Sala 3", 120, "IMAX", true));
        }

        if (filmeRepository.count() == 0) {
            Sala sala1 = salaRepository.findAll().get(0);
            Sala sala2 = salaRepository.findAll().get(1);
            Sala sala3 = salaRepository.findAll().get(2);
            Filme f1 = filmeRepository.save(new Filme(
                    "Interestelar", "Ficcao Cientifica", 169, "10", LocalDate.of(2014, 11, 6)));
            Filme f2 = filmeRepository.save(new Filme(
                    "O Poderoso Chefao", "Drama", 175, "16", LocalDate.of(1972, 3, 24)));

            sessaoRepository.save(new Sessao(
                    f1, sala1, LocalDateTime.now().plusDays(1).withHour(19).withMinute(30),
                    new BigDecimal("32.00"), 80));
            sessaoRepository.save(new Sessao(
                    f2, sala2, LocalDateTime.now().plusDays(1).withHour(21).withMinute(0),
                    new BigDecimal("28.50"), 60));

            Filme f3 = filmeRepository.save(new Filme(
                    "Divertida Mente 2", "Animacao", 96, "L", LocalDate.of(2024, 6, 20)));
            Filme f4 = filmeRepository.save(new Filme(
                    "Duna: Parte Dois", "Ficcao Cientifica", 166, "14", LocalDate.of(2024, 2, 29)));
            sessaoRepository.save(new Sessao(
                    f3, sala1, LocalDateTime.now().plusDays(2).withHour(15).withMinute(0),
                    new BigDecimal("24.00"), 80));
            sessaoRepository.save(new Sessao(
                    f4, sala3, LocalDateTime.now().plusDays(1).withHour(20).withMinute(15),
                    new BigDecimal("45.00"), 120));
            sessaoRepository.save(new Sessao(
                    f1, sala3, LocalDateTime.now().plusDays(3).withHour(18).withMinute(0),
                    new BigDecimal("45.00"), 120));
        }
    }
}
