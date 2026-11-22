package com.isil.edu.pe.spedidos.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "pedido_revision")
public class PedidoRevision {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "pedido_id", nullable = false, unique = true)
  private Pedido pedido;

  @Column(nullable = false, length = 500)
  private String observacion;

  @Column(name = "fecha_revision", nullable = false)
  private LocalDateTime fechaRevision;

  public PedidoRevision() {}

  public PedidoRevision(Pedido pedido, String observacion, LocalDateTime fechaRevision) {
    this.pedido = pedido;
    this.observacion = observacion;
    this.fechaRevision = fechaRevision;
  }

  public Long getId() { return id; }
  public Pedido getPedido() { return pedido; }
  public String getObservacion() { return observacion; }
  public LocalDateTime getFechaRevision() { return fechaRevision; }
}
