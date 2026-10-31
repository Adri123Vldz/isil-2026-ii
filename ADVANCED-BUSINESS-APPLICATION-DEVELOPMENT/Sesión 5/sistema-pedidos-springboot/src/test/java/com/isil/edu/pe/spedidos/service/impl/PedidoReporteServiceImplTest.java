package com.isil.edu.pe.spedidos.service.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;

import com.isil.edu.pe.spedidos.repository.PedidoReporteJdbcRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PedidoReporteServiceImplTest {

  @Mock
  private PedidoReporteJdbcRepository jdbcRepository;

  private PedidoReporteServiceImpl service;

  @BeforeEach
  void setUp() {
    service = new PedidoReporteServiceImpl(jdbcRepository);
  }

  @Test
  void rejectBlankCustomerInAdvancedSearch() {
    assertThrows(
        IllegalArgumentException.class,
        () -> service.buscarPedidosPorClienteYTotalMinimo(
            " ",
            BigDecimal.ZERO
        )
    );
    verifyNoInteractions(jdbcRepository);
  }

  @Test
  void rejectNegativeMinimumOrderTotal() {
    assertThrows(
        IllegalArgumentException.class,
        () -> service.buscarPedidosPorClienteYTotalMinimo(
            "Ana",
            new BigDecimal("-0.01")
        )
    );
    verifyNoInteractions(jdbcRepository);
  }

  @Test
  void rejectFrequentCustomerThresholdBelowOne() {
    assertThrows(
        IllegalArgumentException.class,
        () -> service.listarClientesFrecuentes(0)
    );
    verifyNoInteractions(jdbcRepository);
  }
}
