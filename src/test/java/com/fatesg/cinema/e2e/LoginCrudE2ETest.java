package com.fatesg.cinema.e2e;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * Testes de integracao E2E (Selenium) cobrindo os fluxos principais:
 * 1. Login no sistema.
 * 2. Cadastro de um novo filme (CRUD) pela interface web.
 *
 * Marcados com @Tag("e2e"); executar com: mvn test -Pe2e
 * Requer o navegador Google Chrome instalado.
 */
@Tag("e2e")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LoginCrudE2ETest {

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

    private void login() {
        driver.get(url("/login"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
        driver.findElement(By.id("username")).sendKeys("admin");
        driver.findElement(By.id("password")).sendKeys("admin123");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(ExpectedConditions.urlContains("/filmes"));
    }

    @Test
    @DisplayName("E2E: deve realizar login com sucesso e exibir a listagem de filmes")
    void deveRealizarLogin() {
        login();
        Assertions.assertTrue(
                driver.getCurrentUrl().contains("/filmes"),
                "Apos o login o usuario deve ser redirecionado para /filmes");
        Assertions.assertTrue(driver.getPageSource().contains("Filmes"));
    }

    @Test
    @DisplayName("E2E: deve cadastrar um novo filme pela interface web")
    void deveCadastrarFilme() {
        login();

        driver.get(url("/filmes/novo"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("titulo")));

        String titulo = "Oppenheimer E2E";
        driver.findElement(By.id("titulo")).sendKeys(titulo);
        driver.findElement(By.id("genero")).sendKeys("Drama");
        driver.findElement(By.id("duracaoMinutos")).sendKeys("180");
        driver.findElement(By.id("classificacaoIndicativa")).sendKeys("14");

        // Define a data (input type=date) via JavaScript em formato ISO para evitar
        // problemas de locale do teclado.
        WebElement dataEstreia = driver.findElement(By.id("dataEstreia"));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = '2023-07-21';", dataEstreia);

        // Botao "Salvar" do formulario (evita o botao "Sair" da barra de navegacao).
        driver.findElement(By.cssSelector("form.card button[type='submit']")).click();

        wait.until(ExpectedConditions.urlMatches(".*/filmes$"));

        List<WebElement> linhas = driver.findElements(By.cssSelector("table tbody tr"));
        boolean encontrado = linhas.stream()
                .anyMatch(tr -> tr.getText().contains(titulo));
        Assertions.assertTrue(encontrado,
                "O filme recem-cadastrado deve aparecer na listagem");
    }
}
