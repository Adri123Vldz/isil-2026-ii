package com.isil.edu.pe.spedidos.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.isil.edu.pe.spedidos.client.InventarioClient;
import com.isil.edu.pe.spedidos.client.InventarioClientException;
import com.isil.edu.pe.spedidos.client.InventarioNoDisponibleException;
import com.isil.edu.pe.spedidos.client.dto.ReservaStockResponse;
import com.isil.edu.pe.spedidos.client.dto.DevolucionStockResponse;
import com.isil.edu.pe.spedidos.entity.Pedido;
import com.isil.edu.pe.spedidos.entity.AnulacionPedido;
import com.isil.edu.pe.spedidos.entity.PedidoEstado;
import com.isil.edu.pe.spedidos.exception.PedidoException;
import com.isil.edu.pe.spedidos.exception.PedidoYaAnuladoException;
import com.isil.edu.pe.spedidos.repository.AnulacionPedidoRepository;
import com.isil.edu.pe.spedidos.repository.PedidoRepository;
import java.math.BigDecimal;
import java.util.Optional;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias pedagógicas de {@link PedidoServiceImpl}.
 *
 * <p>Estas pruebas muestran una ventaja de Constructor Injection: el servicio
 * puede construirse directamente con dependencias simuladas, sin levantar el
 * ApplicationContext de Spring ni ejecutar {@code inventario-service}.</p>
 *
 * <pre>
 * PedidoRepository mock ----\
 *                           +--> new PedidoServiceImpl(...)
 * InventarioClient mock ----/
 * </pre>
 */
class PedidoServiceImplTest {

  private PedidoRepository pedidoRepository;
  private AnulacionPedidoRepository anulacionPedidoRepository;
  private InventarioClient inventarioClient;
  private PedidoServiceImpl pedidoService;

  /**
   * Crea mocks de las dependencias y construye manualmente el servicio antes
   * de cada prueba. Spring no participa en esta inicialización.
   */
  @BeforeEach
  void setUp() {
    pedidoRepository = mock(PedidoRepository.class);
    anulacionPedidoRepository = mock(AnulacionPedidoRepository.class);
    inventarioClient = mock(InventarioClient.class);

    pedidoService = new PedidoServiceImpl(
        pedidoRepository,
        anulacionPedidoRepository,
        inventarioClient
    );
  }

  /**
   * Verifica el flujo exitoso: Inventario devuelve precio y producto, el
   * servicio calcula el total y delega la persistencia al Repository.
   */
  @Test
  void debeRegistrarPedidoYCalcularTotal() {
    ReservaStockResponse reserva = nuevaReserva(
        "LAP-001",
        "Laptop",
        new BigDecimal("2500.00")
    );

    when(inventarioClient.reservarStock("LAP-001", 2))
        .thenReturn(reserva);

    when(pedidoRepository.save(any(Pedido.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Pedido pedido = pedidoService.registrarPedido(
        "Ana Torres",
        "LAP-001",
        2
    );

    assertEquals("Ana Torres", pedido.getCliente());
    assertEquals("LAP-001", pedido.getProductoCodigo());
    assertEquals(new BigDecimal("2500.00"), pedido.getPrecioUnitario());
    assertEquals(new BigDecimal("5000.00"), pedido.getTotal());

    verify(inventarioClient).reservarStock("LAP-001", 2);
    verify(pedidoRepository).save(any(Pedido.class));
  }

  /**
   * Verifica que una cantidad inválida sea rechazada antes de invocar las
   * dependencias externas o la persistencia.
   */
  @Test
  void debeRechazarCantidadMenorOIgualACero() {
    PedidoException error = assertThrows(
        PedidoException.class,
        () -> pedidoService.registrarPedido("Ana Torres", "LAP-001", 0)
    );

    assertEquals("La cantidad debe ser mayor que cero.", error.getMessage());
    verifyNoInteractions(inventarioClient, pedidoRepository);
  }

  /**
   * Verifica que un error funcional HTTP 4xx se traduzca a PedidoException y
   * conserve la excepción original como causa para facilitar el diagnóstico.
   */
  @Test
  void debeConservarCausaDelErrorFuncionalDeInventario() {
    InventarioClientException causa = new InventarioClientException(
        409,
        "Stock insuficiente."
    );

    when(inventarioClient.reservarStock("LAP-001", 5))
        .thenThrow(causa);

    PedidoException error = assertThrows(
        PedidoException.class,
        () -> pedidoService.registrarPedido("Ana Torres", "LAP-001", 5)
    );

    assertEquals("Stock insuficiente.", error.getMessage());
    assertSame(causa, error.getCause());
    verifyNoInteractions(pedidoRepository);
  }

  /**
   * Verifica que un HTTP 5xx remoto sea tratado como indisponibilidad técnica
   * y no como un error de datos ingresados por el usuario.
   */
  @Test
  void debeTratarError5xxComoServicioNoDisponible() {
    InventarioClientException causa = new InventarioClientException(
        500,
        "Error interno de inventario."
    );

    when(inventarioClient.reservarStock("LAP-001", 1))
        .thenThrow(causa);

    InventarioNoDisponibleException error = assertThrows(
        InventarioNoDisponibleException.class,
        () -> pedidoService.registrarPedido("Ana Torres", "LAP-001", 1)
    );

    assertSame(causa, error.getCause());
    verifyNoInteractions(pedidoRepository);
  }

  @Test
  void debeAnularPedidoExistente() {
    Pedido pedido = new Pedido(
        "Ana Torres",
        "LAP-001",
        "Laptop",
        new BigDecimal("2500.00"),
        1,
        new BigDecimal("2500.00")
    );
    pedido.setId(42L);
    when(pedidoRepository.findByIdForUpdate(42L)).thenReturn(Optional.of(pedido));
    when(inventarioClient.devolverStock(
        42L,
        pedido.getOperacionAnulacionId(),
        "LAP-001",
        1
    )).thenReturn(new DevolucionStockResponse(
        42L,
        pedido.getOperacionAnulacionId(),
        "LAP-001",
        1,
        6
    ));

    boolean anulado = pedidoService.anularPedido(42L);

    assertTrue(anulado);
    assertEquals(PedidoEstado.ANULADO, pedido.getEstado());
    assertTrue(pedido.getFechaAnulacion() != null);
    verify(pedidoRepository).findByIdForUpdate(42L);
    verify(inventarioClient).devolverStock(
        42L,
        pedido.getOperacionAnulacionId(),
        "LAP-001",
        1
    );
    verify(pedidoRepository).save(pedido);
    verify(anulacionPedidoRepository).save(any(AnulacionPedido.class));
  }

  @Test
  void debeInformarCuandoElPedidoNoExiste() {
    when(pedidoRepository.findByIdForUpdate(42L)).thenReturn(Optional.empty());

    boolean anulado = pedidoService.anularPedido(42L);

    assertFalse(anulado);
    verify(pedidoRepository).findByIdForUpdate(42L);
    verifyNoInteractions(inventarioClient, anulacionPedidoRepository);
  }

  @Test
  void debeRechazarIdentificadorInvalidoSinConsultarElRepositorio() {
    boolean anulado = pedidoService.anularPedido(0L);

    assertFalse(anulado);
    verifyNoInteractions(pedidoRepository, inventarioClient, anulacionPedidoRepository);
  }

  @Test
  void noDebeAnularDosVecesElMismoPedido() {
    Pedido pedido = new Pedido(
        "Ana Torres",
        "LAP-001",
        "Laptop",
        new BigDecimal("2500.00"),
        1,
        new BigDecimal("2500.00")
    );
    pedido.setId(42L);
    pedido.anular(LocalDateTime.now());
    when(pedidoRepository.findByIdForUpdate(42L)).thenReturn(Optional.of(pedido));

    assertThrows(PedidoYaAnuladoException.class, () -> pedidoService.anularPedido(42L));
    verifyNoInteractions(inventarioClient, anulacionPedidoRepository);
  }

  @Test
  void conservaPedidoRegistradoSiInventarioNoPuedeCompletarLaDevolucion() {
    Pedido pedido = new Pedido(
        "Ana Torres",
        "LAP-001",
        "Laptop",
        new BigDecimal("2500.00"),
        1,
        new BigDecimal("2500.00")
    );
    pedido.setId(42L);
    when(pedidoRepository.findByIdForUpdate(42L)).thenReturn(Optional.of(pedido));
    when(inventarioClient.devolverStock(
        42L,
        pedido.getOperacionAnulacionId(),
        "LAP-001",
        1
    ))
        .thenThrow(new InventarioNoDisponibleException("Sin conexión."));

    assertThrows(
        InventarioNoDisponibleException.class,
        () -> pedidoService.anularPedido(42L)
    );

    assertEquals(PedidoEstado.REGISTRADO, pedido.getEstado());
    verifyNoInteractions(anulacionPedidoRepository);
  }

  private ReservaStockResponse nuevaReserva(
      String codigo,
      String nombre,
      BigDecimal precio
  ) {
    ReservaStockResponse reserva = new ReservaStockResponse();
    reserva.setCodigo(codigo);
    reserva.setNombre(nombre);
    reserva.setPrecio(precio);
    return reserva;
  }
}