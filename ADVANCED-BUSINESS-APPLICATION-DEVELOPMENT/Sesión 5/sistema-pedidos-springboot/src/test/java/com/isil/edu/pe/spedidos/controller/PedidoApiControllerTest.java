package com.isil.edu.pe.spedidos.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.isil.edu.pe.spedidos.service.PedidoService;
import com.isil.edu.pe.spedidos.client.InventarioNoDisponibleException;
import com.isil.edu.pe.spedidos.exception.PedidoYaAnuladoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class PedidoApiControllerTest {

  private PedidoService pedidoService;
  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    pedidoService = mock(PedidoService.class);
    mockMvc = MockMvcBuilders
        .standaloneSetup(new PedidoApiController(pedidoService))
        .build();
  }

  @Test
  void debeResponder204CuandoAnulaPedidoExistente() throws Exception {
    when(pedidoService.anularPedido(42L)).thenReturn(true);

    mockMvc.perform(delete("/api/v1/orders/{pedidoId}", 42))
        .andExpect(status().isNoContent());

    verify(pedidoService).anularPedido(42L);
  }

  @Test
  void debeResponder404CuandoElPedidoNoExiste() throws Exception {
    when(pedidoService.anularPedido(42L)).thenReturn(false);

    mockMvc.perform(delete("/api/v1/orders/{pedidoId}", 42))
        .andExpect(status().isNotFound());

    verify(pedidoService).anularPedido(42L);
  }

  @Test
  void debeResponder404CuandoElIdentificadorNoEsNumerico() throws Exception {
    mockMvc.perform(delete("/api/v1/orders/{pedidoId}", "invalido"))
        .andExpect(status().isNotFound());
  }

  @Test
  void debeResponder409CuandoElPedidoYaFueAnulado() throws Exception {
    doThrow(new PedidoYaAnuladoException(42L))
        .when(pedidoService).anularPedido(42L);

    mockMvc.perform(delete("/api/v1/orders/{pedidoId}", 42))
        .andExpect(status().isConflict());
  }

  @Test
  void debeResponder503CuandoInventarioNoEstaDisponible() throws Exception {
    doThrow(new InventarioNoDisponibleException("Sin conexión."))
        .when(pedidoService).anularPedido(42L);

    mockMvc.perform(delete("/api/v1/orders/{pedidoId}", 42))
        .andExpect(status().isServiceUnavailable());
  }
}
