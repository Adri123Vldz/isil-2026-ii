package com.isil.edu.pe.pedidos.reportes.service;

import com.isil.edu.pe.pedidos.reportes.service.domain.Pedido;
import com.isil.edu.pe.pedidos.reportes.service.domain.ProductoVendido;
import com.isil.edu.pe.pedidos.reportes.service.domain.ResumenPedidos;
import java.util.List;

public interface PedidoReporteService {

  Pedido obtenerPorId(long id);

  List<Pedido> listarTodos();

  ResumenPedidos obtenerResumen();

  List<Pedido> buscarPorCliente(String cliente);

  List<ProductoVendido> listarProductosMasVendidos();

  List<com.isil.edu.pe.pedidos.reportes.service.domain.PedidoRevisionReporte> listarPorEstadoRevision(String estado);

  com.isil.edu.pe.pedidos.reportes.service.domain.RevisionEstadistica obtenerEstadisticaRevisiones();

}
