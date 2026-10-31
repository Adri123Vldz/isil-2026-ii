# Evidencias PA3

## Estado al generar esta carpeta

- **Automatizadas:** `mvnw clean test` se ejecutó sobre el proyecto con el perfil `test` y H2. Se guardará el registro de Maven y los reportes XML de Surefire en esta carpeta.
- **Manuales:** no ejecutadas durante la preparación de la entrega. En la comprobación local MySQL (`localhost:3306`), Sistema de Pedidos (`localhost:8081`) e `inventario-service` (`localhost:8082`) no estaban escuchando. No se incluyen capturas simuladas ni resultados atribuidos a servicios apagados.

## Matriz de los cinco escenarios requeridos

| Caso | Evidencia que debe guardarse al ejecutarlo | Estado |
|---|---|---|
| 1. Cliente y monto mínimo | Captura de la JSP, respuesta JSON del mismo filtro y consulta de contraste en MySQL | Pendiente de ejecución manual |
| 2. Clientes frecuentes | Captura/JSON con `minPedidos=2` y consulta que contraste las cuatro métricas | Pendiente de ejecución manual |
| 3. Parámetros inválidos | Respuestas HTTP 400 para cliente vacío, monto negativo y umbral menor a 1 | Cobertura automatizada de monto/umbral; falta la comprobación manual de los tres valores |
| 4. Inventario indisponible | Evidencia del alta controlada con inventario detenido y reportes disponibles; restaurar inventario al final | Pendiente de ejecución manual |
| 5. Roles y CSRF | Anónimo redirigido, `CONSULTA` autorizado a reportes y rechazado al POST; `OPERADOR` rechazado sin CSRF | Cobertura automatizada de acceso a reportes/anónimo y controles de seguridad existentes; falta la comprobación manual |

## Preparación reproducible

1. Crear la base y el usuario de laboratorio usando el script MySQL suministrado por el docente; no copiar contraseñas personales a evidencias.
2. Configurar `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `PEDIDOS_OPERADOR_PASSWORD` y `PEDIDOS_CONSULTA_PASSWORD` en el entorno local.
3. Iniciar MySQL, luego `inventario-service` en el puerto 8082 y por último Sistema de Pedidos en el puerto 8081.
4. Iniciar sesión con el rol apropiado y realizar las solicitudes indicadas en el README.
5. Guardar capturas originales con nombres `caso-1-busqueda.png` a `caso-5-seguridad-csrf.png`. Registrar parámetros, hora, estado HTTP y datos de contraste sin exponer claves.
6. Para el caso 4, detener solo `inventario-service`, comprobar que el alta falla controladamente y que el reporte local sigue disponible; iniciarlo otra vez al terminar.

La suite automatizada es evidencia de pruebas con H2. No sustituye las capturas requeridas de los casos ejecutados con MySQL e inventario-service.
