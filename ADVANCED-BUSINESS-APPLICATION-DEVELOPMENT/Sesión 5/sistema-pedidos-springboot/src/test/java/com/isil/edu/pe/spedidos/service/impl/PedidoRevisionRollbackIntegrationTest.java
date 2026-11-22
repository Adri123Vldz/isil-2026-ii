package com.isil.edu.pe.spedidos.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.isil.edu.pe.spedidos.service.PedidoRevisionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("demo")
class PedidoRevisionRollbackIntegrationTest {
  @Autowired
  private PedidoRevisionService revisionService;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  private long pedidoId;

  @BeforeEach
  void setUp() {
    jdbcTemplate.update("DELETE FROM pedido_revision");
    jdbcTemplate.update("DELETE FROM pedido");
    jdbcTemplate.update(
        """
        INSERT INTO pedido (
          cliente, producto_codigo, producto_nombre,
          precio_unitario, cantidad, total, fecha, revisado
        ) VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, false)
        """,
        "Rollback Cliente", "P001", "Producto", 10.0, 1, 10.0);
    pedidoId = jdbcTemplate.queryForObject(
        "SELECT MAX(id) FROM pedido", Long.class);
  }

  @Test
  void fallaSegundoInsertReviertePrimeraActualizacion() {
    jdbcTemplate.update(
        "INSERT INTO pedido_revision (pedido_id, observacion, fecha_revision) VALUES (?, ?, CURRENT_TIMESTAMP)",
        pedidoId, "Fila previa");

    assertThrows(DataIntegrityViolationException.class,
        () -> revisionService.revisar(pedidoId, "Nueva observación"));

    Boolean revisado = jdbcTemplate.queryForObject(
        "SELECT revisado FROM pedido WHERE id = ?", Boolean.class, pedidoId);
    assertFalse(Boolean.TRUE.equals(revisado));
    assertEquals(1, jdbcTemplate.queryForObject(
        "SELECT COUNT(*) FROM pedido_revision WHERE pedido_id = ?",
        Integer.class, pedidoId));
  }
}
