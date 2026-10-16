package com.isil.edu.pe.spedidos.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "anulacion_pedido",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_anulacion_pedido_pedido",
        columnNames = "pedido_id"
    )
)
public class AnulacionPedido {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "pedido_id", nullable = false)
  private Long pedidoId;

  @Column(nullable = false, length = 30)
  private String codigo;

  @Column(name = "cantidad_devuelta", nullable = false)
  private int cantidadDevuelta;

  @Column(name = "operacion_id", nullable = false, length = 36)
  private String operacionId;

  @Column(length = 255)
  private String motivo;

  @Column(name = "fecha_anulacion", nullable = false)
  private LocalDateTime fechaAnulacion;

  protected AnulacionPedido() {
  }

  public AnulacionPedido(
      Long pedidoId,
      String codigo,
      int cantidadDevuelta,
      String operacionId,
      String motivo,
      LocalDateTime fechaAnulacion
  ) {
    this.pedidoId = pedidoId;
    this.codigo = codigo;
    this.cantidadDevuelta = cantidadDevuelta;
    this.operacionId = operacionId;
    this.motivo = motivo;
    this.fechaAnulacion = fechaAnulacion;
  }

  public Long getId() {
    return id;
  }

  public Long getPedidoId() {
    return pedidoId;
  }

  public String getCodigo() {
    return codigo;
  }

  public int getCantidadDevuelta() {
    return cantidadDevuelta;
  }

  public String getOperacionId() {
    return operacionId;
  }

  public String getMotivo() {
    return motivo;
  }

  public LocalDateTime getFechaAnulacion() {
    return fechaAnulacion;
  }
}
