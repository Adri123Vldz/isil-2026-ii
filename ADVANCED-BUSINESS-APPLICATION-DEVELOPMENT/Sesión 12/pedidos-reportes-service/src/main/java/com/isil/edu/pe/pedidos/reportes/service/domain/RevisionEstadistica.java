package com.isil.edu.pe.pedidos.reportes.service.domain;

import java.math.BigDecimal;

public record RevisionEstadistica(
    long totalPedidos,
    long totalRevisados,
    long totalPendientes,
    BigDecimal porcentajeRevisado
) {}
