package pe.edu.isil.inventario.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class DevolucionStockRequest {

  @Positive(message = "El identificador del pedido debe ser mayor que cero.")
  private Long pedidoId;

  @Size(min = 36, max = 36, message = "El identificador de operación debe ser un UUID.")
  private String operacionId;

  @NotNull(message = "La cantidad es obligatoria.")
  @Positive(message = "La cantidad debe ser mayor que cero.")
  private Integer cantidad;

  public DevolucionStockRequest() {
  }

  public Long getPedidoId() {
    return pedidoId;
  }

  public void setPedidoId(Long pedidoId) {
    this.pedidoId = pedidoId;
  }

  public String getOperacionId() {
    return operacionId;
  }

  public void setOperacionId(String operacionId) {
    this.operacionId = operacionId;
  }

  public Integer getCantidad() {
    return cantidad;
  }

  public void setCantidad(Integer cantidad) {
    this.cantidad = cantidad;
  }

  @AssertTrue(message = "Los identificadores de pedido y operación deben enviarse juntos.")
  public boolean isIdentificacionCompleta() {
    return (pedidoId == null) == (operacionId == null);
  }
}
