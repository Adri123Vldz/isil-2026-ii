package pe.edu.isil.inventario.service;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import pe.edu.isil.inventario.domain.CompensacionStock;
import pe.edu.isil.inventario.domain.ProductoInventario;
import pe.edu.isil.inventario.dto.DevolucionStockResponse;
import java.util.UUID;

/**
 * Servicio EJB responsable de las reglas de negocio del inventario.
 */
@Stateless
public class InventarioService {

  @PersistenceContext(unitName = "InventarioPU")
  private EntityManager entityManager;

  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
  public List<ProductoInventario> listarProductos() {
    return entityManager
        .createQuery(
            """
            select p
            from ProductoInventario p
            order by p.id
            """,
            ProductoInventario.class
        )
        .getResultList();
  }

  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
  public ProductoInventario obtenerProducto(Long id) {
    if (id == null || id <= 0) {
      throw datosInvalidos("El identificador del producto es inválido.");
    }

    ProductoInventario producto = entityManager.find(ProductoInventario.class, id);
    if (producto == null) {
      throw productoNoEncontrado("No existe un producto de inventario con id " + id + ".");
    }
    return producto;
  }

  @TransactionAttribute(TransactionAttributeType.SUPPORTS)
  public ProductoInventario obtenerProductoPorCodigo(String codigo) {
    if (codigo == null || codigo.isBlank()) {
      throw datosInvalidos("El código del producto es obligatorio.");
    }

    List<ProductoInventario> resultado = entityManager
        .createQuery(
            """
            select p
            from ProductoInventario p
            where p.codigo = :codigo
            """,
            ProductoInventario.class
        )
        .setParameter("codigo", codigo.trim())
        .setMaxResults(1)
        .getResultList();

    if (resultado.isEmpty()) {
      throw productoNoEncontrado("No existe un producto de inventario con código " + codigo + ".");
    }
    return resultado.getFirst();
  }

  /**
   * Reserva stock dentro de una transacción local administrada por el contenedor.
   */
  @TransactionAttribute(TransactionAttributeType.REQUIRED)
  public ProductoInventario reservarStock(Long productoId, int cantidad) {
    return reservar(obtenerProducto(productoId), cantidad);
  }

  /**
   * Variante recomendada para integración entre sistemas: usa un código de negocio estable.
   */
  @TransactionAttribute(TransactionAttributeType.REQUIRED)
  public ProductoInventario reservarStockPorCodigo(String codigo, int cantidad) {
    return reservar(obtenerProductoPorCodigo(codigo), cantidad);
  }

  @TransactionAttribute(TransactionAttributeType.REQUIRED)
  public DevolucionStockResponse devolverStockPorCodigo(
      String codigo,
      Long pedidoId,
      String operacionId,
      int cantidad
  ) {
    if (pedidoId == null || pedidoId <= 0) {
      throw datosInvalidos("El identificador del pedido es inválido.");
    }
    if (cantidad <= 0) {
      throw datosInvalidos("La cantidad debe ser mayor que cero.");
    }
    if (!esUuidValido(operacionId)) {
      throw datosInvalidos("El identificador de operación debe ser un UUID válido.");
    }
    if (codigo == null || codigo.isBlank()) {
      throw datosInvalidos("El código del producto es obligatorio.");
    }

    ProductoInventario producto = buscarProductoPorCodigoConBloqueo(codigo.trim());
    List<CompensacionStock> compensaciones = entityManager
        .createQuery(
            "select c from CompensacionStock c where c.operacionId = :operacionId",
            CompensacionStock.class
        )
        .setParameter("operacionId", operacionId)
        .setMaxResults(1)
        .getResultList();

    if (!compensaciones.isEmpty()) {
      CompensacionStock existente = compensaciones.getFirst();
      if (!existente.getPedidoId().equals(pedidoId)
          || !existente.getCodigo().equals(producto.getCodigo())
          || existente.getCantidad() != cantidad) {
        throw new InventarioException(
            InventarioException.Tipo.CONFLICTO_CONCURRENCIA,
            "El pedido ya tiene una compensación registrada con datos diferentes."
        );
      }
      return toDevolucionResponse(existente);
    }

    try {
      producto.devolverStock(cantidad);
      CompensacionStock compensacion = new CompensacionStock(
          operacionId,
          pedidoId,
          producto.getCodigo(),
          cantidad,
          producto.getStock()
      );
      entityManager.persist(compensacion);
      entityManager.flush();
      return toDevolucionResponse(compensacion);
    } catch (IllegalArgumentException e) {
      throw datosInvalidos(e.getMessage());
    } catch (IllegalStateException e) {
      throw new InventarioException(
          InventarioException.Tipo.DATOS_INVALIDOS,
          e.getMessage()
      );
    } catch (OptimisticLockException e) {
      throw new InventarioException(
          InventarioException.Tipo.CONFLICTO_CONCURRENCIA,
          "El stock fue modificado por otra operación. Vuelva a consultar e intente nuevamente."
      );
    }
  }

  private ProductoInventario buscarProductoPorCodigoConBloqueo(String codigo) {
    List<ProductoInventario> resultado = entityManager
        .createQuery(
            "select p from ProductoInventario p where p.codigo = :codigo",
            ProductoInventario.class
        )
        .setParameter("codigo", codigo)
        .setLockMode(LockModeType.PESSIMISTIC_WRITE)
        .setMaxResults(1)
        .getResultList();

    if (resultado.isEmpty()) {
      throw productoNoEncontrado("No existe un producto de inventario con código " + codigo + ".");
    }
    return resultado.getFirst();
  }

  private static DevolucionStockResponse toDevolucionResponse(CompensacionStock compensacion) {
    return new DevolucionStockResponse(
        compensacion.getPedidoId(),
        compensacion.getOperacionId(),
        compensacion.getCodigo(),
        compensacion.getCantidad(),
        compensacion.getStockResultante()
    );
  }

  private static boolean esUuidValido(String operacionId) {
    if (operacionId == null) {
      return false;
    }
    try {
      return UUID.fromString(operacionId).toString().equalsIgnoreCase(operacionId);
    } catch (IllegalArgumentException e) {
      return false;
    }
  }

  private ProductoInventario reservar(ProductoInventario producto, int cantidad) {
    if (cantidad <= 0) {
      throw datosInvalidos("La cantidad debe ser mayor que cero.");
    }

    try {
      producto.reservarStock(cantidad);
      // Fuerza el UPDATE antes de abandonar el método para traducir aquí
      // un eventual conflicto de optimistic locking.
      entityManager.flush();
      return producto;
    } catch (IllegalArgumentException e) {
      throw datosInvalidos(e.getMessage());
    } catch (IllegalStateException e) {
      throw new InventarioException(
          InventarioException.Tipo.STOCK_INSUFICIENTE,
          e.getMessage()
      );
    } catch (OptimisticLockException e) {
      throw new InventarioException(
          InventarioException.Tipo.CONFLICTO_CONCURRENCIA,
          "El stock fue modificado por otra operación. Vuelva a consultar e intente nuevamente."
      );
    }
  }

  private static InventarioException datosInvalidos(String mensaje) {
    return new InventarioException(InventarioException.Tipo.DATOS_INVALIDOS, mensaje);
  }

  private static InventarioException productoNoEncontrado(String mensaje) {
    return new InventarioException(InventarioException.Tipo.PRODUCTO_NO_ENCONTRADO, mensaje);
  }
}
