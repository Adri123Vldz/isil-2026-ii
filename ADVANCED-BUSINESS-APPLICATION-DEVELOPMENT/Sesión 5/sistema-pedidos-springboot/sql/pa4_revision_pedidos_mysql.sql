-- PA4: revisión y trazabilidad de pedidos
-- Ejecutar con una cuenta autorizada sobre la base pedidosdb.

ALTER TABLE pedido
  ADD COLUMN IF NOT EXISTS revisado BOOLEAN NOT NULL DEFAULT FALSE;

UPDATE pedido
SET revisado = FALSE
WHERE revisado IS NULL;

CREATE TABLE IF NOT EXISTS pedido_revision (
  id BIGINT NOT NULL AUTO_INCREMENT,
  pedido_id BIGINT NOT NULL,
  observacion VARCHAR(500) NOT NULL,
  fecha_revision DATETIME NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_pedido_revision_pedido
    FOREIGN KEY (pedido_id) REFERENCES pedido(id),
  CONSTRAINT uk_pedido_revision_pedido
    UNIQUE (pedido_id)
) ENGINE=InnoDB;
