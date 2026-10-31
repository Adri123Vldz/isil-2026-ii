# Architecture Diagram

The application separates the order-entry workflow from read-only reports. Spring MVC and the JSON API share business services; JPA handles order persistence and Spring JDBC runs the PA3 reporting queries against the same local database.

## Application Architecture

<!-- mermaid-checked: no \n, no em-dash/en-dash, no {} in labels, subgraphs are id["label"], arrows are -->|"label"|, all subgraphs closed by end, ids unique -->
~~~mermaid
flowchart TD
    subgraph ClientLayer["Client Layer"]
        Browser["Browser or HTTP client"]
    end
    subgraph AppLayer["Spring Boot 4.1.1 Application"]
        Security["Spring Security"]
        MVC["Spring MVC and JSP"]
        API["REST JSON and HAL"]
        Services["Order and report services"]
        InventoryClient["Inventory REST client"]
    end
    subgraph DataLayer["Data Layer"]
        JPA["Spring Data JPA"]
        JDBC["Spring JDBC"]
        MySQL[("MySQL orders database")]
    end
    subgraph ExternalLayer["External Services"]
        Inventory["Jakarta EE inventory service"]
        SQLServer[("SQL Server inventory database")]
    end
    Browser -->|"HTTP request"| Security
    Security -->|"authorized request"| MVC
    Security -->|"authorized request"| API
    MVC -->|"JSP and Model"| Services
    API -->|"JSON request"| Services
    Services -->|"order CRUD"| JPA
    Services -->|"read-only reports"| JDBC
    JPA -->|"ORM persistence"| MySQL
    JDBC -->|"parameterized SQL"| MySQL
    Services -->|"inventory operation"| InventoryClient
    InventoryClient -->|"HTTP and JSON"| Inventory
    Inventory -->|"JPA persistence"| SQLServer
~~~

### Technology Stack Summary

| Layer | Technology | Version | Purpose |
|---|---|---|---|
| Language | Java | 21 | Application source and tests |
| Application | Spring Boot | 4.1.1 | Dependency injection and application runtime |
| Presentation | Spring MVC, JSP, JSTL | Configured by Maven | HTML report and order screens |
| API | Spring MVC REST, Spring HATEOAS | Configured by Maven | JSON and HAL reporting endpoints |
| Security | Spring Security | Configured by Maven | Session login, role checks and CSRF |
| Order persistence | Spring Data JPA | Configured by Maven | Existing order CRUD |
| Report persistence | Spring JDBC | Configured by Maven | Parameterized reporting queries |
| Runtime database | MySQL | External installation | Order history and reporting source |
| Test database | H2 | Test scope | Automated repository and MVC tests |
| External integration | Spring RestClient | Configured by Maven | Calls to inventory-service |
| Build and tests | Maven Wrapper, JUnit 5, Mockito | Wrapper and Maven dependencies | Repeatable build and automated verification |

### Data Storage & External Services

MySQL stores the local order history and is shared by Spring Data JPA and Spring JDBC. The report queries read this local history and do not depend on inventory-service. H2 is configured for automated tests. Order creation calls the separate Jakarta EE inventory-service, which uses SQL Server for inventory data.

### Key Architectural Decisions

- Keep the existing JPA order workflow and implement the new reporting SQL with `NamedParameterJdbcTemplate`.
- Reuse `PedidoReporteService` from both the JSP controller and JSON controller.
- Protect report reads by role and keep CSRF enabled for state-changing form submissions.

## Component Relationships

<!-- mermaid-checked: no \n, no em-dash/en-dash, no {} in labels, subgraphs are id["label"], arrows are -->|"label"|, all subgraphs closed by end, ids unique -->
~~~mermaid
flowchart LR
    subgraph PresentationLayer["Presentation"]
        cWeb["PedidoReporteWebController"]
        cRest["PedidoReporteController"]
        cJsp["reportes-pedidos.jsp"]
        cDto["Report response DTOs"]
    end
    subgraph BusinessLayer["Business Logic"]
        cService["PedidoReporteService"]
        cImpl["PedidoReporteServiceImpl"]
    end
    subgraph DataAccessLayer["Data Access"]
        cJdbcRepo["PedidoReporteJdbcRepository"]
        cRowMapper["PedidoRowMapper"]
        cProjection["ClienteFrecuenteProjection"]
        cJpaRepo["PedidoRepository"]
    end
    subgraph InfraLayer["Infrastructure"]
        cSecurity["SecurityFilterChain"]
        cJdbc["NamedParameterJdbcTemplate"]
        cJpa["Spring Data JPA"]
        cDb[("MySQL or H2")]
        cInvClient["InventarioRestClient"]
        cInvService["inventory-service"]
    end
    cSecurity -.->|"authorizes"| cWeb
    cSecurity -.->|"authorizes"| cRest
    cWeb -->|"delegates"| cService
    cRest -->|"delegates"| cService
    cWeb -->|"returns view"| cJsp
    cRest -->|"maps response"| cDto
    cService -->|"implemented by"| cImpl
    cImpl -->|"report queries"| cJdbcRepo
    cImpl -->|"order operations"| cJpaRepo
    cJdbcRepo -->|"maps Pedido rows"| cRowMapper
    cJdbcRepo -->|"maps aggregates"| cProjection
    cJdbcRepo -->|"parameterized SQL"| cJdbc
    cJdbc -->|"reads"| cDb
    cJpaRepo -->|"JPA operations"| cJpa
    cJpa -->|"persists"| cDb
    cInvClient -->|"HTTP JSON"| cInvService
~~~

### Component Inventory

| Component | Layer | Type | Responsibility |
|---|---|---|---|
| `PedidoReporteWebController` | Presentation | Spring MVC controller | Populates the report model and selects the JSP view |
| `PedidoReporteController` | Presentation | REST controller | Exposes reporting resources as JSON and HAL |
| `reportes-pedidos.jsp` | Presentation | JSP view | Displays the summary, advanced search and frequent customers |
| `ClienteFrecuenteDto` | Presentation | Response DTO | Defines the frequent-customer JSON representation |
| `PedidoReporteService` | Business logic | Service interface | Shares report operations across web and API entry points |
| `PedidoReporteServiceImpl` | Business logic | Service | Validates report inputs and maps repository results |
| `PedidoReporteJdbcRepository` | Data access | JDBC repository | Executes explicit, parameterized report SQL |
| `PedidoRowMapper` | Data access | Row mapper | Maps SQL order rows to `Pedido` |
| `ClienteFrecuenteProjection` | Data access | Projection | Carries SQL aggregate values per customer |
| `PedidoRepository` | Data access | Spring Data repository | Preserves existing JPA order operations |
| `SecurityConfig` | Infrastructure | Security configuration | Enforces authenticated access, roles and CSRF |
| `InventarioRestClient` | Infrastructure | HTTP adapter | Communicates with the external inventory service |
