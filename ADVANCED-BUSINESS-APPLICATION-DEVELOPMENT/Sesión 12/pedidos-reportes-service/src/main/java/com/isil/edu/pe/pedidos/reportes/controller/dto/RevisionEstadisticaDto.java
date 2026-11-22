package com.isil.edu.pe.pedidos.reportes.controller.dto;

import com.isil.edu.pe.pedidos.reportes.service.domain.RevisionEstadistica;
import java.math.BigDecimal;

public record RevisionEstadisticaDto(
    long totalPedidos,
    long totalRevisados,
    long totalPendientes,
    BigDecimal porcentajeRevisado
) {
  public static RevisionEstadisticaDto from(RevisionEstadistica value) {
    return new RevisionEstadisticaDto(
        value.totalPedidos(),
        value.totalRevisados(),
        value.totalPendientes(),
        value.porcentajeRevisado());
  }
}
