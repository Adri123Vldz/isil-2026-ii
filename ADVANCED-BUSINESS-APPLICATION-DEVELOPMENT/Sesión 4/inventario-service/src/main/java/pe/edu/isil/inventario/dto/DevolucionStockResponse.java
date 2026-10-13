package pe.edu.isil.inventario.dto;

public class DevolucionStockResponse {

  private Long pedidoId;
  private String operacionId;
  private String codigo;
  private int cantidadDevuelta;
  private int stockResultante;

  public DevolucionStockResponse() {
  }

  public DevolucionStockResponse(
      Long pedidoId,
      String operacionId,
      String codigo,
      int cantidadDevuelta,
      int stockResultante
  ) {
    this.pedidoId = pedidoId;
    this.operacionId = operacionId;
    this.codigo = codigo;
    this.cantidadDevuelta = cantidadDevuelta;
    this.stockResultante = stockResultante;
  }

  public Long getPedidoId() {
    return pedidoId;
  }

  public String getCodigo() {
    return codigo;
  }

  public String getOperacionId() {
    return operacionId;
  }

  public int getCantidadDevuelta() {
    return cantidadDevuelta;
  }

  public int getStockResultante() {
    return stockResultante;
  }
}
