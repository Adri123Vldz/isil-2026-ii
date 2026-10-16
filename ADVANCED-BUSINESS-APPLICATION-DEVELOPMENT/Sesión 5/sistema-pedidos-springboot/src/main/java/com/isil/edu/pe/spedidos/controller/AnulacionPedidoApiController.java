package com.isil.edu.pe.spedidos.controller;

import com.isil.edu.pe.spedidos.client.InventarioNoDisponibleException;
import com.isil.edu.pe.spedidos.controller.dto.AnulacionPedidoRequest;
import com.isil.edu.pe.spedidos.controller.dto.AnulacionPedidoResponse;
import com.isil.edu.pe.spedidos.entity.AnulacionPedido;
import com.isil.edu.pe.spedidos.exception.PedidoException;
import com.isil.edu.pe.spedidos.exception.PedidoNoEncontradoException;
import com.isil.edu.pe.spedidos.exception.PedidoYaAnuladoException;
import com.isil.edu.pe.spedidos.service.AnulacionPedidoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/anulaciones")
public class AnulacionPedidoApiController {

  private final AnulacionPedidoService anulacionPedidoService;

  public AnulacionPedidoApiController(AnulacionPedidoService anulacionPedidoService) {
    this.anulacionPedidoService = anulacionPedidoService;
  }

  @GetMapping
  public List<AnulacionPedidoResponse> listarAnulaciones() {
    return anulacionPedidoService.listarAnulaciones()
        .stream()
        .map(AnulacionPedidoResponse::from)
        .toList();
  }

  @PostMapping
  public ResponseEntity<AnulacionPedidoResponse> registrarAnulacion(
      @Valid @RequestBody AnulacionPedidoRequest request
  ) {
    AnulacionPedido anulacion = anulacionPedidoService.registrarAnulacion(
        request.getPedidoId(),
        request.getMotivo()
    );
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(AnulacionPedidoResponse.from(anulacion));
  }

  @ExceptionHandler(PedidoNoEncontradoException.class)
  public ResponseEntity<Void> pedidoNoEncontrado() {
    return ResponseEntity.notFound().build();
  }

  @ExceptionHandler(PedidoYaAnuladoException.class)
  public ResponseEntity<Void> pedidoYaAnulado() {
    return ResponseEntity.status(HttpStatus.CONFLICT).build();
  }

  @ExceptionHandler(InventarioNoDisponibleException.class)
  public ResponseEntity<Void> inventarioNoDisponible() {
    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
  }

  @ExceptionHandler(PedidoException.class)
  public ResponseEntity<Void> errorCompensacion() {
    return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
  }
}
