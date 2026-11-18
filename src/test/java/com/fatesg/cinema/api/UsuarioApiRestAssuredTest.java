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
 * Testes de API (REST-assured) para o recurso /api/usuarios.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UsuarioApiRestAssuredTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @Test
    @DisplayName("POST /api/usuarios deve criar usuario e nao expor a senha em texto puro")
    void deveCriarUsuario() {
        String corpo = """
            {
              "nome": "Carlos Teste",
              "username": "carlos_api",
              "email": "carlos@mail.com",
              "senha": "segredo123",
              "perfil": "USER"
            }
            """;

        given()
            .auth().preemptive().basic("admin", "admin123")
            .contentType(ContentType.JSON)
            .body(corpo)
        .when()
            .post("/api/usuarios")
        .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("username", equalTo("carlos_api"))
            .body("senha", notNullValue());
    }

    @Test
    @DisplayName("POST /api/usuarios com username duplicado deve retornar 400")
    void deveRejeitarUsernameDuplicado() {
        String corpo = """
            {
              "nome": "Admin Duplicado",
              "username": "admin",
              "email": "dup@mail.com",
              "senha": "qualquer123",
              "perfil": "USER"
            }
            """;

        given()
            .auth().preemptive().basic("admin", "admin123")
            .contentType(ContentType.JSON)
            .body(corpo)
        .when()
            .post("/api/usuarios")
        .then()
            .statusCode(400)
            .body("mensagem", equalTo("Ja existe um usuario com o login 'admin'"));
    }
}
