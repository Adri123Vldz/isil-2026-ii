package com.isil.edu.pe.spedidos.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.isil.edu.pe.spedidos.entity.AnulacionPedido;
import com.isil.edu.pe.spedidos.exception.PedidoNoEncontradoException;
import com.isil.edu.pe.spedidos.exception.PedidoYaAnuladoException;
import com.isil.edu.pe.spedidos.service.AnulacionPedidoService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class AnulacionPedidoApiControllerTest {

  private AnulacionPedidoService anulacionPedidoService;
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    anulacionPedidoService = mock(AnulacionPedidoService.class);
    mockMvc = MockMvcBuilders
        .standaloneSetup(new AnulacionPedidoApiController(anulacionPedidoService))
        .build();
  }

  @Test
  void debeCrearAnulacionDesdeElEndpointSolicitado() throws Exception {
    when(anulacionPedidoService.registrarAnulacion(42L, "Cliente lo solicitó"))
        .thenReturn(new AnulacionPedido(
            42L,
            "LAP-001",
            2,
            "6f1e5aa1-8443-4df9-a2e6-f23831cd987a",
            "Cliente lo solicitó",
            LocalDateTime.parse("2026-10-08T21:48:19")
        ));

    mockMvc.perform(post("/api/v1/anulaciones")
            .contentType("application/json")
            .content("""
                {"pedidoId":42,"motivo":"Cliente lo solicitó"}
                """))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.pedidoId").value(42))
        .andExpect(jsonPath("$.codigo").value("LAP-001"))
        .andExpect(jsonPath("$.cantidadDevuelta").value(2));

    verify(anulacionPedidoService).registrarAnulacion(42L, "Cliente lo solicitó");
  }

  @Test
  void debeListarLasAnulaciones() throws Exception {
    when(anulacionPedidoService.listarAnulaciones()).thenReturn(List.of(
        new AnulacionPedido(
            42L,
            "LAP-001",
            2,
            "6f1e5aa1-8443-4df9-a2e6-f23831cd987a",
            null,
            LocalDateTime.parse("2026-10-08T21:48:19")
        )
    ));

    mockMvc.perform(get("/api/v1/anulaciones"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].pedidoId").value(42))
        .andExpect(jsonPath("$[0].cantidadDevuelta").value(2));
  }

  @Test
  void debeResponder404CuandoElPedidoNoExiste() throws Exception {
    when(anulacionPedidoService.registrarAnulacion(404L, null))
        .thenThrow(new PedidoNoEncontradoException(404L));

    mockMvc.perform(post("/api/v1/anulaciones")
            .contentType("application/json")
            .content("""
                {"pedidoId":404}
                """))
        .andExpect(status().isNotFound());
  }

  @Test
  void debeResponder409CuandoElPedidoYaFueAnulado() throws Exception {
    when(anulacionPedidoService.registrarAnulacion(42L, null))
        .thenThrow(new PedidoYaAnuladoException(42L));

    mockMvc.perform(post("/api/v1/anulaciones")
            .contentType("application/json")
            .content("""
                {"pedidoId":42}
                """))
        .andExpect(status().isConflict());
  }

  @Test
  void debeRechazarIdentificadorDePedidoNoPositivo() throws Exception {
    mockMvc.perform(post("/api/v1/anulaciones")
            .contentType("application/json")
            .content("""
                {"pedidoId":0}
                """))
        .andExpect(status().isBadRequest());
  }
}
