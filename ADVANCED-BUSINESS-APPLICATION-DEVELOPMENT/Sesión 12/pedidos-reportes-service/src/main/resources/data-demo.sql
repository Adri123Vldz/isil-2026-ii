DELETE FROM pedido_revision;
DELETE FROM pedido;

INSERT INTO pedido (
  cliente, producto_codigo, producto_nombre,
  precio_unitario, cantidad, total, fecha, revisado
) VALUES
  ('Ana Torres', 'LAP-001', 'Laptop', 2500.00, 2, 5000.00,
   TIMESTAMP '2026-10-25 09:00:00', FALSE),
  ('Ana Torres', 'MOU-001', 'Mouse', 50.00, 2, 100.00,
   TIMESTAMP '2026-10-26 10:00:00', FALSE),
  ('Carlos Perez', 'MON-001', 'Monitor', 850.00, 1, 850.00,
   TIMESTAMP '2026-10-27 11:00:00', FALSE);
