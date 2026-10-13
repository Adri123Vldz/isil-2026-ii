USE InventarioDB;
GO

IF OBJECT_ID(N'dbo.compensacion_stock', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.compensacion_stock (
        operacion_id       VARCHAR(36)  NOT NULL,
        pedido_id          BIGINT       NOT NULL,
        codigo             VARCHAR(30)  NOT NULL,
        cantidad           INT          NOT NULL,
        stock_resultante   INT          NOT NULL,
        fecha_compensacion DATETIME2    NOT NULL,
        CONSTRAINT PK_compensacion_stock PRIMARY KEY (operacion_id),
        CONSTRAINT FK_compensacion_producto
            FOREIGN KEY (codigo) REFERENCES dbo.producto_inventario(codigo),
        CONSTRAINT CK_compensacion_cantidad CHECK (cantidad > 0)
    );
END;
GO

GRANT SELECT, INSERT ON dbo.compensacion_stock TO inventario_app;
GO
