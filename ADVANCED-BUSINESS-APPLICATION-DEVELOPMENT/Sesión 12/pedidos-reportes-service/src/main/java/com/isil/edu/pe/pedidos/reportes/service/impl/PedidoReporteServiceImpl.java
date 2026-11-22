package com.isil.edu.pe.pedidos.reportes.service.impl;

import com.isil.edu.pe.pedidos.reportes.exception.PedidoNoEncontradoException;
import com.isil.edu.pe.pedidos.reportes.repository.PedidoReporteJdbcRepository;
import com.isil.edu.pe.pedidos.reportes.repository.projection.ResumenPedidosProjection;
import com.isil.edu.pe.pedidos.reportes.service.PedidoReporteService;
import com.isil.edu.pe.pedidos.reportes.service.domain.Pedido;
import com.isil.edu.pe.pedidos.reportes.service.domain.ProductoVendido;
import com.isil.edu.pe.pedidos.reportes.service.domain.ResumenPedidos;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Implementación del servicio de reporte de pedidos.
 */
@Service
public class PedidoReporteServiceImpl implements PedidoReporteService {

  private final PedidoReporteJdbcRepository jdbcRepository;

  public PedidoReporteServiceImpl(PedidoReporteJdbcRepository jdbcRepository) {
      this.jdbcRepository = jdbcRepository;
  }

  @Override
  public Pedido obtenerPorId(long id) {
    return jdbcRepository.buscarPorId(id).orElseThrow(() -> new PedidoNoEncontradoException(id));
  }

  @Override
  public List<Pedido> listarTodos() {
    return jdbcRepository.listarTodos();
  }

  @Override
  public ResumenPedidos obtenerResumen() {
    ResumenPedidosProjection resumenPedidos = jdbcRepository.obtenerResumen();
    return new ResumenPedidos(resumenPedidos.totalPedidos(), resumenPedidos.totalUnidades(),
        resumenPedidos.importeAcumulado(), resumenPedidos.ticketPromedio());
  }

  @Override
  public List<Pedido> buscarPorCliente(String cliente) {
    if (cliente == null || cliente.isBlank()) {
      return List.of();
    }

    return jdbcRepository.buscarPorCliente(cliente);
  }

  @Override
  public List<ProductoVendido> listarProductosMasVendidos() {
    return jdbcRepository.listarProductosMasVendidos()
        .stream()
        .map(item ->
            new ProductoVendido(
                item.productoCodigo(),
                item.productoNombre(),
                item.totalPedidos(),
                item.totalUnidades(),
                item.importeAcumulado()
            )
        )
        .toList();
  }

  @Override
  public List<com.isil.edu.pe.pedidos.reportes.service.domain.PedidoRevisionReporte> listarPorEstadoRevision(
      String estado) {
    if (!"PENDIENTE".equals(estado) && !"REVISADO".equals(estado)) {
      throw new IllegalArgumentException("Estado inválido. Use PENDIENTE o REVISADO.");
    }
    return jdbcRepository.listarPorEstadoRevision(estado);
  }

  @Override
  public com.isil.edu.pe.pedidos.reportes.service.domain.RevisionEstadistica obtenerEstadisticaRevisiones() {
    return jdbcRepository.obtenerEstadisticaRevisiones();
  }
}
