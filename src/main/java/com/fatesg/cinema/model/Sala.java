package com.fatesg.cinema.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Sala de cinema. Cadastro restrito ao perfil ADMIN.
 * Atributos: id, nome, capacidade, tipo, ativa.
 */
@Entity
@Table(name = "salas")
public class Sala {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome da sala e obrigatorio")
    @Size(max = 50)
    @Column(nullable = false, unique = true, length = 50)
    private String nome;

    @NotNull(message = "A capacidade e obrigatoria")
    @Min(value = 1, message = "A capacidade deve ser maior que zero")
    @Max(value = 260, message = "A capacidade maxima e 260 assentos")
    @Column(nullable = false)
    private Integer capacidade;

    @NotBlank(message = "O tipo da sala e obrigatorio")
    @Size(max = 20)
    @Column(nullable = false, length = 20)
    private String tipo;

    @Column(nullable = false)
    private boolean ativa = true;

    public Sala() {
    }

    public Sala(String nome, Integer capacidade, String tipo, boolean ativa) {
        this.nome = nome;
        this.capacidade = capacidade;
        this.tipo = tipo;
        this.ativa = ativa;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(Integer capacidade) {
        this.capacidade = capacidade;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public boolean isAtiva() {
        return ativa;
    }

    public void setAtiva(boolean ativa) {
        this.ativa = ativa;
    }
}
