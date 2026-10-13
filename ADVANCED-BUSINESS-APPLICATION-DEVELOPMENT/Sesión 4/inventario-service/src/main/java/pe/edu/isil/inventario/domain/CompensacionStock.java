package pe.edu.isil.inventario.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "compensacion_stock")
public class CompensacionStock {

  @Id
  @Column(name = "operacion_id", nullable = false, length = 36)
  private String operacionId;

  @Column(name = "pedido_id", nullable = false)
  private Long pedidoId;

  @Column(nullable = false, length = 30)
  private String codigo;

  @Column(nullable = false)
  private int cantidad;

  @Column(name = "stock_resultante", nullable = false)
  private int stockResultante;

  @Column(name = "fecha_compensacion", nullable = false)
  private LocalDateTime fechaCompensacion;

  protected CompensacionStock() {
  }

  public CompensacionStock(
      String operacionId,
      Long pedidoId,
      String codigo,
      int cantidad,
      int stockResultante
  ) {
    this.operacionId = operacionId;
    this.pedidoId = pedidoId;
    this.codigo = codigo;
    this.cantidad = cantidad;
    this.stockResultante = stockResultante;
    this.fechaCompensacion = LocalDateTime.now();
  }

  public Long getPedidoId() {
    return pedidoId;
  }

  public String getOperacionId() {
    return operacionId;
  }

  public String getCodigo() {
    return codigo;
  }

  public int getCantidad() {
    return cantidad;
  }

  public int getStockResultante() {
    return stockResultante;
  }

  public LocalDateTime getFechaCompensacion() {
    return fechaCompensacion;
  }
}
