package com.fatesg.cinema.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Entidade de negocio Sessao (CRUD 2) - representa a venda de ingressos de uma sessao.
 * Atributos: id, filme, sala, dataHora, preco, assentosDisponiveis.
 */
@Entity
@Table(name = "sessoes")
public class Sessao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "O filme e obrigatorio")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "filme_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Filme filme;

    @NotNull(message = "A sala e obrigatoria")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sala_id", nullable = false)
    private Sala sala;

    @NotNull(message = "A data e hora da sessao e obrigatoria")
    @Column(nullable = false)
    private LocalDateTime dataHora;

    @NotNull(message = "O preco e obrigatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "O preco deve ser maior que zero")
    @Column(nullable = false)
    private BigDecimal preco;

    @NotNull(message = "A quantidade de assentos e obrigatoria")
    @Min(value = 0, message = "Assentos disponiveis nao pode ser negativo")
    @Column(nullable = false)
    private Integer assentosDisponiveis;

    public Sessao() {
    }

    public Sessao(Filme filme, Sala sala, LocalDateTime dataHora,
                  BigDecimal preco, Integer assentosDisponiveis) {
        this.filme = filme;
        this.sala = sala;
        this.dataHora = dataHora;
        this.preco = preco;
        this.assentosDisponiveis = assentosDisponiveis;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Filme getFilme() {
        return filme;
    }

    public void setFilme(Filme filme) {
        this.filme = filme;
    }

    public Sala getSala() {
        return sala;
    }

    public void setSala(Sala sala) {
        this.sala = sala;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public Integer getAssentosDisponiveis() {
        return assentosDisponiveis;
    }

    public void setAssentosDisponiveis(Integer assentosDisponiveis) {
        this.assentosDisponiveis = assentosDisponiveis;
    }
}
