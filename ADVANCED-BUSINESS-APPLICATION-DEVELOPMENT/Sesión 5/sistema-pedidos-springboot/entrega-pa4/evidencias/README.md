# Evidencias PA4

Las evidencias deben obtenerse ejecutando las cinco aplicaciones:

1. `inventario-service` en `8082`
2. `sistema-pedidos-springboot` en `8081`
3. `pedidos-reportes-service` en `8083`

## Capturas obligatorias

### Caso 1 — Revisión correcta
- Antes: pedido con `revisado=false`.
- POST al formulario de revisión con observación válida.
- Después: `revisado=true` y una fila en `pedido_revision`.
- JSP mostrando el estado `REVISADO`.

### Caso 2 — Revisión duplicada
- Segundo POST del mismo pedido.
- Evidencia HTTP/flash: respuesta controlada `409` o mensaje equivalente.
- Base: sigue existiendo una sola fila en `pedido_revision`.

### Caso 3 — Validaciones e inexistencia
- Captura de `pedidoId <= 0`.
- Captura de observación vacía.
- Captura de ID inexistente.
- Confirmar que no aparecen escrituras parciales.

### Caso 4 — Rollback real
- Ejecutar `PedidoRevisionRollbackIntegrationTest`.
- Guardar `target/surefire-reports/...RollbackIntegrationTest.txt`.
- Evidencia SQL antes/después.

### Caso 5 — Servicio disponible/no disponible
- Reportes activos: capturar los dos endpoints y la JSP.
- Reportes detenidos: capturar mensaje de indisponibilidad en JSP.
- Confirmar que no cambia `pedido` ni `pedido_revision`.

## Comandos

```powershell
cd "ADVANCED-BUSINESS-APPLICATION-DEVELOPMENT/Sesión 5/sistema-pedidos-springboot"
.\mvnw.cmd test

cd "../../Sesión 12/pedidos-reportes-service"
.\mvnw.cmd test
```

Guardar los registros:

- `automatizadas/sistema-pedidos-mvn-test.log`
- `automatizadas/pedidos-reportes-mvn-test.log`
