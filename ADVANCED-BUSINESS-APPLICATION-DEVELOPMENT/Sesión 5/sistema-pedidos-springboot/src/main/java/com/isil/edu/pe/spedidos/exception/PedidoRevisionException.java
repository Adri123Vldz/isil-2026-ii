package com.isil.edu.pe.spedidos.exception;

public class PedidoRevisionException extends RuntimeException {
  private final int status;

  public PedidoRevisionException(String message) {
    this(message, 400);
  }

  public PedidoRevisionException(String message, int status) {
    super(message);
    this.status = status;
  }

  public int getStatus() {
    return status;
  }
}
