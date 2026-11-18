package com.fatesg.cinema.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

/**
 * Testes de API (REST-assured) do fluxo de compra e das permissoes do perfil USER.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CompraApiRestAssuredTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    private String corpoPix(String assento) {
        return """
            { "sessaoId": 1, "assentos": ["%s"], "formaPagamento": "PIX" }
            """.formatted(assento);
    }

    @Test
    @DisplayName("USER compra ingresso com Pix (201), o assento fica ocupado e nao pode ser vendido de novo (400)")
    void usuarioCompraIngresso() {
        given().auth().preemptive().basic("user", "user123")
            .contentType(ContentType.JSON).body(corpoPix("H5"))
        .when().post("/api/compras")
        .then().statusCode(201)
            .body("codigoPedido", notNullValue())
            .body("total", equalTo(32.00f))
            .body("ingressos.size()", equalTo(1))
            .body("ingressos[0].assento", equalTo("H5"));

        given().auth().preemptive().basic("user", "user123")
        .when().get("/api/sessoes/1/assentos")
        .then().statusCode(200)
            .body("ocupados", hasItem("H5"));

        given().auth().preemptive().basic("user", "user123")
            .contentType(ContentType.JSON).body(corpoPix("H5"))
        .when().post("/api/compras")
        .then().statusCode(400)
            .body("mensagem", containsString("H5"));

        given().auth().preemptive().basic("user", "user123")
        .when().get("/api/compras/meus")
        .then().statusCode(200)
            .body("assento", hasItem("H5"));
    }

    @Test
    @DisplayName("Cartao de credito recusado retorna 400 e valido retorna 201")
    void pagamentoComCartao() {
        String recusado = """
            { "sessaoId": 1, "assentos": ["H6"], "formaPagamento": "CREDITO",
              "numeroCartao": "4111111111110000", "nomeTitular": "Fulano", "validade": "12/99", "cvv": "123" }
            """;
        given().auth().preemptive().basic("user", "user123")
            .contentType(ContentType.JSON).body(recusado)
        .when().post("/api/compras")
        .then().statusCode(400)
            .body("mensagem", containsString("recusado"));

        String valido = recusado.replace("4111111111110000", "4111111111111111");
        given().auth().preemptive().basic("user", "user123")
            .contentType(ContentType.JSON).body(valido)
        .when().post("/api/compras")
        .then().statusCode(201)
            .body("formaPagamento", equalTo("CREDITO"));
    }

    @Test
    @DisplayName("USER consulta o catalogo (200) mas nao acessa os cadastros (403)")
    void usuarioNaoAcessaCadastros() {
        given().auth().preemptive().basic("user", "user123")
        .when().get("/api/filmes").then().statusCode(200);

        given().auth().preemptive().basic("user", "user123")
        .when().get("/api/usuarios").then().statusCode(403);

        given().auth().preemptive().basic("user", "user123")
            .contentType(ContentType.JSON)
            .body("{\"titulo\":\"X\",\"genero\":\"Y\",\"duracaoMinutos\":90,\"classificacaoIndicativa\":\"L\",\"dataEstreia\":\"2024-01-01\"}")
        .when().post("/api/filmes").then().statusCode(403);
    }
}
