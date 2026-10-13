package com.isil.edu.pe.spedidos.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "pedido_auditoria")
public class PedidoAuditoria {

  @Id
  @Column(name = "pedido_id", nullable = false)
  private Long pedidoId;

  @Column(nullable = false, length = 30)
  private String productoCodigo;

  @Column(nullable = false)
  private int cantidad;

  @Column(nullable = false, length = 30)
  private String accion;

  @Column(name = "operacion_id", nullable = false, length = 36)
  private String operacionId;

  @Column(nullable = false)
  private LocalDateTime fecha;

  protected PedidoAuditoria() {
  }

  public PedidoAuditoria(
      Long pedidoId,
      String operacionId,
      String productoCodigo,
      int cantidad,
      LocalDateTime fecha
  ) {
    this.pedidoId = pedidoId;
    this.operacionId = operacionId;
    this.productoCodigo = productoCodigo;
    this.cantidad = cantidad;
    this.accion = "ANULACION";
    this.fecha = fecha;
  }

  public Long getPedidoId() {
    return pedidoId;
  }

  public String getProductoCodigo() {
    return productoCodigo;
  }

  public int getCantidad() {
    return cantidad;
  }

  public String getAccion() {
    return accion;
  }

  public String getOperacionId() {
    return operacionId;
  }

  public LocalDateTime getFecha() {
    return fecha;
  }
}
