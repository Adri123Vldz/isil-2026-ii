package com.isil.edu.pe.spedidos.controller;

import com.isil.edu.pe.spedidos.client.InventarioNoDisponibleException;
import com.isil.edu.pe.spedidos.controller.dto.PedidoResponse;
import com.isil.edu.pe.spedidos.entity.Pedido;
import com.isil.edu.pe.spedidos.exception.PedidoException;
import com.isil.edu.pe.spedidos.exception.PedidoYaAnuladoException;
import com.isil.edu.pe.spedidos.service.PedidoService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para consultar y anular pedidos.
 *
 * <p>El controlador depende de la abstracción {@link PedidoService} y recibe
 * su implementación mediante Constructor Injection.</p>
 *
 * <pre>
 * Petición HTTP
 *      |
 *      v
 * PedidoApiController
 *      |
 *      | Constructor Injection
 *      v
 * PedidoService
 *      |
 *      v
 * InventarioClient
 * </pre>
 */
@RestController
@RequestMapping("/api/v1/orders")
public class PedidoApiController {

  private final PedidoService pedidoService;

  /**
   * Construye el controlador utilizando el servicio proporcionado
   * por el contenedor IoC de Spring.
   *
   * @param pedidoService servicio de negocio de pedidos
   */
  public PedidoApiController(PedidoService pedidoService) {
    this.pedidoService = pedidoService;
  }

  /**
   * Obtiene los pedidos registrados.
   *
   * <p>El método no conoce cómo se realiza la consulta externa.
   * Únicamente delega la operación en {@link PedidoService}.</p>
   *
   * @return pedidos registrados
   */
  @GetMapping
  public List<PedidoResponse> listarPedidos() {
    return pedidoService.listarPedidos()
        .stream()
        .map(PedidoResponse::from)
        .toList();
  }

  /**
   * Anula un pedido existente y repone el stock en Inventario.
   *
   * @param pedidoId identificador del pedido que se desea anular
   * @return HTTP 204 si se anuló o HTTP 404 si el ID no es válido
   *         o el pedido no existe
   */
  @DeleteMapping("/{pedidoId}")
  public ResponseEntity<Void> anularPedido(@PathVariable String pedidoId) {
    final long id;
    try {
      id = Long.parseLong(pedidoId);
    } catch (NumberFormatException e) {
      return ResponseEntity.notFound().build();
    }

    if (!pedidoService.anularPedido(id)) {
      return ResponseEntity.notFound().build();
    }

    return ResponseEntity.noContent().build();
  }

  @org.springframework.web.bind.annotation.ExceptionHandler(PedidoYaAnuladoException.class)
  public ResponseEntity<Void> pedidoYaAnulado() {
    return ResponseEntity.status(HttpStatus.CONFLICT).build();
  }

  @org.springframework.web.bind.annotation.ExceptionHandler(InventarioNoDisponibleException.class)
  public ResponseEntity<Void> inventarioNoDisponible() {
    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
  }

  @org.springframework.web.bind.annotation.ExceptionHandler(PedidoException.class)
  public ResponseEntity<Void> errorCompensacion() {
    return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
  }
}
