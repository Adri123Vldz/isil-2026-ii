# Sistema de Pedidos — Spring Boot

Aplicación académica para registrar y consultar pedidos, con Spring Boot, Spring MVC, Spring Data JPA y H2. `inventario-service` (Jakarta EE) gestiona el catálogo, las reservas y las devoluciones compensatorias de stock.

## Estructura

```text
src/main/java/com/isil/edu/pe/spedidos/
├── client/          Cliente HTTP y DTOs de Inventario
├── config/          Configuración del cliente HTTP
├── controller/      Controladores MVC y API REST
├── entity/          Entidades JPA
├── exception/       Excepciones de negocio e integración
├── repository/      Repositorios Spring Data (pedidos y auditoría)
└── service/         Contrato y lógica de negocio
src/main/resources/  Configuración de Spring y H2
src/main/webapp/     Vistas JSP y recursos estáticos
src/test/java/       Pruebas unitarias y de contexto
postman/             Colección de solicitudes de ejemplo
arquitectura-anulacion.puml  Secuencia de cancelación y recuperación
```

## Requisitos y ejecución

- JDK 21.
- PowerShell en Windows (el proyecto incluye Maven Wrapper).
- SQL Server con la tabla de compensaciones instalada mediante `database/04_crear_compensaciones.sql` en Inventario.
- `inventario-service` disponible en `http://localhost:8082` para consultar, reservar y devolver stock.

Desde la raíz del proyecto:

```powershell
.\mvnw.cmd spring-boot:run
```

La aplicación inicia en `http://localhost:8081/sistema-pedidos`.

## API de pedidos

La API REST usa el prefijo `/api/v1/orders` bajo el context path `/sistema-pedidos`.

| Método | Ruta | Resultado |
|---|---|---|
| `GET` | `/sistema-pedidos/api/v1/orders` | Lista los pedidos como JSON. |
| `DELETE` | `/sistema-pedidos/api/v1/orders/{pedidoId}` | Ruta de compatibilidad para devolver el stock y anular un pedido. |
| `POST` | `/sistema-pedidos/api/v1/anulaciones` | Registra la anulación de un pedido y devuelve sus unidades al inventario. |
| `GET` | `/sistema-pedidos/api/v1/anulaciones` | Lista las anulaciones registradas. |

Para crear una anulación, envía `{"pedidoId": 1, "motivo": "Solicitud del cliente"}`. `motivo` es opcional. La respuesta es `201 Created` e incluye el código, la cantidad devuelta y la fecha de anulación. Un `pedidoId` obligatorio y positivo es validado antes de procesar la solicitud.

La anulación no elimina el pedido. `pedido.estado` cambia de `REGISTRADO` a `ANULADO`; se conserva `fecha_anulacion` y se inserta el registro correspondiente en `anulacion_pedido` en la misma transacción local de H2. El endpoint `DELETE /orders/{pedidoId}` se mantiene por compatibilidad con la interfaz y las integraciones existentes.

| Respuesta | Significado |
|---|---|
| `201 Created` | Anulación confirmada por `POST /anulaciones`. |
| `200 OK` | Lista de anulaciones devuelta por `GET /anulaciones`. |
| `204 No Content` | Anulación confirmada por la ruta de compatibilidad DELETE. |
| `400 Bad Request` | El cuerpo no contiene un `pedidoId` positivo. |
| `404 Not Found` | ID inválido o pedido inexistente. |
| `409 Conflict` | El pedido ya estaba anulado. No se vuelve a solicitar una devolución. |
| `503 Service Unavailable` | Inventario no respondió; el pedido permanece registrado y se puede reintentar. |
| `502 Bad Gateway` | Inventario rechazó la compensación o respondió con datos inconsistentes. |

Cada pedido conserva un UUID de operación de anulación desde su creación. Inventario lo usa como clave única en `compensacion_stock` y bloquea el producto mientras procesa la devolución. Un reintento con el mismo UUID, pedido, código y cantidad devuelve el resultado previamente guardado sin incrementar stock otra vez; el UUID no colisiona si la secuencia numérica de H2 se reinicia. Esto permite recuperar el caso en que Inventario confirmó la transacción pero la respuesta se perdió. Las transacciones de H2 y SQL Server son independientes; no se simula una transacción distribuida.

El formulario MVC también permite registrar pedidos mediante `POST /sistema-pedidos/pedidos`.
En la tabla de pedidos, el botón **Devolver productos** anula un pedido registrado y devuelve su cantidad al inventario. Después de una devolución confirmada, el pedido deja de mostrarse en la tabla, pero permanece almacenado con su auditoría y estado de anulación.

## Pruebas manuales

Arranca primero Inventario y luego Pedidos; importa `postman/ISIL.postman_collection.json` o usa el archivo `../../Sesión 4/inventario-service/requests/inventario-service.http`.

1. Consulta stock inicial de `LAP-001`.
2. Registra un pedido por 2 unidades; comprueba que el stock baje y que GET orders muestre `estado: REGISTRADO`.
3. Envía `POST /api/v1/anulaciones` con el `pedidoId`; debe responder 201, dejar el pedido en estado `ANULADO` e incrementar el stock en 2.
4. Comprueba `GET /api/v1/anulaciones`; debe incluir la anulación recién creada. Repite el POST para ese pedido; debe responder 409 y el stock debe permanecer igual.
5. Detén Inventario y prueba una anulación para otro pedido; debe responder 503 y, una vez que Inventario vuelva a estar disponible, permitir reintentar la anulación.

Para SQL Server, crea una instalación nueva con `../../Sesión 4/inventario-service/database/01_crear_inventario.sql` y luego ejecuta `../../Sesión 4/inventario-service/database/04_crear_compensaciones.sql`. Para una base existente ejecuta el script 04 antes de anular pedidos.

## Pruebas y empaquetado

Ejecuta la validación completa con:

```powershell
.\mvnw.cmd clean verify
```

Inventario: desde su carpeta, ejecutar `mvn clean verify`. Pedidos: desde este módulo, ejecutar `.\mvnw.cmd clean verify`. Ambos comandos compilan, ejecutan pruebas y generan sus artefactos WAR. Las pruebas unitarias no sustituyen los escenarios manuales con ambos servicios levantados.

## Ubicación y control de versiones

El módulo forma parte del repositorio académico y no es un repositorio Git independiente. Verifica la rama del checkout antes de entregar; en esta revisión se confirmó `feature/sistema-pedidos-spring`.
