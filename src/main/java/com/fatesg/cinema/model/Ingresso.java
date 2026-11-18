package com.fatesg.cinema.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Ingresso vendido: um assento de uma sessao. A restricao de unicidade
 * (sessao, assento) impede vender o mesmo assento duas vezes.
 * Os dados do cartao nunca sao armazenados.
 */
@Entity
@Table(name = "ingressos",
        uniqueConstraints = @UniqueConstraint(columnNames = {"sessao_id", "assento"}))
public class Ingresso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "sessao_id", nullable = false)
    private Sessao sessao;

    @Column(nullable = false, length = 50)
    private String usuario;

    @Column(nullable = false, length = 10)
    private String assento;

    @Column(nullable = false)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FormaPagamento formaPagamento;

    @Column(nullable = false)
    private LocalDateTime dataCompra;

    @Column(nullable = false, length = 20)
    private String codigoPedido;

    public Ingresso() {
    }

    public Ingresso(Sessao sessao, String usuario, String assento, BigDecimal valor,
                    FormaPagamento formaPagamento, LocalDateTime dataCompra, String codigoPedido) {
        this.sessao = sessao;
        this.usuario = usuario;
        this.assento = assento;
        this.valor = valor;
        this.formaPagamento = formaPagamento;
        this.dataCompra = dataCompra;
        this.codigoPedido = codigoPedido;
    }

    public Long getId() {
        return id;
    }

    public Sessao getSessao() {
        return sessao;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getAssento() {
        return assento;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public LocalDateTime getDataCompra() {
        return dataCompra;
    }

    public String getCodigoPedido() {
        return codigoPedido;
    }
}
