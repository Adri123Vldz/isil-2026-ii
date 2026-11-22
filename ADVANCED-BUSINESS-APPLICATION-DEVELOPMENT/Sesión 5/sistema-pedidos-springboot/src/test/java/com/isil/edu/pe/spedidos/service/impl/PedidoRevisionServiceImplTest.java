package com.isil.edu.pe.spedidos.service.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.isil.edu.pe.spedidos.exception.PedidoRevisionException;
import com.isil.edu.pe.spedidos.repository.PedidoRevisionJdbcRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class PedidoRevisionServiceImplTest {
  @Mock
  private PedidoRevisionJdbcRepository repository;
  private PedidoRevisionServiceImpl service;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    service = new PedidoRevisionServiceImpl(repository);
  }

  @Test
  void revisionValidaActualizaPedidoYRegistraTrazabilidad() {
    when(repository.existePedido(10L)).thenReturn(true);
    when(repository.estaRevisado(10L)).thenReturn(false);
    when(repository.marcarRevisado(10L)).thenReturn(1);
    when(repository.registrarRevision(
        org.mockito.ArgumentMatchers.eq(10L),
        org.mockito.ArgumentMatchers.eq("Pedido correcto"),
        org.mockito.ArgumentMatchers.any())).thenReturn(1);

    service.revisar(10L, "Pedido correcto");

    verify(repository).marcarRevisado(10L);
    verify(repository).registrarRevision(
        org.mockito.ArgumentMatchers.eq(10L),
        org.mockito.ArgumentMatchers.eq("Pedido correcto"),
        org.mockito.ArgumentMatchers.any());
  }

  @Test
  void revisionDuplicadaSeRechazaSinEscribir() {
    when(repository.existePedido(20L)).thenReturn(true);
    when(repository.estaRevisado(20L)).thenReturn(true);

    assertThrows(PedidoRevisionException.class,
        () -> service.revisar(20L, "Intento duplicado"));

    verify(repository, never()).marcarRevisado(20L);
    verify(repository, never()).registrarRevision(
        org.mockito.ArgumentMatchers.eq(20L),
        org.mockito.ArgumentMatchers.anyString(),
        org.mockito.ArgumentMatchers.any());
  }
}
