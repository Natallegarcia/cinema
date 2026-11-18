package com.fatesg.cinema.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fatesg.cinema.exception.RecursoNaoEncontradoException;
import com.fatesg.cinema.exception.RegraNegocioException;
import com.fatesg.cinema.model.Filme;
import com.fatesg.cinema.model.Sala;
import com.fatesg.cinema.model.Sessao;
import com.fatesg.cinema.repository.IngressoRepository;
import com.fatesg.cinema.repository.SessaoRepository;

/**
 * Regras de negocio do CRUD de Sessao (venda de ingressos).
 */
@Service
public class SessaoService {

    private final SessaoRepository repository;
    private final FilmeService filmeService;
    private final SalaService salaService;
    private final IngressoRepository ingressoRepository;

    public SessaoService(SessaoRepository repository, FilmeService filmeService,
                         SalaService salaService, IngressoRepository ingressoRepository) {
        this.ingressoRepository = ingressoRepository;
        this.repository = repository;
        this.filmeService = filmeService;
        this.salaService = salaService;
    }

    private Sala validarSala(Sessao sessao) {
        Sala sala = salaService.buscarPorId(sessao.getSala().getId());
        if (!sala.isAtiva()) {
            throw new RegraNegocioException("A sala '" + sala.getNome() + "' esta inativa");
        }
        if (sessao.getAssentosDisponiveis() > sala.getCapacidade()) {
            throw new RegraNegocioException("Assentos disponiveis ("
                    + sessao.getAssentosDisponiveis() + ") excedem a capacidade da sala ("
                    + sala.getCapacidade() + ")");
        }
        return sala;
    }

    public List<Sessao> listar() {
        return repository.findAll();
    }

    public Sessao buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Sessao nao encontrada com id " + id));
    }

    public Sessao salvar(Sessao sessao) {
        // garante que o filme referenciado existe
        Filme filme = filmeService.buscarPorId(sessao.getFilme().getId());
        sessao.setFilme(filme);
        sessao.setSala(validarSala(sessao));
        return repository.save(sessao);
    }

    public Sessao atualizar(Long id, Sessao dados) {
        Sessao sessao = buscarPorId(id);
        Filme filme = filmeService.buscarPorId(dados.getFilme().getId());
        sessao.setFilme(filme);
        sessao.setSala(validarSala(dados));
        sessao.setDataHora(dados.getDataHora());
        sessao.setPreco(dados.getPreco());
        sessao.setAssentosDisponiveis(dados.getAssentosDisponiveis());
        return repository.save(sessao);
    }

    public void excluir(Long id) {
        Sessao sessao = buscarPorId(id);
        if (ingressoRepository.existsBySessaoId(id)) {
            throw new RegraNegocioException("A sessao possui ingressos vendidos e nao pode ser excluida");
        }
        repository.delete(sessao);
    }
}
