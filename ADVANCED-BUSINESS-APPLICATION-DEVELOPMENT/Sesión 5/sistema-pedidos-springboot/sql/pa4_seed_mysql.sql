USE pedidosdb;

DELETE FROM pedido_revision;
DELETE FROM pedido;

INSERT INTO pedido
  (cliente, producto_codigo, producto_nombre, precio_unitario, cantidad, total, fecha, revisado)
VALUES
  ('Ana Torres', 'LAP-001', 'Laptop', 2500.00, 1, 2500.00, NOW(), FALSE),
  ('Carlos Perez', 'MOU-001', 'Mouse', 50.00, 2, 100.00, NOW(), FALSE);

SELECT id, cliente, producto_codigo, producto_nombre, total, revisado
FROM pedido
ORDER BY id;
