package com.isil.edu.pe.spedidos.service.impl;

import com.isil.edu.pe.spedidos.entity.AnulacionPedido;
import com.isil.edu.pe.spedidos.exception.PedidoNoEncontradoException;
import com.isil.edu.pe.spedidos.repository.AnulacionPedidoRepository;
import com.isil.edu.pe.spedidos.service.AnulacionPedidoService;
import com.isil.edu.pe.spedidos.service.PedidoService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnulacionPedidoServiceImpl implements AnulacionPedidoService {

  private final PedidoService pedidoService;
  private final AnulacionPedidoRepository anulacionPedidoRepository;

  public AnulacionPedidoServiceImpl(
      PedidoService pedidoService,
      AnulacionPedidoRepository anulacionPedidoRepository
  ) {
    this.pedidoService = pedidoService;
    this.anulacionPedidoRepository = anulacionPedidoRepository;
  }

  @Override
  @Transactional
  public AnulacionPedido registrarAnulacion(long pedidoId, String motivo) {
    if (pedidoId <= 0) {
      throw new PedidoNoEncontradoException(pedidoId);
    }
    if (!pedidoService.anularPedido(pedidoId, motivo)) {
      throw new PedidoNoEncontradoException(pedidoId);
    }
    return anulacionPedidoRepository.findByPedidoId(pedidoId)
        .orElseThrow(() -> new IllegalStateException(
            "La anulación se completó, pero no se encontró su registro persistido."
        ));
  }

  @Override
  @Transactional(readOnly = true)
  public List<AnulacionPedido> listarAnulaciones() {
    return anulacionPedidoRepository.findAllByOrderByFechaAnulacionDesc();
  }
}
