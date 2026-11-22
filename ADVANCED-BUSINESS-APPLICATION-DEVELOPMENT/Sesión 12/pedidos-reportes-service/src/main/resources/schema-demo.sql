CREATE TABLE IF NOT EXISTS pedido (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  cliente VARCHAR(120) NOT NULL,
  producto_codigo VARCHAR(30) NOT NULL,
  producto_nombre VARCHAR(120) NOT NULL,
  precio_unitario DECIMAL(12,2) NOT NULL,
  cantidad INT NOT NULL,
  total DECIMAL(12,2) NOT NULL,
  fecha TIMESTAMP NOT NULL,
  revisado BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS pedido_revision (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  pedido_id BIGINT NOT NULL,
  observacion VARCHAR(500) NOT NULL,
  fecha_revision TIMESTAMP NOT NULL,
  CONSTRAINT fk_demo_pedido_revision
    FOREIGN KEY (pedido_id) REFERENCES pedido(id),
  CONSTRAINT uk_demo_pedido_revision
    UNIQUE (pedido_id)
);
