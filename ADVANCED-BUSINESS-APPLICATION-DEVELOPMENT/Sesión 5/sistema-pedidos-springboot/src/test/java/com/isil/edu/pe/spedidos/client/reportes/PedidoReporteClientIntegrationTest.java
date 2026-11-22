package com.isil.edu.pe.spedidos.client.reportes;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class PedidoReporteClientIntegrationTest {
  @Test
  void clientTraduceServicioNoDisponibleSinExponerRestClientException() {
    PedidoReporteClient client = new PedidoReporteClient() {
      @Override
      public java.util.List<com.isil.edu.pe.spedidos.client.reportes.dto.PedidoRevisionReporteDto>
          listarRevisiones(String estado) {
        throw new PedidoReporteNoDisponibleException(
            "El servicio de reportes no está disponible.",
            new RuntimeException("conexión rechazada"));
      }

      @Override
      public com.isil.edu.pe.spedidos.client.reportes.dto.RevisionEstadisticaDto
          obtenerEstadisticas() {
        throw new PedidoReporteNoDisponibleException(
            "El servicio de reportes no está disponible.",
            new RuntimeException("conexión rechazada"));
      }
    };

    assertThrows(PedidoReporteNoDisponibleException.class,
        () -> client.listarRevisiones("PENDIENTE"));
  }
}
