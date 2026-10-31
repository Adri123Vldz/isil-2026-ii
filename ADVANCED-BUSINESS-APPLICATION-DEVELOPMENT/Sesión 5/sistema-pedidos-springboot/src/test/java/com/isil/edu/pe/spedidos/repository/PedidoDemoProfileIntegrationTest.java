package com.isil.edu.pe.spedidos.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.isil.edu.pe.spedidos.repository.projection.ClienteFrecuenteProjection;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("demo")
class PedidoDemoProfileIntegrationTest {

  @Autowired
  private PedidoReporteJdbcRepository repository;

  @Test
  @DisplayName("Load the reproducible demo orders into the H2 demo profile")
  void loadReproducibleDemoOrdersIntoH2DemoProfile() {
    assertEquals(5, repository.obtenerResumen().totalPedidos());
  }

  @Test
  @DisplayName("Run advanced search and frequent-customer aggregation against demo data")
  void runReportQueriesAgainstDemoData() {
    var pedidosAna = repository.buscarPedidosPorClienteYTotalMinimo(
        "Ana",
        new BigDecimal("1000.00")
    );
    List<ClienteFrecuenteProjection> clientes =
        repository.listarClientesFrecuentes(2);
    ClienteFrecuenteProjection ana = clientes.stream()
        .filter(cliente -> "Ana Torres".equals(cliente.cliente()))
        .findFirst()
        .orElse(null);

    assertEquals(1, pedidosAna.size());
    assertEquals(0, new BigDecimal("5000.00").compareTo(pedidosAna.get(0).getTotal()));
    assertEquals(2, clientes.size());
    assertNotNull(ana);
    assertEquals(3L, ana.totalPedidos());
    assertEquals(8L, ana.totalUnidades());
    assertEquals(0, new BigDecimal("5580.00").compareTo(ana.importeAcumulado()));
    assertEquals(0, new BigDecimal("1860.00").compareTo(ana.ticketPromedio()));
  }
}
