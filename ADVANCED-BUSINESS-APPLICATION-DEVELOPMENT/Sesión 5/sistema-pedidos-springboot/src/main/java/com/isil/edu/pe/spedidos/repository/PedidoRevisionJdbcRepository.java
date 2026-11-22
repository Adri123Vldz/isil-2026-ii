package com.isil.edu.pe.spedidos.repository;

import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PedidoRevisionJdbcRepository {
  private final NamedParameterJdbcTemplate jdbc;

  public PedidoRevisionJdbcRepository(NamedParameterJdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  public boolean existePedido(long pedidoId) {
    Integer cantidad = jdbc.queryForObject(
        "SELECT COUNT(*) FROM pedido WHERE id = :pedidoId",
        Map.of("pedidoId", pedidoId),
        Integer.class);
    return cantidad != null && cantidad > 0;
  }

  public boolean estaRevisado(long pedidoId) {
    Boolean revisado = jdbc.queryForObject(
        "SELECT revisado FROM pedido WHERE id = :pedidoId",
        Map.of("pedidoId", pedidoId),
        Boolean.class);
    return Boolean.TRUE.equals(revisado);
  }

  public int marcarRevisado(long pedidoId) {
    return jdbc.update(
        "UPDATE pedido SET revisado = true WHERE id = :pedidoId AND revisado = false",
        Map.of("pedidoId", pedidoId));
  }

  public int registrarRevision(long pedidoId, String observacion, LocalDateTime fechaRevision) {
    return jdbc.update(
        """
        INSERT INTO pedido_revision (pedido_id, observacion, fecha_revision)
        VALUES (:pedidoId, :observacion, :fechaRevision)
        """,
        Map.of(
            "pedidoId", pedidoId,
            "observacion", observacion,
            "fechaRevision", fechaRevision));
  }
}
