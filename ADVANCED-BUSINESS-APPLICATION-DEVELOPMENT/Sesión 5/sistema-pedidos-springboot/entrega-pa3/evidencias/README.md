# Evidencias PA3

## Resultado de validación

Se ejecutó `mvnw clean test` con el perfil `test` y H2: **36 pruebas, 0 fallos, 0 errores y 0 omitidas**. **16 pruebas nuevas de PA3** se agregaron sobre la rama base del docente. El log y los reportes XML de Surefire están en `automatizadas/`.

La ejecución manual contra el entorno solicitado sigue bloqueada: MySQL no está instalado como servicio/comando en este equipo y los puertos 3306, 8081 y 8082 no aceptan conexiones. Docker tampoco está disponible. Node.js, Java y Maven sí están disponibles.

Las evidencias automatizadas usan H2 y no se presentan como capturas manuales ni como verificación contra MySQL. No se han creado capturas ficticias.

## Matriz de los cinco escenarios

| Caso | Evidencia automatizada | Ejecución manual MySQL/servicios |
|---|---|---|
| 1. Cliente y monto mínimo | Pruebas de repositorio JDBC, endpoint JSON y vista con H2/MockMvc | Pendiente; no hubo MySQL ni aplicación ejecutándose |
| 2. Clientes frecuentes | Pruebas de `GROUP BY`/`HAVING`, cuatro métricas y endpoint JSON con H2 | Pendiente; falta contraste con consultas y datos reales en MySQL |
| 3. Parámetros inválidos | HTTP 400 para cliente vacío, monto negativo y mínimo de pedidos menor a uno | Pendiente de capturas manuales; API cubierta automáticamente |
| 4. Inventario indisponible | Conexión HTTP real rechazada: el alta lanza `InventarioNoDisponibleException`, no persiste y el reporte local sigue disponible | Pendiente con el inventario real detenido y MySQL en funcionamiento |
| 5. Roles y CSRF | `CONSULTA` accede a reportes, anónimo es redirigido, `CONSULTA` no registra pedidos y `OPERADOR` sin CSRF es rechazado | Pendiente de capturas/manual con las credenciales locales |

## Preparación reproducible

1. Instalar/iniciar MySQL y crear la base y el usuario de laboratorio usando el script suministrado por el docente; no copiar contraseñas personales a evidencias.
2. Configurar `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `PEDIDOS_OPERADOR_PASSWORD` y `PEDIDOS_CONSULTA_PASSWORD` en el entorno local.
3. Iniciar MySQL, luego `inventario-service` en el puerto 8082 y por último Sistema de Pedidos en el puerto 8081.
4. Iniciar sesión con el rol apropiado y realizar las solicitudes indicadas en el README.
5. Guardar capturas originales con nombres `caso-1-busqueda.png` a `caso-5-seguridad-csrf.png`. Registrar parámetros, hora, estado HTTP y datos de contraste sin exponer claves.
6. Para el caso 4, iniciar los tres servicios, detener solo `inventario-service`, comprobar que el alta falla controladamente y que el reporte local sigue disponible; iniciarlo otra vez al terminar.

Para cerrar la evidencia manual requerida, ejecutar los cinco casos en un equipo con MySQL y ambos servicios disponibles y guardar las capturas originales junto a esta matriz.
