package com.fatesg.cinema.e2e;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * Testes E2E (Selenium) do fluxo do cliente: catalogo -> assentos -> pagamento -> confirmacao.
 * Executar com: mvn test -Pe2e
 */
@Tag("e2e")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CompraIngressoE2ETest {

    @LocalServerPort
    private int port;

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeAll
    static void configurarDriver() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void abrirNavegador() {
        ChromeOptions options = new ChromeOptions();
        // Para ver o navegador: mvnw test -Pe2e -De2e.headless=false
        if (!"false".equalsIgnoreCase(System.getProperty("e2e.headless"))) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--no-sandbox", "--disable-dev-shm-usage", "--window-size=1280,900");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @AfterEach
    void fecharNavegador() {
        if (driver != null) {
            driver.quit();
        }
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    private void loginComoCliente() {
        driver.get(url("/login"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        driver.findElement(By.id("username")).sendKeys("user");
        driver.findElement(By.id("password")).sendKeys("user123");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(ExpectedConditions.urlContains("/catalogo"));
    }

    @Test
    @DisplayName("E2E: cliente e levado ao catalogo e nao acessa os cadastros")
    void clienteNaoAcessaCadastros() {
        loginComoCliente();
        Assertions.assertTrue(driver.getPageSource().contains("Filmes em cartaz"));

        driver.get(url("/filmes"));
        wait.until(ExpectedConditions.urlContains("/catalogo"));
        Assertions.assertTrue(driver.getPageSource().contains("não tem permissão"),
                "Deve exibir aviso de acesso negado");
        Assertions.assertTrue(driver.findElements(By.linkText("Usuarios")).isEmpty(),
                "Menu de cadastros nao deve aparecer para o cliente");
    }

    @Test
    @DisplayName("E2E: cliente escolhe assento, paga com Pix e ve a confirmacao")
    void clienteCompraIngressoComPix() {
        loginComoCliente();

        driver.findElement(By.cssSelector(".session a.btn-comprar")).click();
        wait.until(ExpectedConditions.urlContains("/compra/sessao/"));

        // Clica no rotulo do assento livre (o checkbox fica oculto por CSS).
        driver.findElement(By.cssSelector("label.seat:not(.seat-taken) span")).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.id("btn-continuar"))).click();

        wait.until(ExpectedConditions.urlContains("/compra/checkout"));
        driver.findElement(By.id("forma-PIX")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("bloco-pix")));
        driver.findElement(By.id("btn-pagar")).click();

        wait.until(ExpectedConditions.urlContains("/compra/confirmacao/"));
        Assertions.assertTrue(driver.findElement(By.id("msg-sucesso")).getText().contains("Compra confirmada"));
        Assertions.assertFalse(driver.findElement(By.id("codigo-pedido")).getText().isBlank());

        driver.get(url("/meus-ingressos"));
        Assertions.assertFalse(driver.findElements(By.cssSelector("table tbody tr td")).isEmpty());
        Assertions.assertTrue(driver.findElement(By.cssSelector("table tbody")).getText().contains("Pix"));
    }
}
