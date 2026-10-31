INSERT INTO pedido (
    cliente,
    producto_codigo,
    producto_nombre,
    precio_unitario,
    cantidad,
    total,
    fecha
) VALUES
    ('Ana Torres', 'LAP-001', 'Laptop', 2500.00, 2, 5000.00, TIMESTAMP '2026-10-25 09:00:00'),
    ('Ana Torres', 'MOU-001', 'Mouse', 50.00, 2, 100.00, TIMESTAMP '2026-10-26 10:00:00'),
    ('Carlos Perez', 'MON-001', 'Monitor', 850.00, 1, 850.00, TIMESTAMP '2026-10-27 11:00:00'),
    ('Ana Torres', 'TEC-001', 'Teclado', 120.00, 4, 480.00, TIMESTAMP '2026-10-28 12:00:00'),
    ('Carlos Perez', 'MOU-001', 'Mouse', 50.00, 3, 150.00, TIMESTAMP '2026-10-29 13:00:00');
