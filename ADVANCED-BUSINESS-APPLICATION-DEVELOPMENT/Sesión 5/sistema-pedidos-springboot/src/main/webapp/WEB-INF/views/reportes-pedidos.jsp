<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<!doctype html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Reportes de pedidos - ISIL</title>
    <c:url var="cssUrl" value="/assets/css/app.css"/>
    <link rel="stylesheet" href="${cssUrl}">
</head>
<body>
<div class="contenedor">
    <header class="cabecera">
        <div>
            <p class="eyebrow">Spring JDBC · Consultas de solo lectura</p>
            <h1>Reportes de pedidos</h1>
            <p class="subtitulo">Resumen, búsqueda por cliente e identificación de clientes frecuentes.</p>
        </div>
        <nav>
            <c:url var="pedidosUrl" value="/pedidos"/>
            <a href="${pedidosUrl}">Volver a pedidos</a>
            <c:url var="logoutUrl" value="/logout"/>
            <form method="post" action="${logoutUrl}">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                <button type="submit">Cerrar sesión</button>
            </form>
        </nav>
    </header>

    <c:if test="${not empty error}">
        <div class="alerta error"><c:out value="${error}"/></div>
    </c:if>

    <section class="tarjeta">
        <h2>Resumen general</h2>
        <dl>
            <dt>Total de pedidos</dt>
            <dd><c:out value="${resumen.totalPedidos}"/></dd>
            <dt>Total de unidades</dt>
            <dd><c:out value="${resumen.totalUnidades}"/></dd>
            <dt>Importe acumulado</dt>
            <dd>S/ <fmt:formatNumber value="${resumen.importeAcumulado}" minFractionDigits="2" maxFractionDigits="2"/></dd>
            <dt>Ticket promedio</dt>
            <dd>S/ <fmt:formatNumber value="${resumen.ticketPromedio}" minFractionDigits="2" maxFractionDigits="2"/></dd>
        </dl>
    </section>

    <section class="tarjeta">
        <h2>Búsqueda avanzada</h2>
        <c:url var="reporteUrl" value="/reportes/pedidos"/>
        <form method="get" action="${reporteUrl}" class="form-grid">
            <label>
                Cliente
                <input name="cliente" value="${fn:escapeXml(clienteBuscado)}" required maxlength="120">
            </label>
            <label>
                Total mínimo
                <input name="totalMinimo" type="number" min="0" step="0.01" value="${totalMinimo}" required>
            </label>
            <button type="submit">Buscar pedidos</button>
        </form>

        <c:if test="${busquedaRealizada}">
            <div class="tabla-scroll">
                <table>
                    <thead>
                    <tr><th>ID</th><th>Cliente</th><th>Producto</th><th>Cantidad</th><th>Total</th><th>Fecha</th></tr>
                    </thead>
                    <tbody>
                    <c:forEach var="pedido" items="${pedidosFiltrados}">
                        <tr>
                            <td><c:out value="${pedido.id}"/></td>
                            <td><c:out value="${pedido.cliente}"/></td>
                            <td><c:out value="${pedido.productoNombre}"/></td>
                            <td><c:out value="${pedido.cantidad}"/></td>
                            <td>S/ <fmt:formatNumber value="${pedido.total}" minFractionDigits="2" maxFractionDigits="2"/></td>
                            <td><c:out value="${pedido.fecha}"/></td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty pedidosFiltrados}">
                        <tr><td colspan="6" class="sin-datos">No se encontraron pedidos con esos filtros.</td></tr>
                    </c:if>
                    </tbody>
                </table>
            </div>
        </c:if>
    </section>

    <section class="tarjeta">
        <h2>Clientes frecuentes</h2>
        <form method="get" action="${reporteUrl}" class="form-grid">
            <label>
                Mínimo de pedidos
                <input name="minPedidos" type="number" min="1" value="${minPedidos}" required>
            </label>
            <button type="submit">Consultar clientes</button>
        </form>

        <c:if test="${frecuenciaConsultada}">
            <div class="tabla-scroll">
                <table>
                    <thead>
                    <tr><th>Cliente</th><th>Pedidos</th><th>Unidades</th><th>Importe acumulado</th><th>Ticket promedio</th></tr>
                    </thead>
                    <tbody>
                    <c:forEach var="clienteFrecuente" items="${clientesFrecuentes}">
                        <tr>
                            <td><c:out value="${clienteFrecuente.cliente}"/></td>
                            <td><c:out value="${clienteFrecuente.totalPedidos}"/></td>
                            <td><c:out value="${clienteFrecuente.totalUnidades}"/></td>
                            <td>S/ <fmt:formatNumber value="${clienteFrecuente.importeAcumulado}" minFractionDigits="2" maxFractionDigits="2"/></td>
                            <td>S/ <fmt:formatNumber value="${clienteFrecuente.ticketPromedio}" minFractionDigits="2" maxFractionDigits="2"/></td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty clientesFrecuentes}">
                        <tr><td colspan="5" class="sin-datos">No hay clientes que alcancen ese mínimo.</td></tr>
                    </c:if>
                    </tbody>
                </table>
            </div>
        </c:if>
    </section>
</div>
</body>
</html>
