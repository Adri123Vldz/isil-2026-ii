package com.isil.edu.pe.spedidos.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.isil.edu.pe.spedidos.client.InventarioNoDisponibleException;
import com.isil.edu.pe.spedidos.repository.PedidoReporteJdbcRepository;
import com.isil.edu.pe.spedidos.service.PedidoService;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PedidoInventarioNoDisponibleIntegrationTest {

  @Autowired
  private PedidoService pedidoService;

  @Autowired
  private PedidoReporteJdbcRepository reporteRepository;

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @DynamicPropertySource
  static void configureUnavailableInventory(DynamicPropertyRegistry registry) {
    registry.add(
        "inventario.api.base-url",
        () -> "http://127.0.0.1:1/inventario-service/api/inventario"
    );
  }

  @BeforeEach
  void setUp() {
    jdbcTemplate.update("DELETE FROM pedido");
    jdbcTemplate.update(
        """
        INSERT INTO pedido (
          cliente,
          producto_codigo,
          producto_nombre,
          precio_unitario,
          cantidad,
          total,
          fecha
        ) VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)
        """,
        "Ana Torres",
        "LAP-001",
        "Laptop",
        new BigDecimal("2500.00"),
        1,
        new BigDecimal("2500.00")
    );
  }

  @Test
  @DisplayName("Fail order creation but keep local reports available when inventory connection is refused")
  void failOrderCreationButKeepLocalReportsAvailableWhenInventoryConnectionIsRefused() {
    assertThrows(
        InventarioNoDisponibleException.class,
        () -> pedidoService.registrarPedido("Carlos Perez", "LAP-001", 1)
    );

    assertEquals(1, jdbcTemplate.queryForObject(
        "SELECT COUNT(*) FROM pedido",
        Integer.class
    ));
    assertEquals(1L, reporteRepository.obtenerResumen().totalPedidos());
  }
}
