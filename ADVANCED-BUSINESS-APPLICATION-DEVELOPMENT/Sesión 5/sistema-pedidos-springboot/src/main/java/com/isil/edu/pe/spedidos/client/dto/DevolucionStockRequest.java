package com.isil.edu.pe.spedidos.client.dto;

public record DevolucionStockRequest(Long pedidoId, String operacionId, int cantidad) {
}
