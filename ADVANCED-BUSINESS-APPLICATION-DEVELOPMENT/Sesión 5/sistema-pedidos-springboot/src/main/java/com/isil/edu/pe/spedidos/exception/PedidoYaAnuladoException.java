package com.isil.edu.pe.spedidos.exception;

public class PedidoYaAnuladoException extends RuntimeException {

  public PedidoYaAnuladoException(long pedidoId) {
    super("El pedido " + pedidoId + " ya está anulado.");
  }
}
