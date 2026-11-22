package com.isil.edu.pe.pedidos.reportes.service.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PedidoRevisionReporte(
    Long id,
    String cliente,
    String productoCodigo,
    String productoNombre,
    BigDecimal total,
    boolean revisado,
    String observacion,
    LocalDateTime fechaRevision
) {}
