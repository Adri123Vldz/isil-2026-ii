package com.isil.edu.pe.spedidos.controller.dto;

import com.isil.edu.pe.spedidos.entity.AnulacionPedido;
import java.time.LocalDateTime;

public record AnulacionPedidoResponse(
    Long id,
    Long pedidoId,
    String codigo,
    int cantidadDevuelta,
    String motivo,
    LocalDateTime fechaAnulacion
) {

  public static AnulacionPedidoResponse from(AnulacionPedido anulacion) {
    return new AnulacionPedidoResponse(
        anulacion.getId(),
        anulacion.getPedidoId(),
        anulacion.getCodigo(),
        anulacion.getCantidadDevuelta(),
        anulacion.getMotivo(),
        anulacion.getFechaAnulacion()
    );
  }
}
