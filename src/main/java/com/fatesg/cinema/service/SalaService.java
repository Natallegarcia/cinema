package com.fatesg.cinema.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fatesg.cinema.exception.RecursoNaoEncontradoException;
import com.fatesg.cinema.exception.RegraNegocioException;
import com.fatesg.cinema.model.Sala;
import com.fatesg.cinema.repository.SalaRepository;
import com.fatesg.cinema.repository.SessaoRepository;

/**
 * Regras de negocio do CRUD de Sala.
 */
@Service
public class SalaService {

    private final SalaRepository repository;
    private final SessaoRepository sessaoRepository;

    public SalaService(SalaRepository repository, SessaoRepository sessaoRepository) {
        this.repository = repository;
        this.sessaoRepository = sessaoRepository;
    }

    public List<Sala> listar() {
        return repository.findAll();
    }

    public Sala buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Sala nao encontrada com id " + id));
    }

    public Sala salvar(Sala sala) {
        if (repository.existsByNome(sala.getNome())) {
            throw new RegraNegocioException("Ja existe uma sala com o nome '" + sala.getNome() + "'");
        }
        return repository.save(sala);
    }

    public Sala atualizar(Long id, Sala dados) {
        Sala sala = buscarPorId(id);
        if (repository.existsByNomeAndIdNot(dados.getNome(), id)) {
            throw new RegraNegocioException("Ja existe uma sala com o nome '" + dados.getNome() + "'");
        }
        sala.setNome(dados.getNome());
        sala.setCapacidade(dados.getCapacidade());
        sala.setTipo(dados.getTipo());
        sala.setAtiva(dados.isAtiva());
        return repository.save(sala);
    }

    public void excluir(Long id) {
        Sala sala = buscarPorId(id);
        if (sessaoRepository.existsBySalaId(id)) {
            throw new RegraNegocioException(
                    "A sala '" + sala.getNome() + "' possui sessoes cadastradas e nao pode ser excluida");
        }
        repository.delete(sala);
    }
}
