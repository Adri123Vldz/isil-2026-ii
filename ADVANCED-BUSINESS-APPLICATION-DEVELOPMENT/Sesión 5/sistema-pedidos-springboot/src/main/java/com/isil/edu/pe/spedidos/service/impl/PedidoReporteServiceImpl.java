package com.isil.edu.pe.spedidos.service.impl;

import com.isil.edu.pe.spedidos.entity.Pedido;
import com.isil.edu.pe.spedidos.repository.PedidoReporteJdbcRepository;
import com.isil.edu.pe.spedidos.repository.projection.ClienteFrecuenteProjection;
import com.isil.edu.pe.spedidos.repository.projection.ResumenPedidosProjection;
import com.isil.edu.pe.spedidos.service.PedidoReporteService;
import com.isil.edu.pe.spedidos.service.domain.ClienteFrecuente;
import com.isil.edu.pe.spedidos.service.domain.ProductoVendido;
import com.isil.edu.pe.spedidos.service.domain.ResumenPedidos;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PedidoReporteServiceImpl implements PedidoReporteService {

  private final PedidoReporteJdbcRepository jdbcRepository;

  public PedidoReporteServiceImpl(PedidoReporteJdbcRepository jdbcRepository) {
      this.jdbcRepository = jdbcRepository;
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
  public List<Pedido> buscarPedidosPorClienteYTotalMinimo(
      String cliente,
      BigDecimal totalMinimo) {
    validarCliente(cliente);
    if (totalMinimo == null || totalMinimo.signum() < 0) {
      throw new IllegalArgumentException(
          "El total mínimo debe ser mayor o igual a cero."
      );
    }

    return jdbcRepository.buscarPedidosPorClienteYTotalMinimo(
        cliente,
        totalMinimo
    );
  }

  @Override
  public List<ClienteFrecuente> listarClientesFrecuentes(int minPedidos) {
    if (minPedidos < 1) {
      throw new IllegalArgumentException(
          "El mínimo de pedidos debe ser al menos 1."
      );
    }

    return jdbcRepository.listarClientesFrecuentes(minPedidos)
        .stream()
        .map(PedidoReporteServiceImpl::toClienteFrecuente)
        .toList();
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

  private static ClienteFrecuente toClienteFrecuente(
      ClienteFrecuenteProjection cliente) {
    return new ClienteFrecuente(
        cliente.cliente(),
        cliente.totalPedidos(),
        cliente.totalUnidades(),
        cliente.importeAcumulado(),
        cliente.ticketPromedio()
    );
  }

  private static void validarCliente(String cliente) {
    if (cliente == null || cliente.isBlank()) {
      throw new IllegalArgumentException("El cliente es obligatorio.");
    }
  }
}
