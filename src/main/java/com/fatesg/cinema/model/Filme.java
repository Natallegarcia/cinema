package com.fatesg.cinema.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Entidade de negocio Filme (CRUD 1).
 * Atributos: id, titulo, genero, duracaoMinutos, classificacaoIndicativa, dataEstreia.
 */
@Entity
@Table(name = "filmes")
public class Filme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O titulo e obrigatorio")
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String titulo;

    @NotBlank(message = "O genero e obrigatorio")
    @Size(max = 60)
    @Column(nullable = false, length = 60)
    private String genero;

    @NotNull(message = "A duracao e obrigatoria")
    @Min(value = 1, message = "A duracao deve ser maior que zero")
    @Column(nullable = false)
    private Integer duracaoMinutos;

    @NotBlank(message = "A classificacao indicativa e obrigatoria")
    @Size(max = 10)
    @Column(nullable = false, length = 10)
    private String classificacaoIndicativa;

    @NotNull(message = "A data de estreia e obrigatoria")
    @Column(nullable = false)
    private LocalDate dataEstreia;

    public Filme() {
    }

    public Filme(String titulo, String genero, Integer duracaoMinutos,
                 String classificacaoIndicativa, LocalDate dataEstreia) {
        this.titulo = titulo;
        this.genero = genero;
        this.duracaoMinutos = duracaoMinutos;
        this.classificacaoIndicativa = classificacaoIndicativa;
        this.dataEstreia = dataEstreia;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public Integer getDuracaoMinutos() {
        return duracaoMinutos;
    }

    public void setDuracaoMinutos(Integer duracaoMinutos) {
        this.duracaoMinutos = duracaoMinutos;
    }

    public String getClassificacaoIndicativa() {
        return classificacaoIndicativa;
    }

    public void setClassificacaoIndicativa(String classificacaoIndicativa) {
        this.classificacaoIndicativa = classificacaoIndicativa;
    }

    public LocalDate getDataEstreia() {
        return dataEstreia;
    }

    public void setDataEstreia(LocalDate dataEstreia) {
        this.dataEstreia = dataEstreia;
    }
}
