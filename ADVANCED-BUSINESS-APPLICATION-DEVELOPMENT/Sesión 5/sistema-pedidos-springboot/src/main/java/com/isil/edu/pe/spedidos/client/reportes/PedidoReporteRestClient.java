package com.isil.edu.pe.spedidos.client.reportes;

import com.isil.edu.pe.spedidos.client.reportes.dto.PedidoRevisionReporteDto;
import com.isil.edu.pe.spedidos.client.reportes.dto.RevisionEstadisticaDto;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class PedidoReporteRestClient implements PedidoReporteClient {
  private final RestClient restClient;
  private final String baseUrl;

  public PedidoReporteRestClient(
      @Qualifier("pedidoReporteHttpClient") RestClient restClient,
      @Value("${pedidos-reportes.api.base-url}") String baseUrl) {
    this.restClient = restClient;
    this.baseUrl = baseUrl;
  }

  @Override
  public List<PedidoRevisionReporteDto> listarRevisiones(String estado) {
    if (!"PENDIENTE".equals(estado) && !"REVISADO".equals(estado)) {
      throw new PedidoReporteNoDisponibleException(
          "El estado solicitado no es válido.", new IllegalArgumentException(estado));
    }
    try {
      return restClient.get()
          .uri(baseUrl + "/api/v1/pedidos/revisiones?estado={estado}", estado)
          .retrieve()
          .body(new ParameterizedTypeReference<>() {});
    } catch (ResourceAccessException | RestClientResponseException e) {
      throw new PedidoReporteNoDisponibleException(
          "El servicio de reportes no está disponible.", e);
    }
  }

  @Override
  public RevisionEstadisticaDto obtenerEstadisticas() {
    try {
      return restClient.get()
          .uri(baseUrl + "/api/v1/pedidos/estadisticas/revisiones")
          .retrieve()
          .body(RevisionEstadisticaDto.class);
    } catch (ResourceAccessException | RestClientResponseException e) {
      throw new PedidoReporteNoDisponibleException(
          "El servicio de reportes no está disponible.", e);
    }
  }
}
