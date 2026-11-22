package com.isil.edu.pe.pedidos.reportes.controller.dto;

import com.isil.edu.pe.pedidos.reportes.service.domain.PedidoRevisionReporte;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PedidoRevisionReporteDto(
    Long id,
    String cliente,
    String productoCodigo,
    String productoNombre,
    BigDecimal total,
    boolean revisado,
    String observacion,
    LocalDateTime fechaRevision
) {
  public static PedidoRevisionReporteDto from(PedidoRevisionReporte pedido) {
    return new PedidoRevisionReporteDto(
        pedido.id(),
        pedido.cliente(),
        pedido.productoCodigo(),
        pedido.productoNombre(),
        pedido.total(),
        pedido.revisado(),
        pedido.observacion(),
        pedido.fechaRevision());
  }
}
