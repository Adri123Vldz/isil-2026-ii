package com.isil.edu.pe.spedidos.client.reportes;

import com.isil.edu.pe.spedidos.client.reportes.dto.PedidoRevisionReporteDto;
import com.isil.edu.pe.spedidos.client.reportes.dto.RevisionEstadisticaDto;
import java.util.List;

public interface PedidoReporteClient {
  List<PedidoRevisionReporteDto> listarRevisiones(String estado);
  RevisionEstadisticaDto obtenerEstadisticas();
}
