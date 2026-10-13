# Informe de entrega — Sistema de Pedidos Spring Boot

## Resultado

La cancelación conserva el pedido, devuelve el stock mediante Inventario, guarda el estado `CANCELADO` y registra una auditoría local en H2. Las llamadas repetidas no duplican el stock: Inventario trata la devolución como idempotente por `pedidoId`, mientras que Pedidos responde `409 Conflict` cuando el pedido ya está cancelado.

## Flujo y manejo de fallos

1. Pedidos obtiene el pedido con bloqueo pesimista para serializar cancelaciones concurrentes.
2. Llama a `POST /api/inventario/productos/codigo/{codigo}/devoluciones` con `pedidoId`, UUID `operacionId` y `cantidad`.
3. Inventario bloquea el producto y registra la compensación en la misma transacción que incrementa el stock. El UUID persistente es clave primaria en `compensacion_stock`.
4. Solo tras una respuesta coherente, Pedidos actualiza el estado y la fecha y persiste `pedido_auditoria` en la transacción local.
5. Si Inventario no está disponible, la transacción local revierte y el pedido queda activo (`503`). Un reintento es seguro incluso si la primera respuesta remota se perdió. Los almacenes de ambos servicios son independientes; no se usa una transacción distribuida.

## Archivos modificados o añadidos

### Inventario (`../../Sesión 4/inventario-service` relativo a este módulo)

- `domain/ProductoInventario.java`: operación de incremento de stock validada.
- `domain/CompensacionStock.java`: registro idempotente por pedido.
- `service/InventarioService.java`: bloqueo, validación, persistencia y repetición segura.
- `api/ProductoResource.java`: endpoint REST de devolución.
- `dto/DevolucionStockRequest.java`, `dto/DevolucionStockResponse.java`: contrato HTTP.
- `src/main/resources/META-INF/persistence.xml`: registro de la nueva entidad.
- `database/04_crear_compensaciones.sql`: actualización requerida para SQL Server existente o nuevo.
- `openapi/inventario-service.yaml`, `requests/inventario-service.http`, `src/main/webapp/index.html`, `README.md`: contrato y operación documentados.
- `src/test/java/pe/edu/isil/inventario/domain/ProductoInventarioTest.java`: casos de devolución y validación.

### Pedidos (`Sesión 5/sistema-pedidos-springboot`)

- `entity/Pedido.java`: estado, fecha y UUID persistente de la operación de anulación.
- `entity/PedidoAuditoria.java`: evento de cancelación persistido en H2.
- `repository/PedidoRepository.java`: consulta con bloqueo para evitar doble cancelación concurrente.
- `repository/PedidoAuditoriaRepository.java`: persistencia del evento de auditoría.
- `client/InventarioClient.java`, `client/InventarioRestClient.java`, `client/dto/DevolucionStockRequest.java`, `client/dto/DevolucionStockResponse.java`: integración HTTP.
- `service/PedidoService.java`, `service/impl/PedidoServiceImpl.java`: coordinación de compensación y persistencia local.
- `exception/PedidoYaAnuladoException.java`, `controller/PedidoApiController.java`: respuesta 409 para repetición, 503 para indisponibilidad y 502 para rechazo/respuesta remota inconsistente.
- `controller/dto/PedidoResponse.java`: expone `estado` y `fechaAnulacion`.
- Pruebas del servicio y MockMvc cubren anulación, duplicado, ausencia e indisponibilidad.
- `README.md`, `arquitectura-anulacion.puml` y Postman: guía, diagrama y solicitud de API.

## Códigos HTTP

| Caso | Resultado |
|---|---:|
| Pedido existente compensado | `204 No Content` |
| ID inválido o pedido inexistente | `404 Not Found` |
| Pedido previamente anulado | `409 Conflict` |
| Inventario fuera de servicio | `503 Service Unavailable` |
| Inventario rechaza datos o devuelve resultado inconsistente | `502 Bad Gateway` |

## Comprobaciones y evidencia

Verificaciones ejecutadas después de los cambios:

```powershell
# Sesión 4/inventario-service
mvn clean verify

# Sesión 5/sistema-pedidos-springboot
.\mvnw.cmd clean verify
```

- Inventario: `BUILD SUCCESS`; 7 pruebas ejecutadas, 0 fallos, 0 errores.
- Pedidos: `BUILD SUCCESS`; 15 pruebas ejecutadas, 0 fallos, 0 errores; WAR generado.
- `git diff --check` sin errores; la colección Postman pasó la validación JSON.

Los cinco escenarios manuales están definidos en el README, pero requieren SQL Server/WildFly y ambos servicios en ejecución; no se incluyen capturas ficticias ni se afirma que se haya hecho una llamada HTTP real.

## Estado de repositorio y limitaciones

El checkout comprobado es `isil-2026-ii`, rama `feature/sistema-pedidos-spring`. Los dos módulos académicos están en carpetas hermanas dentro de este único repositorio, no en repositorios Git individuales. No se reestructuraron remotos ni se crearon repositorios separados.
