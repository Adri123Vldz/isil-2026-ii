# pedidos-reportes-service

Servicio Spring Boot REST para consultas de pedidos y reportes sobre MySQL.

## Arranque

```powershell
.\mvnw.cmd spring-boot:run
```

Puerto predeterminado: `8083` con contexto `/pedidos-reportes-service`.

## Recursos PA4

```text
GET /api/v1/pedidos/revisiones?estado=PENDIENTE
GET /api/v1/pedidos/revisiones?estado=REVISADO
GET /api/v1/pedidos/estadisticas/revisiones
```

Ejemplo de estadísticas:

```json
{
  "totalPedidos": 10,
  "totalRevisados": 4,
  "totalPendientes": 6,
  "porcentajeRevisado": 40.00
}
```

Parámetros inválidos retornan HTTP `400`. Los datos provienen de Spring JDBC y no existen
excepciones JPA compartidas con el cliente web.

## Configuración

```properties
DB_URL=jdbc:mysql://localhost:3306/pedidosdb?serverTimezone=America/Lima
DB_USERNAME=<usuario>
DB_PASSWORD=<password>
```

## Pruebas

```powershell
.\mvnw.cmd test
```
