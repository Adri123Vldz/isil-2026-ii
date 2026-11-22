<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!doctype html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>Supervisión de pedidos</title>
  <c:url var="cssUrl" value="/assets/css/app.css"/>
  <link rel="stylesheet" href="${cssUrl}">
</head>
<body>
<div class="contenedor">
  <header class="cabecera">
    <h1>Supervisión de pedidos</h1>
  </header>

  <c:if test="${not empty error}"><div class="alerta error"><c:out value="${error}"/></div></c:if>
  <c:if test="${not empty exito}"><div class="alerta exito"><c:out value="${exito}"/></div></c:if>

  <c:if test="${not empty estadisticas}">
    <section class="tarjeta">
      Total: <c:out value="${estadisticas.totalPedidos}"/> |
      Revisados: <c:out value="${estadisticas.totalRevisados}"/> |
      Pendientes: <c:out value="${estadisticas.totalPendientes}"/> |
      Porcentaje: <fmt:formatNumber value="${estadisticas.porcentajeRevisado}" maxFractionDigits="2"/>%
    </section>
  </c:if>

  <section class="tarjeta">
    <form method="get" action="<c:url value='/supervision/pedidos'/>">
      <select name="estado">
        <option value="PENDIENTE" ${estado eq 'PENDIENTE' ? 'selected' : ''}>Pendientes</option>
        <option value="REVISADO" ${estado eq 'REVISADO' ? 'selected' : ''}>Revisados</option>
      </select>
      <button type="submit">Filtrar</button>
    </form>

    <table>
      <thead>
      <tr><th>ID</th><th>Cliente</th><th>Producto</th><th>Total</th><th>Estado</th><th>Acción</th></tr>
      </thead>
      <tbody>
      <c:forEach var="pedido" items="${pedidos}">
        <tr>
          <td><c:out value="${pedido.id}"/></td>
          <td><c:out value="${pedido.cliente}"/></td>
          <td><c:out value="${pedido.productoNombre}"/></td>
          <td>S/ <fmt:formatNumber value="${pedido.total}" minFractionDigits="2"/></td>
          <td><c:out value="${pedido.revisado ? 'REVISADO' : 'PENDIENTE'}"/></td>
          <td>
            <c:choose>
              <c:when test="${!pedido.revisado}">
                <form method="post" action="<c:url value='/supervision/pedidos/${pedido.id}/revisar'/>">
                  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                  <input name="observacion" required maxlength="500" placeholder="Observación">
                  <button type="submit">Revisar</button>
                </form>
              </c:when>
              <c:otherwise>
                <c:out value="${pedido.observacion}"/>
              </c:otherwise>
            </c:choose>
          </td>
        </tr>
      </c:forEach>
      </tbody>
    </table>
  </section>
</div>
</body>
</html>
