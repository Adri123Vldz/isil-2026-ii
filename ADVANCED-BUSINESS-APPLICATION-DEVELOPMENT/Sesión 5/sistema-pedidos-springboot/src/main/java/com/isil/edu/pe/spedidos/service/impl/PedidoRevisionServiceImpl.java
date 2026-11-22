package com.isil.edu.pe.spedidos.service.impl;

import com.isil.edu.pe.spedidos.exception.PedidoRevisionException;
import com.isil.edu.pe.spedidos.repository.PedidoRevisionJdbcRepository;
import com.isil.edu.pe.spedidos.service.PedidoRevisionService;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PedidoRevisionServiceImpl implements PedidoRevisionService {
  private final PedidoRevisionJdbcRepository repository;

  public PedidoRevisionServiceImpl(PedidoRevisionJdbcRepository repository) {
    this.repository = repository;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void revisar(long pedidoId, String observacion) {
    if (pedidoId <= 0) {
      throw new PedidoRevisionException("El identificador del pedido debe ser mayor que cero.");
    }
    if (observacion == null || observacion.isBlank()) {
      throw new PedidoRevisionException("La observación es obligatoria.");
    }
    if (!repository.existePedido(pedidoId)) {
      throw new PedidoRevisionException("El pedido solicitado no existe.", 404);
    }
    if (repository.estaRevisado(pedidoId)) {
      throw new PedidoRevisionException("El pedido ya fue revisado.", 409);
    }

    int actualizado = repository.marcarRevisado(pedidoId);
    if (actualizado != 1) {
      throw new PedidoRevisionException("No se pudo confirmar la revisión del pedido.");
    }

    int insertado = repository.registrarRevision(
        pedidoId,
        observacion.trim(),
        LocalDateTime.now());
    if (insertado != 1) {
      throw new PedidoRevisionException("No se pudo registrar la trazabilidad del pedido.");
    }
  }
}
