package com.isil.edu.pe.spedidos.exception;

public class PedidoNoEncontradoException extends RuntimeException {

  public PedidoNoEncontradoException(long pedidoId) {
    super("No existe el pedido con identificador " + pedidoId + ".");
  }
}
