package com.fatesg.cinema.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

/**
 * Testes de API (REST-assured) do cadastro de salas, restrito ao perfil ADMIN.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SalaApiRestAssuredTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    private static final String CORPO = """
        { "nome": "%s", "capacidade": 120, "tipo": "IMAX", "ativa": true }
        """;

    @Test
    @DisplayName("ADMIN deve cadastrar sala (201) e nome duplicado deve retornar 400")
    void adminCadastraSala() {
        given().auth().preemptive().basic("admin", "admin123")
            .contentType(ContentType.JSON).body(CORPO.formatted("Sala IMAX"))
        .when().post("/api/salas")
        .then().statusCode(201)
            .body("id", notNullValue())
            .body("nome", equalTo("Sala IMAX"))
            .body("capacidade", equalTo(120));

        given().auth().preemptive().basic("admin", "admin123")
            .contentType(ContentType.JSON).body(CORPO.formatted("Sala IMAX"))
        .when().post("/api/salas")
        .then().statusCode(400);
    }

    @Test
    @DisplayName("Usuario comum nao pode acessar /api/salas (403)")
    void usuarioComumNaoAcessaSalas() {
        given().auth().preemptive().basic("user", "user123")
        .when().get("/api/salas")
        .then().statusCode(403);
    }
}
