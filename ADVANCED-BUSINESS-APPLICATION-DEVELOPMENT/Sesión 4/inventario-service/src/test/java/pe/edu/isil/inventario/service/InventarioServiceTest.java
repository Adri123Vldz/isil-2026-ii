package pe.edu.isil.inventario.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.TypedQuery;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import pe.edu.isil.inventario.domain.CompensacionStock;
import pe.edu.isil.inventario.domain.ProductoInventario;
import pe.edu.isil.inventario.dto.DevolucionStockResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InventarioServiceTest {

  private static final String OPERATION_ID = "6f1e5aa1-8443-4df9-a2e6-f23831cd987a";

  private EntityManager entityManager;
  private TypedQuery<ProductoInventario> productoQuery;
  private TypedQuery<CompensacionStock> compensacionQuery;
  private InventarioService service;
  private ProductoInventario producto;

  @BeforeEach
  void setUp() throws Exception {
    entityManager = mock(EntityManager.class);
    productoQuery = mock(TypedQuery.class);
    compensacionQuery = mock(TypedQuery.class);
    service = new InventarioService();
    Field field = InventarioService.class.getDeclaredField("entityManager");
    field.setAccessible(true);
    field.set(service, entityManager);
    producto = new ProductoInventario("LAP-001", "Laptop", new BigDecimal("2500.00"), 3);

    when(entityManager.createQuery(anyString(), eq(ProductoInventario.class)))
        .thenReturn(productoQuery);
    when(productoQuery.setParameter("codigo", "LAP-001")).thenReturn(productoQuery);
    when(productoQuery.setLockMode(LockModeType.PESSIMISTIC_WRITE)).thenReturn(productoQuery);
    when(productoQuery.setMaxResults(1)).thenReturn(productoQuery);
    when(productoQuery.getResultList()).thenReturn(List.of(producto));

    when(entityManager.createQuery(anyString(), eq(CompensacionStock.class)))
        .thenReturn(compensacionQuery);
    when(compensacionQuery.setParameter(eq("operacionId"), any())).thenReturn(compensacionQuery);
    when(compensacionQuery.setMaxResults(1)).thenReturn(compensacionQuery);
  }

  @Test
  void debeIncrementarStockYGuardarLaCompensacion() {
    when(compensacionQuery.getResultList()).thenReturn(List.of());

    DevolucionStockResponse response = service.devolverStockPorCodigo(
        "LAP-001",
        42L,
        OPERATION_ID,
        2
    );

    assertEquals(5, producto.getStock());
    assertEquals(42L, response.getPedidoId());
    assertEquals(OPERATION_ID, response.getOperacionId());
    assertEquals(5, response.getStockResultante());
    assertEquals(5, response.getStockRestante());
    verify(entityManager).persist(any(CompensacionStock.class));
    verify(entityManager).flush();
  }

  @Test
  void debeRepetirRespuestaSinIncrementarStockOtraVez() {
    CompensacionStock compensacion = new CompensacionStock(
        OPERATION_ID,
        42L,
        "LAP-001",
        2,
        5
    );
    when(compensacionQuery.getResultList()).thenReturn(List.of(compensacion));

    DevolucionStockResponse response = service.devolverStockPorCodigo(
        "LAP-001",
        42L,
        OPERATION_ID,
        2
    );

    assertEquals(3, producto.getStock());
    assertEquals(5, response.getStockResultante());
  }

  @Test
  void rechazaReutilizarPedidoConOtraCantidad() {
    when(compensacionQuery.getResultList())
        .thenReturn(List.of(new CompensacionStock(
            OPERATION_ID,
            42L,
            "LAP-001",
            2,
            5
        )));

    InventarioException exception = assertThrows(
        InventarioException.class,
        () -> service.devolverStockPorCodigo("LAP-001", 42L, OPERATION_ID, 3)
    );

    assertEquals(InventarioException.Tipo.CONFLICTO_CONCURRENCIA, exception.getTipo());
    assertEquals(3, producto.getStock());
  }
}
