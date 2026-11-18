package com.fatesg.cinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fatesg.cinema.model.Filme;

public interface FilmeRepository extends JpaRepository<Filme, Long> {
}
