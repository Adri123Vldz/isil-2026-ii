package com.isil.edu.pe.spedidos.repository;

import com.isil.edu.pe.spedidos.entity.AnulacionPedido;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnulacionPedidoRepository extends JpaRepository<AnulacionPedido, Long> {

  Optional<AnulacionPedido> findByPedidoId(Long pedidoId);

  List<AnulacionPedido> findAllByOrderByFechaAnulacionDesc();
}
