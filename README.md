# 🎬 Sistema de Venda de Ingressos de Cinema

Projeto N2 — **Testes de Software** (FATESG — 2026/02 — Prof. Reinaldo Jr).

Aplicação **Full Stack** que simula um sistema de venda de ingressos de cinema,
desenvolvida em Java com Spring Boot, seguindo a arquitetura cliente-servidor e
com cobertura de testes automatizados em três níveis (unitário, API e E2E).

---

## 👥 Integrantes do Grupo



- Nátalle Garcia dos Santos


---

## 📝 Descrição da Proposta

O sistema gerencia a operação de um cinema e tem **dois tipos de usuário**:

- **Administrador (ADMIN):** mantém os cadastros de filmes, salas, sessões (com sala, horário,
  preço e assentos disponíveis) e usuários.
- **Cliente (USER):** consulta o catálogo de filmes em cartaz, escolhe assentos, paga em um
  checkout simulado (crédito, débito ou Pix) e acompanha seus ingressos.

O acesso é feito por tela de login com autenticação.

A aplicação contempla:

- **CRUD de Filme** (entidade de negócio 1) — 6 atributos:
  `id`, `titulo`, `genero`, `duracaoMinutos`, `classificacaoIndicativa`, `dataEstreia`.
- **CRUD de Sessão** (entidade de negócio 2) — 6 atributos:
  `id`, `filme`, `sala`, `dataHora`, `preco`, `assentosDisponiveis`.
- **CRUD de Sala** (exclusivo do perfil **ADMIN**) — `id`, `nome`, `capacidade`, `tipo` (2D/3D/IMAX/VIP), `ativa`.
  Cada sessão referencia uma sala cadastrada; os assentos da sessão não podem exceder a capacidade da sala,
  salas inativas não recebem sessões e salas com sessões não podem ser excluídas.
- **Área do cliente (perfil USER)** — o cliente não acessa os cadastros. Ele pode:
  consultar o **catálogo** de filmes e sessões, **escolher assentos** em um mapa da sala,
  **pagar** em um checkout simulado (**cartão de crédito, cartão de débito ou Pix**) e ver
  **Meus ingressos**. Um assento vendido não pode ser vendido de novo.
- **CRUD de Usuário** com **tela de Login funcional e autenticação** (Spring Security + senha criptografada com BCrypt).

### Perfis e permissões

| Recurso | ADMIN | USER (cliente) |
|---------|:-----:|:--------------:|
| Cadastros: filmes, sessões, salas, usuários (telas e API) | ✅ | ❌ (redirecionado ao catálogo / `403` na API) |
| Catálogo de filmes e sessões (tela e `GET` na API) | ✅ | ✅ |
| Compra de ingressos, checkout e "Meus ingressos" | ✅ | ✅ |
| Console do banco H2 (`/h2-console`) | ✅ | ❌ |

### Regras de negócio

- Os assentos de uma sessão não podem exceder a capacidade da sala; salas inativas não recebem sessões.
- Uma sala com sessões e uma sessão com ingressos vendidos não podem ser excluídas.
- Nome de sala e login de usuário são únicos.
- Um assento vendido não pode ser vendido de novo (restrição única no banco, inclusive para compras simultâneas).
- Máximo de 6 ingressos por compra; sessões que já começaram não vendem.
- O valor da compra é calculado no servidor a partir do preço da sessão, e os lugares disponíveis diminuem a cada venda.
- O pagamento é **simulado**: não há cobrança real e dados de cartão não são gravados.

A arquitetura segue o modelo **cliente-servidor**:

- **Back-end:** APIs REST (`/api/**`) para processamento de dados e regras de negócio.
- **Front-end:** interface web (Thymeleaf) com as telas de cadastro (ADMIN) e a área do cliente (catálogo, assentos, checkout).
- **Persistência:** banco de dados **relacional H2** (em arquivo, em `./data`; os testes usam H2 em memória).

---

## 🛠️ Tecnologias Utilizadas

| Camada | Tecnologia |
|--------|------------|
| Linguagem | Java 17+ |
| Framework | Spring Boot 3.4.1 (Web, Data JPA, Security, Validation, Thymeleaf) |
| Banco de dados | H2 (relacional, em arquivo) |
| Build | Maven (via Maven Wrapper — não requer Maven instalado) |
| Testes unitários | **JUnit 5** + Mockito |
| Testes de API | **REST-assured** |
| Testes E2E | **Selenium** (WebDriver) + WebDriverManager |

---

## 📁 Estrutura do Projeto

```
src/main/java/com/fatesg/cinema
├── IngressosCinemaApplication.java   # Classe principal
├── api/                              # Controllers REST (back-end)
│   ├── FilmeApiController.java
│   ├── SessaoApiController.java
│   ├── SalaApiController.java
│   ├── CompraApiController.java
│   ├── UsuarioApiController.java
│   └── ApiExceptionHandler.java
├── web/                              # Controllers web (front-end Thymeleaf)
│   ├── HomeController.java
│   ├── LoginController.java
│   ├── FilmeWebController.java
│   ├── SessaoWebController.java
│   ├── SalaWebController.java
│   ├── CatalogoController.java       # Catálogo e "meus ingressos" (cliente)
│   ├── CompraWebController.java      # Assentos, checkout e confirmação
│   └── UsuarioWebController.java
├── service/                          # Regras de negócio
├── repository/                       # Repositórios JPA
├── model/                            # Entidades (Filme, Sala, Sessao, Ingresso, Usuario)
├── dto/                              # Objetos de transferência (compra, assentos)
├── config/                           # Segurança, seed de dados, conversores
└── exception/                        # Exceções de negócio

src/main/resources
├── application.properties
├── static/css/style.css              # Estilos do front-end
└── templates/                        # Páginas Thymeleaf (login, cadastros do admin, catálogo e compra)
    ├── filmes/ sessoes/ salas/ usuarios/
    ├── catalogo/
    └── compra/                       # assentos, checkout, confirmação, meus ingressos

src/test/java/com/fatesg/cinema
├── service/   → Testes unitários (JUnit)
├── api/       → Testes de API (REST-assured)
└── e2e/       → Testes E2E (Selenium)
```

---

## ▶️ Como Executar o Ambiente

### Pré-requisitos

- **JDK 17 ou superior** instalado e a variável `JAVA_HOME` configurada.
- Conexão com a internet (a primeira execução baixa o Maven e as dependências).
- **Google Chrome** instalado (necessário apenas para os testes E2E com Selenium).

> O projeto usa o **Maven Wrapper**, portanto **não é necessário instalar o Maven**.
> Use `./mvnw` (Linux/macOS) ou `mvnw.cmd` (Windows).

> **Dica sobre o `JAVA_HOME`:** se o Maven Wrapper acusar o erro
> `JAVA_HOME not found in your environment`, defina a variável apontando para a
> pasta do seu JDK antes de rodar os comandos.
>
> **Windows (PowerShell):**
> ```powershell
> $env:JAVA_HOME="C:\Program Files\Java\jdk-21"   # ajuste para o caminho do seu JDK
> ```
> **Linux / macOS:**
> ```bash
> export JAVA_HOME=/caminho/para/seu/jdk
> ```

### 1. Rodar a aplicação

**Windows (PowerShell):**
```powershell
.\mvnw.cmd spring-boot:run
```

**Linux / macOS:**
```bash
./mvnw spring-boot:run
```

Acesse no navegador: **http://localhost:8080**

### 2. Credenciais de acesso (login)

Os usuários e os dados de exemplo (3 salas, 4 filmes e 5 sessões nos próximos dias) são criados
automaticamente na inicialização.

| Usuário | Senha | Perfil |
|---------|-------|--------|
| `admin` | `admin123` | ADMIN (cadastros: filmes, sessões, salas, usuários) |
| `user`  | `user123`  | USER (cliente: catálogo, compra de ingressos) |

Após o login, o ADMIN vai para `/filmes` e o cliente para `/catalogo`. Se o cliente tentar abrir
um cadastro, é redirecionado ao catálogo com um aviso de acesso negado.

### Fluxo de compra (cliente)

1. **Catálogo** (`/catalogo`): filmes em cartaz, com busca por título/gênero e as próximas sessões.
2. **Assentos** (`/compra/sessao/{id}`): mapa da sala, até 6 ingressos por compra.
3. **Pagamento** (`/compra/checkout`): crédito, débito ou Pix. É uma **simulação**, sem cobrança real
   e sem gravar dados de cartão. Regras: número com 13 a 19 dígitos, validade `MM/AA` não vencida,
   CVV com 3 ou 4 dígitos. **Cartões terminados em `0000` são recusados** (para demonstrar o erro).
4. **Confirmação** e **Meus ingressos** (`/meus-ingressos`).

### 3. Endpoints principais

- Front-end web: `http://localhost:8080/filmes`, `/sessoes`, `/usuarios` e `/salas` (somente ADMIN)
- Cliente: `/catalogo`, `/compra/sessao/{id}`, `/meus-ingressos`
- API REST (autenticação HTTP Basic): `GET/POST/PUT/DELETE /api/filmes`, `/api/sessoes`, `/api/salas`, `/api/usuarios`
  (cadastros somente ADMIN; o USER só pode fazer `GET` em filmes e sessões e usar a API de compras)
- Console do banco H2 (somente ADMIN logado): `http://localhost:8080/h2-console`
  (JDBC URL: `jdbc:h2:file:./data/cinemadb;AUTO_SERVER=TRUE`, usuário `sa`, senha em branco)
- DBeaver: crie uma conexão **H2 Server** (ou H2 Embedded) com a URL acima, apontando para o arquivo
  `data/cinemadb.mv.db` na raiz do projeto. A aplicação pode estar rodando durante o acesso.

---

## 🔌 Documentação da API REST

Todos os endpoints de `/api/**` exigem **autenticação HTTP Basic**
(ex.: usuário `admin`, senha `admin123`). As respostas são em **JSON**.

**Permissões:** `POST`, `PUT` e `DELETE` (e todo o recurso de salas e usuários) são **somente ADMIN**.
O perfil USER pode apenas `GET` em `/api/filmes` e `/api/sessoes` e usar `/api/compras`.

### Filmes — `/api/filmes`

| Método | Rota | Descrição | Status de sucesso |
|--------|------|-----------|-------------------|
| GET | `/api/filmes` | Lista todos os filmes | `200 OK` |
| GET | `/api/filmes/{id}` | Busca um filme por id | `200 OK` |
| POST | `/api/filmes` | Cria um novo filme | `201 Created` |
| PUT | `/api/filmes/{id}` | Atualiza um filme existente | `200 OK` |
| DELETE | `/api/filmes/{id}` | Remove um filme | `204 No Content` |

Exemplo de corpo (POST/PUT):
```json
{
  "titulo": "Duna",
  "genero": "Ficcao Cientifica",
  "duracaoMinutos": 155,
  "classificacaoIndicativa": "12",
  "dataEstreia": "2021-10-21"
}
```

### Sessões — `/api/sessoes`

| Método | Rota | Descrição | Status de sucesso |
|--------|------|-----------|-------------------|
| GET | `/api/sessoes` | Lista todas as sessões | `200 OK` |
| GET | `/api/sessoes/{id}` | Busca uma sessão por id | `200 OK` |
| POST | `/api/sessoes` | Cria uma nova sessão | `201 Created` |
| PUT | `/api/sessoes/{id}` | Atualiza uma sessão existente | `200 OK` |
| DELETE | `/api/sessoes/{id}` | Remove uma sessão | `204 No Content` |

Exemplo de corpo (POST/PUT) — `filme.id` deve referenciar um filme existente:
```json
{
  "filme": { "id": 1 },
  "sala": { "id": 1 },
  "dataHora": "2026-12-01T20:00:00",
  "preco": 30.00,
  "assentosDisponiveis": 100
}
```

### Salas — `/api/salas` (somente ADMIN)

Mesmos métodos CRUD das demais entidades. Usuário sem perfil ADMIN recebe `403 Forbidden`;
nome de sala duplicado retorna `400`.

```json
{ "nome": "Sala IMAX", "capacidade": 120, "tipo": "IMAX", "ativa": true }
```

### Usuários — `/api/usuarios`

| Método | Rota | Descrição | Status de sucesso |
|--------|------|-----------|-------------------|
| GET | `/api/usuarios` | Lista todos os usuários | `200 OK` |
| GET | `/api/usuarios/{id}` | Busca um usuário por id | `200 OK` |
| POST | `/api/usuarios` | Cria um novo usuário | `201 Created` |
| PUT | `/api/usuarios/{id}` | Atualiza um usuário existente | `200 OK` |
| DELETE | `/api/usuarios/{id}` | Remove um usuário | `204 No Content` |

Exemplo de corpo (POST):
```json
{
  "nome": "Carlos Silva",
  "username": "carlos",
  "email": "carlos@cinema.com",
  "senha": "segredo123",
  "perfil": "USER"
}
```

> A senha é armazenada **criptografada (BCrypt)** — nunca em texto puro.
> No `PUT`, enviar a senha em branco mantém a senha atual.

### Compras — `/api/compras` (qualquer usuário autenticado)

| Método | Rota | Descrição | Status |
|--------|------|-----------|--------|
| GET | `/api/sessoes/{id}/assentos` | Assentos ocupados da sessão | `200 OK` |
| POST | `/api/compras` | Compra ingressos (valida assentos e pagamento) | `201 Created` |
| GET | `/api/compras/meus` | Ingressos do usuário autenticado | `200 OK` |

```json
{
  "sessaoId": 1,
  "assentos": ["A1", "A2"],
  "formaPagamento": "CREDITO",
  "numeroCartao": "4111111111111111",
  "nomeTitular": "Maria Silva",
  "validade": "12/30",
  "cvv": "123"
}
```
Para `"formaPagamento": "PIX"` os campos do cartão não são necessários. O valor é sempre calculado
no servidor a partir do preço da sessão.

### Respostas de erro

As APIs retornam um JSON padronizado em caso de erro:

```json
{
  "timestamp": "2026-11-04T20:00:00",
  "status": 404,
  "erro": "Not Found",
  "mensagem": "Filme nao encontrado com id 99"
}
```

| Situação | Status HTTP |
|----------|-------------|
| Requisição sem autenticação | `401 Unauthorized` |
| Perfil sem permissão (ex.: USER em `/api/salas`) | `403 Forbidden` |
| Dados inválidos / regra de negócio violada | `400 Bad Request` |
| Recurso inexistente | `404 Not Found` |

---

## ✅ Como Executar os Testes Automatizados

### Testes unitários (JUnit) + testes de API (REST-assured)

```powershell
.\mvnw.cmd test
```

Executa:
- **13 testes unitários (JUnit)** — `FilmeService` (3), `UsuarioService` (3), `CompraService` (5) e `PagamentoService` (2).
- **10 testes de API (REST-assured)** — filmes (3), usuários (2), salas (2) e compras (3, incluindo as permissões do perfil USER).

Total da suíte padrão: **23 testes**.

### Testes E2E (Selenium)

Os testes E2E são marcados com `@Tag("e2e")` e executados com o perfil `e2e`
(requerem o Google Chrome instalado; a aplicação sobe sozinha em porta aleatória durante o teste):

```powershell
.\mvnw.cmd test -Pe2e
```

Por padrão o Chrome roda em modo headless (sem janela). Para ver o navegador durante os testes:

```powershell
.\mvnw.cmd test -Pe2e "-De2e.headless=false"
```

Executa **4 testes E2E (Selenium)**:
1. Login do ADMIN pela interface web.
2. Cadastro de um novo filme pela interface web (fluxo de CRUD).
3. Cliente é levado ao catálogo e não acessa os cadastros.
4. Cliente escolhe um assento, paga com Pix e vê a confirmação e "Meus ingressos".

### Resumo da cobertura de testes

| Nível de Teste | Framework | Quantidade | Exigido |
|----------------|-----------|------------|---------|
| Unitário | JUnit 5 | 13 | ≥ 3 ✅ |
| API | REST-assured | 10 | ≥ 2 ✅ |
| Integração (E2E) | Selenium | 4 | ≥ 2 ✅ |

---

## 📦 Gerar o pacote executável (JAR)

```powershell
.\mvnw.cmd clean package
```

O JAR é gerado em `target/ingressos-cinema.jar` e pode ser executado com:

```powershell
java -jar target/ingressos-cinema.jar
```

---


