package com.fatesg.cinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fatesg.cinema.model.Sessao;

public interface SessaoRepository extends JpaRepository<Sessao, Long> {

    boolean existsBySalaId(Long salaId);
}
