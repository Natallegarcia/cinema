package com.fatesg.cinema.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

/**
 * Testes de API (REST-assured) para o recurso /api/filmes.
 * Sobe a aplicacao completa em uma porta aleatoria.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FilmeApiRestAssuredTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @Test
    @DisplayName("GET /api/filmes deve retornar 200 e a lista de filmes para usuario autenticado")
    void deveListarFilmes() {
        given()
            .auth().preemptive().basic("admin", "admin123")
        .when()
            .get("/api/filmes")
        .then()
            .statusCode(200)
            .body("size()", greaterThanOrEqualTo(2));
    }

    @Test
    @DisplayName("POST /api/filmes deve criar um novo filme e retornar 201")
    void deveCriarFilme() {
        String novoFilme = """
            {
              "titulo": "Duna",
              "genero": "Ficcao Cientifica",
              "duracaoMinutos": 155,
              "classificacaoIndicativa": "12",
              "dataEstreia": "2021-10-21"
            }
            """;

        given()
            .auth().preemptive().basic("admin", "admin123")
            .contentType(ContentType.JSON)
            .body(novoFilme)
        .when()
            .post("/api/filmes")
        .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("titulo", equalTo("Duna"))
            .body("duracaoMinutos", equalTo(155));
    }

    @Test
    @DisplayName("GET /api/filmes sem autenticacao deve retornar 401")
    void deveExigirAutenticacao() {
        given()
            .redirects().follow(false)
        .when()
            .get("/api/filmes")
        .then()
            .statusCode(401);
    }
}
