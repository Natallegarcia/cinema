package com.fatesg.cinema.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fatesg.cinema.model.Ingresso;

public interface IngressoRepository extends JpaRepository<Ingresso, Long> {

    List<Ingresso> findBySessaoId(Long sessaoId);

    boolean existsBySessaoId(Long sessaoId);

    List<Ingresso> findByUsuarioOrderByDataCompraDescAssentoAsc(String usuario);

    List<Ingresso> findByCodigoPedidoAndUsuario(String codigoPedido, String usuario);
}
