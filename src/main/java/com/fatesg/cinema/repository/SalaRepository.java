package com.fatesg.cinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fatesg.cinema.model.Sala;

public interface SalaRepository extends JpaRepository<Sala, Long> {

    boolean existsByNome(String nome);

    boolean existsByNomeAndIdNot(String nome, Long id);
}
