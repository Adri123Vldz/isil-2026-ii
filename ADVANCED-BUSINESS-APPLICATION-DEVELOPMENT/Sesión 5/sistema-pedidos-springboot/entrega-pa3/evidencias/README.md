# Evidencias PA3

## Resultado de validación

Se ejecutó `mvnw clean test`: **38 pruebas, 0 fallos, 0 errores y 0 omitidas**. **18 pruebas nuevas de PA3** se agregaron sobre la rama base del docente. El log y los reportes XML de Surefire están en `automatizadas/`.

El proyecto incluye ahora un perfil local `demo` con H2 y cinco pedidos reproducibles. Dos pruebas de integración cargan el contexto Spring bajo ese perfil y verifican los datos de muestra y las consultas de PA3. Esto facilita una demostración local sin MySQL, pero **no equivale a capturas HTTP/manuales ni valida compatibilidad con MySQL**. MySQL no está instalado como servicio/comando en este equipo, el puerto 3306 no acepta conexiones y no se confirmó Sistema de Pedidos escuchando en el puerto 8081. Docker tampoco está disponible.

Se capturaron tres pantallas de la aplicación local autenticada como `CONSULTA`: resumen general con el diseño alineado, búsqueda de Ana con total mínimo S/ 1,000 y clientes frecuentes desde dos pedidos. Están en `demostracion-h2/`; son evidencia visual auténtica de la ejecución con H2 y datos semilla, no de MySQL.

Como comprobación parcial adicional del 31/10/2026, se inició temporalmente `inventario-service` con SQL Server Express y `InventarioDB`. El catálogo respondió HTTP 200 con tres productos; las consultas de stock de `LAP-001`, `MON-001` y `TEC-001` también respondieron HTTP 200 y devolvieron stocks 10, 20 y 30. Solo se hicieron solicitudes GET: no se reservaron productos ni se modificó el inventario. El servicio se cerró al terminar la comprobación. Esto valida el servicio de inventario por separado, no la integración con Pedidos ni los escenarios manuales de PA3.

Las evidencias automatizadas usan H2 y no se presentan como capturas manuales ni como verificación contra MySQL. No se han creado capturas ficticias.

## Matriz de los cinco escenarios

| Caso | Evidencia automatizada | Ejecución manual MySQL/servicios |
|---|---|---|
| 1. Cliente y monto mínimo | Repositorio JDBC, JSON y vista con H2/MockMvc; prueba de integración verifica el conjunto reproducible del perfil `demo` | Pendiente de recorrido HTTP/captura y contraste con MySQL |
| 2. Clientes frecuentes | `GROUP BY`/`HAVING`, cuatro métricas y endpoint JSON con H2; perfil `demo` verifica 3 pedidos, 8 unidades, S/ 5,580 y ticket promedio S/ 1,860 para Ana Torres | Pendiente de recorrido HTTP/captura y contraste con MySQL |
| 3. Parámetros inválidos | HTTP 400 para cliente vacío, monto negativo y mínimo de pedidos menor a uno en pruebas MockMvc | Pendiente de captura HTTP/manual |
| 4. Inventario indisponible | Conexión HTTP rechazada en prueba: el alta lanza `InventarioNoDisponibleException`, no persiste y el reporte local sigue disponible. El inventario se consultó por separado con GET 200 | Pendiente de recorrido coordinado Pedidos/inventario y captura con MySQL |
| 5. Roles y CSRF | Pruebas MockMvc: `CONSULTA` accede a reportes, anónimo es redirigido, `CONSULTA` no registra pedidos y `OPERADOR` sin CSRF es rechazado | Pendiente de recorrido HTTP/captura con las credenciales locales |

## Preparación reproducible

Para una demostración local sin MySQL, ejecutar `.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=demo"` desde el módulo. El perfil crea cinco registros en H2 cada vez que arranca y permite comprobar las consultas y la vista; no acredita el comportamiento de MySQL.

Para completar la evidencia manual solicitada, instalar/iniciar MySQL, crear la base y el usuario de laboratorio, configurar `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `PEDIDOS_OPERADOR_PASSWORD` y `PEDIDOS_CONSULTA_PASSWORD`, e iniciar MySQL, `inventario-service` y Sistema de Pedidos. Ejecutar los cinco casos con los roles adecuados y guardar capturas originales `caso-1-busqueda.png` a `caso-5-seguridad-csrf.png`, sin exponer claves. Para el caso 4, detener solo el inventario, comprobar el rechazo controlado y la continuidad del reporte local, y volver a iniciarlo al terminar.
