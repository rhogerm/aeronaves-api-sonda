# Gestão de Aeronaves

API RESTful em Java (Spring Boot / Spring MVC + JPA) para cadastro de aeronaves, com
front-end SPA em AngularJS 1.x + Bootstrap, banco de dados PostgreSQL e versionamento
de schema com Flyway.

Implementado como resposta ao desafio técnico do processo seletivo Sonda VSS (cadastro
de aeronaves com back-end RESTful, front-end SPA e banco relacional).

## Sumário

- [Arquitetura](#arquitetura)
- [Stack tecnológica](#stack-tecnológica)
- [Regras de negócio implementadas](#regras-de-negócio-implementadas)
- [Como executar](#como-executar)
- [Endpoints da API](#endpoints-da-api)
- [Testes](#testes)
- [Critérios de avaliação](#mapeamento-dos-critérios-de-avaliação)

## Arquitetura

O projeto é dividido em duas aplicações independentes, comunicando-se via HTTP/JSON:

```
api-sonda/
├── backend/          API RESTful (Spring Boot 3 + Java 17 + JPA + PostgreSQL)
├── frontend/          SPA estático (AngularJS 1.x + Bootstrap 5, sem build/transpilação)
└── docker-compose.yml Sobe apenas o PostgreSQL (backend roda via Maven/JVM local)
```

### Back-end — camadas e design patterns

O back-end segue arquitetura em camadas (layered architecture), típica de aplicações
Spring MVC, com responsabilidades bem isoladas:

```
Controller  → recebe HTTP, valida entrada (Bean Validation), delega ao Service
   ↓
Service     → regras de negócio, transações
   ↓
Repository  → acesso a dados (Spring Data JPA)
   ↓
Model       → entidade JPA (Aeronave) + enum Fabricante (whitelist)
```

Design patterns e boas práticas aplicados:

- **Repository Pattern** (`AeronaveRepository extends JpaRepository`) — abstrai o acesso a dados.
- **DTO Pattern** (`AeronaveRequest` / `AeronaveResponse`) — a API nunca expõe a entidade JPA
  diretamente, evitando acoplamento entre o contrato HTTP e o modelo de persistência.
- **Mapper Pattern** via MapStruct (`AeronaveMapper`) — conversão DTO ↔ entidade sem código
  boilerplate manual e sem reflection em tempo de execução (geração em compile-time).
- **Strategy/Constraint customizada** (`@FabricanteValido` + `FabricanteValidator`) —
  encapsula a regra "nomes de fabricante devem ser consistentes" como uma anotação de
  Bean Validation reutilizável, plugada no ciclo de validação padrão do Spring.
- **Enum como Value Object fechado** (`Fabricante`) — a lista de fabricantes aceitos é um
  enum, não uma string livre; a mesma whitelist é reforçada por um `CHECK CONSTRAINT` no
  banco (`V1__create_aeronave_table.sql`), garantindo consistência mesmo contra inserts
  feitos fora da API (defesa em profundidade).
- **Global Exception Handler** (`@RestControllerAdvice`) — centraliza o tratamento de erros
  (404, 400 de validação, 500) em um único ponto, com um formato de erro (`ApiError`)
  consistente em toda a API.
- **Migrations versionadas** (Flyway) — o schema do banco é código versionado
  (`V1__create_aeronave_table.sql`, `V2__seed_data.sql`), não criado "na mão"; qualquer
  ambiente novo sobe com `ddl-auto: validate` (Hibernate nunca altera o schema em produção).
- **Records imutáveis para DTOs** — `AeronaveRequest`, `AeronaveResponse` e os DTOs de
  estatística são `record`s Java, garantindo imutabilidade e reduzindo boilerplate.

### Por que essa stack

- **Spring Boot** é a evolução natural do que o desafio pede ("Spring MVC e JPA") — o
  autoconfigure do Boot elimina XML/boilerplate de configuração mantendo os mesmos
  conceitos (`@Controller`, `@Service`, `@Repository`, JPA/Hibernate).
- **PostgreSQL**, dentre as opções sugeridas (Oracle/Postgres/MySQL), por ser open-source,
  fácil de rodar em container e com bom suporte a `CHECK constraints`.
- **AngularJS 1.x + Bootstrap**: seguindo a stack sugerida no PDF. É importante registrar
  que o AngularJS 1.x está oficialmente descontinuado desde 2022 (sem mais patches de
  segurança) — a escolha aqui é para aderir literalmente à recomendação do teste; para um
  projeto novo hoje a recomendação seria Angular moderno, React ou Vue.
- **Sem build tooling no front-end** (sem Webpack/Node/npm): o SPA é HTML5/CSS3/JS puro
  carregado via `<script>`, o que maximiza o critério "facilidade de configuração" —
  basta um servidor estático (ou até abrir com Live Server) apontando para a API.

## Stack tecnológica

| Camada        | Tecnologia                                                             |
|---------------|-------------------------------------------------------------------------|
| Back-end      | Java 17+, Spring Boot 3.3 (Web, Data JPA, Validation, Actuator), Maven  |
| Banco         | PostgreSQL 16 (via Docker) + Flyway (migrations)                       |
| Mapeamento    | MapStruct                                                               |
| Documentação  | springdoc-openapi (Swagger UI)                                         |
| Testes        | JUnit 5, Mockito, MockMvc, AssertJ                                      |
| Front-end     | AngularJS 1.8.3, Bootstrap 5 (via CDN), HTML5/CSS3, sem build step      |

## Regras de negócio implementadas

Todos os requisitos do desafio foram implementados:

- ✅ Cadastro, atualização e exclusão de aeronaves (`POST` / `PUT` / `DELETE`)
- ✅ Quantidade de aeronaves não vendidas (`GET /api/aeronaves/estatisticas/nao-vendidas`)
- ✅ Distribuição por década de fabricação (`GET /api/aeronaves/estatisticas/por-decada`)
- ✅ Distribuição por fabricante (`GET /api/aeronaves/estatisticas/por-fabricante`)
- ✅ Aeronaves registradas na última semana (`GET /api/aeronaves/estatisticas/ultima-semana`)
- ✅ Consistência de nomes de fabricante: o campo `marca` só aceita valores de uma
  whitelist fechada (`Fabricante`), validada tanto na API (Bean Validation) quanto no
  banco (`CHECK CONSTRAINT`). Tentativas como `"Enbraer"`, `"Boing"` ou `"ErBus"` são
  rejeitadas com HTTP 400 e mensagem explicando os valores aceitos.

## Como executar

### Pré-requisitos

- Java 17+ (JDK)
- Docker Desktop (para o PostgreSQL)
- Um servidor estático simples para o front-end (qualquer um serve): Python 3
  (`python -m http.server`), `npx serve`, extensão Live Server do VS Code, etc.
- **Não é necessário ter Maven instalado** — o projeto inclui Maven Wrapper (`mvnw` / `mvnw.cmd`).

### 1. Subir o banco de dados (PostgreSQL via Docker)

Na raiz do projeto:

```bash
docker compose up -d
```

Isso sobe um container `postgres:16-alpine` na porta `5432`, com banco `aeronaves_db`,
usuário `aeronaves` e senha `aeronaves` (apenas para desenvolvimento local).

### 2. Rodar o back-end

```bash
cd backend
./mvnw spring-boot:run        # Linux/Mac
mvnw.cmd spring-boot:run      # Windows
```

Na primeira execução, o Flyway cria automaticamente o schema (`V1`) e uma massa de dados
de exemplo (`V2`) para já demonstrar as estatísticas funcionando. A API sobe em
`http://localhost:8080`.

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Health check: `http://localhost:8080/actuator/health`

Variáveis de ambiente disponíveis (todas com valor padrão para rodar out-of-the-box):
`DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`, `CORS_ALLOWED_ORIGINS`.

### 3. Rodar o front-end

O front-end é estático (sem build). Sirva a pasta `frontend/` em qualquer servidor HTTP,
por exemplo:

```bash
cd frontend
python -m http.server 8081
```

Acesse `http://localhost:8081`. Por padrão, o front-end aponta para a API em
`http://localhost:8080/api` (ver `frontend/js/app.module.js`) e o back-end já libera
CORS para `http://localhost:8081` (ver `app.cors.allowed-origins` em `application.yml`).
Se você usar outra porta/servidor, ajuste um dos dois lados.

## Endpoints da API

| Método | Caminho                                    | Descrição                                   |
|--------|---------------------------------------------|----------------------------------------------|
| GET    | `/api/aeronaves`                            | Lista todas as aeronaves                      |
| GET    | `/api/aeronaves/find?termo=`                | Busca por termo (nome, marca ou id)          |
| GET    | `/api/aeronaves/{id}`                       | Detalhes de uma aeronave                      |
| POST   | `/api/aeronaves`                            | Cadastra uma nova aeronave                    |
| PUT    | `/api/aeronaves/{id}`                       | Atualiza uma aeronave existente               |
| DELETE | `/api/aeronaves/{id}`                       | Remove uma aeronave                           |
| GET    | `/api/aeronaves/estatisticas/nao-vendidas`  | Quantidade de aeronaves não vendidas          |
| GET    | `/api/aeronaves/estatisticas/por-decada`    | Distribuição por década de fabricação         |
| GET    | `/api/aeronaves/estatisticas/por-fabricante`| Distribuição por fabricante                   |
| GET    | `/api/aeronaves/estatisticas/ultima-semana` | Aeronaves cadastradas nos últimos 7 dias      |

Exemplo de payload (`POST`/`PUT`):

```json
{
  "nome": "E2-190",
  "marca": "Embraer",
  "ano": 2014,
  "descricao": "Jato comercial de fuselagem estreita",
  "vendido": false
}
```

Fabricantes aceitos (whitelist): `Embraer`, `Boeing`, `Airbus`, `Bombardier`, `Cessna`,
`ATR`, `Gulfstream`, `Dassault`, `Lockheed Martin`, `Piper`, `Textron Aviation`, `Saab`,
`Antonov`, `De Havilland`.

## Testes

```bash
cd backend
./mvnw test
```

Cobertura de testes automatizados:

- `FabricanteValidatorTest` — valida a whitelist de fabricantes (aceita variações de
  caixa/espaço, rejeita nomes incorretos como "Enbraer"/"Boing"/"ErBus").
- `AeronaveServiceImplTest` — regras de negócio da camada de serviço (CRUD, exceção de
  "não encontrado", agregações de década/fabricante, contagem de não vendidas), com o
  repositório mockado via Mockito.
- `AeronaveControllerTest` — camada HTTP via `MockMvc` (`@WebMvcTest`), incluindo o
  retorno 400 para fabricante inválido e 404 para aeronave inexistente.

## Mapeamento dos critérios de avaliação

| Critério                          | Como foi endereçado |
|------------------------------------|----------------------|
| Facilidade de configuração         | Maven Wrapper (não precisa instalar Maven); Postgres via `docker compose up -d`; front-end sem build step; variáveis de ambiente com defaults sensatos. |
| Performance                        | Índices em `marca`, `ano` e `created`; queries de agregação feitas no banco (`GROUP BY`) em vez de em memória na aplicação; `open-in-view: false` evitando conexões de banco ociosas. |
| Código limpo e organização         | Camadas separadas (controller/service/repository/dto/mapper/validation/exception); DTOs imutáveis (`record`); nomes de métodos e classes em português refletindo o domínio do negócio; comentários apenas onde a intenção não é óbvia. |
| Documentação de código             | Javadoc nos pontos não-óbvios (ex.: `Fabricante`, `FabricanteValido`); Swagger/OpenAPI documentando todos os endpoints (`@Operation`, `@Tag`). |
| Documentação do projeto (README)   | Este arquivo: arquitetura, stack, como rodar, endpoints, testes. |
| Arquitetura                        | Ver seção [Arquitetura](#arquitetura) — camadas, fluxo de dependências, decisões justificadas. |
| Boas práticas de desenvolvimento   | Bean Validation nas entradas; tratamento de erros centralizado; migrations versionadas (nunca `ddl-auto: update` em runtime); consistência de dados reforçada em dois níveis (API + banco). |
| Design Patterns                    | Repository, DTO, Mapper, Strategy (validação customizada), Global Exception Handler — ver seção de arquitetura. |

## Limitações conhecidas

- Não há autenticação/autorização — fora do escopo do desafio.
- O teste de UI foi validado via chamadas HTTP diretas à API (todos os endpoints e
  regras de negócio); a interação visual em navegador não pôde ser automatizada neste
  ambiente de desenvolvimento (sem extensão de browser disponível), mas o código do
  front-end foi revisado e o carregamento estático dos arquivos foi verificado.
