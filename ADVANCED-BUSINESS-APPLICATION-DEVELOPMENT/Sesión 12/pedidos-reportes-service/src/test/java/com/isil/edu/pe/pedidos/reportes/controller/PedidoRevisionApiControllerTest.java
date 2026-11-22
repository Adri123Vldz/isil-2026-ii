package com.isil.edu.pe.pedidos.reportes.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.isil.edu.pe.pedidos.reportes.controller.dto.PedidoRevisionReporteDto;
import com.isil.edu.pe.pedidos.reportes.controller.dto.RevisionEstadisticaDto;
import com.isil.edu.pe.pedidos.reportes.service.PedidoReporteService;
import com.isil.edu.pe.pedidos.reportes.service.domain.PedidoRevisionReporte;
import com.isil.edu.pe.pedidos.reportes.service.domain.RevisionEstadistica;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class PedidoRevisionApiControllerTest {
  private PedidoReporteService service;
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    service = Mockito.mock(PedidoReporteService.class);
    mockMvc = MockMvcBuilders
        .standaloneSetup(new PedidoApiController(service))
        .build();
  }

  @Test
  void listadoPorEstadoRetornaJson() throws Exception {
    when(service.listarPorEstadoRevision("PENDIENTE")).thenReturn(List.of(
        new PedidoRevisionReporte(
            7L, "Ana", "P001", "Teclado",
            BigDecimal.valueOf(100), false, null, null)));

    mockMvc.perform(get("/api/v1/orders/revisiones").param("estado", "PENDIENTE"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(7))
        .andExpect(jsonPath("$[0].revisado").value(false));
  }

  @Test
  void estadisticasRetornanContratoJson() throws Exception {
    when(service.obtenerEstadisticaRevisiones()).thenReturn(
        new RevisionEstadistica(10, 4, 6, BigDecimal.valueOf(40)));

    mockMvc.perform(get("/api/v1/orders/estadisticas/revisiones"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalPedidos").value(10))
        .andExpect(jsonPath("$.totalRevisados").value(4))
        .andExpect(jsonPath("$.porcentajeRevisado").value(40));
  }
}
