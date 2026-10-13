-- Creacion de la base de datos 
CREATE DATABASE bdventas;

-- Crear el esquema
CREATE SCHEMA catalogo;

-- Crear tabla de la categoria 
CREATE TABLE catalogo.categorias
(
    id_categoria SERIAL PRIMARY KEY,
    nombre VARCHAR(80) NOT NULL, 
    descripcion VARCHAR(200) NOT NULL,
    estado SMALLINT NOT NULL DEFAULT 1,

    CONSTRAINT chk_categoria_estado
        CHECK (estado IN (0, 1))
);

INSERT INTO catalogo.categorias (nombre, descripcion)
VALUES ('Cereales 2', 'Categorias adicionales');

-- Creacion de la tabla Marca
CREATE TABLE catalogo.marcas
(
    id_marca SERIAL PRIMARY KEY,
    nombre VARCHAR(80) NOT NULL UNIQUE,
    pais_origen VARCHAR(80),
    estado SMALLINT NOT NULL DEFAULT 1,

    CONSTRAINT chk_marca_estado
        CHECK (estado IN (0,1))
);

-- Esta consulta da error si se ejecuta dos veces, debido a UNIQUE
INSERT INTO catalogo.marcas (nombre, pais_origen, estado)
VALUES ('Asus', 'Taiwan', 1);

-- Creacion de Tabla Producto
CREATE TABLE catalogo.producto
(
    id_producto SERIAL NOT NULL PRIMARY KEY,
    codigo VARCHAR(20) NOT NULL UNIQUE, 
    nombre VARCHAR(100) NOT NULL, 
    descripcion VARCHAR(200), 
    precio NUMERIC(10, 2) NOT NULL,
    stock INTEGER NOT NULL DEFAULT 0,
    fecha_creacion DATE NOT NULL DEFAULT CURRENT_DATE,
    id_categoria INTEGER NOT NULL, 
    id_marca INTEGER NOT NULL, 
    estado SMALLINT NOT NULL DEFAULT 1,

    CONSTRAINT fk_producto_categoria
        FOREIGN KEY (id_categoria)
        REFERENCES catalogo.categorias(id_categoria),

    CONSTRAINT fk_producto_marca
        FOREIGN KEY (id_marca)
        REFERENCES catalogo.marcas(id_marca),

    CONSTRAINT chk_producto_precio
        CHECK (precio > 0),

    CONSTRAINT chk_producto_stock
        CHECK (stock >= 0),

    CONSTRAINT chk_producto_estado
        CHECK (estado IN (0,1))
);


INSERT INTO catalogo.producto
(codigo, nombre, descripcion, precio, stock, id_categoria, id_marca, estado)
VALUES
('P001', 'Laptop Asus', 'Laptop para uso universitario', 850.00, 10, 3, 1, 1);

-- Crear cinco registros de marcas

INSERT INTO catalogo.marcas (nombre, pais_origen, estado)
VALUES
('Lenovo', 'China', 1),
('HP', 'Estados Unidos', 1),
('Dell', 'Estados Unidos', 1),
('Acer', 'Taiwan', 1),
('MSI', 'Taiwan', 1);

-- Crear cinco registros de categorias

INSERT INTO catalogo.categorias (nombre, descripcion, estado)
VALUES
('Laptops', 'Computadoras portatiles', 1),
('Monitores', 'Pantallas y monitores para computadora', 1),
('Teclados', 'Teclados alambricos e inalambricos', 1),
('Mouse', 'Dispositivos apuntadores para computadora', 1),
('Accesorios', 'Accesorios y complementos de computacion', 1);

-- Crear diez registros de productos

INSERT INTO catalogo.producto
(codigo, nombre, descripcion, precio, stock, id_categoria, id_marca, estado)
VALUES
(
    'P002',
    'Lenovo IdeaPad 3',
    'Laptop Lenovo IdeaPad para uso universitario',
    700.00,
    8,
    (SELECT id_categoria FROM catalogo.categorias WHERE nombre = 'Laptops' LIMIT 1),
    (SELECT id_marca FROM catalogo.marcas WHERE nombre = 'Lenovo' LIMIT 1),
    1
),
(
    'P003',
    'HP Pavilion',
    'Laptop HP Pavilion para oficina',
    850.00,
    6,
    (SELECT id_categoria FROM catalogo.categorias WHERE nombre = 'Laptops' LIMIT 1),
    (SELECT id_marca FROM catalogo.marcas WHERE nombre = 'HP' LIMIT 1),
    1
),
(
    'P004',
    'Dell Inspiron 15',
    'Laptop Dell Inspiron de 15 pulgadas',
    900.00,
    5,
    (SELECT id_categoria FROM catalogo.categorias WHERE nombre = 'Laptops' LIMIT 1),
    (SELECT id_marca FROM catalogo.marcas WHERE nombre = 'Dell' LIMIT 1),
    1
),
(
    'P005',
    'Acer Aspire 5',
    'Laptop Acer Aspire para trabajo y estudio',
    750.00,
    7,
    (SELECT id_categoria FROM catalogo.categorias WHERE nombre = 'Laptops' LIMIT 1),
    (SELECT id_marca FROM catalogo.marcas WHERE nombre = 'Acer' LIMIT 1),
    1
),
(
    'P006',
    'Monitor Asus 24',
    'Monitor Asus de 24 pulgadas Full HD',
    180.00,
    12,
    (SELECT id_categoria FROM catalogo.categorias WHERE nombre = 'Monitores' LIMIT 1),
    (SELECT id_marca FROM catalogo.marcas WHERE nombre = 'Asus' LIMIT 1),
    1
),
(
    'P007',
    'Monitor Dell 27',
    'Monitor Dell de 27 pulgadas',
    250.00,
    9,
    (SELECT id_categoria FROM catalogo.categorias WHERE nombre = 'Monitores' LIMIT 1),
    (SELECT id_marca FROM catalogo.marcas WHERE nombre = 'Dell' LIMIT 1),
    1
),
(
    'P008',
    'Teclado Lenovo',
    'Teclado USB para computadora',
    35.00,
    20,
    (SELECT id_categoria FROM catalogo.categorias WHERE nombre = 'Teclados' LIMIT 1),
    (SELECT id_marca FROM catalogo.marcas WHERE nombre = 'Lenovo' LIMIT 1),
    1
),
(
    'P009',
    'Mouse HP',
    'Mouse optico inalambrico',
    25.00,
    25,
    (SELECT id_categoria FROM catalogo.categorias WHERE nombre = 'Mouse' LIMIT 1),
    (SELECT id_marca FROM catalogo.marcas WHERE nombre = 'HP' LIMIT 1),
    1
),
(
    'P010',
    'Cargador Acer',
    'Cargador para laptop Acer',
    45.00,
    15,
    (SELECT id_categoria FROM catalogo.categorias WHERE nombre = 'Accesorios' LIMIT 1),
    (SELECT id_marca FROM catalogo.marcas WHERE nombre = 'Acer' LIMIT 1),
    1
);

SELECT
    c.conname AS nombre_constraint,
    CASE c.contype
        WHEN 'p' THEN 'PRIMARY KEY'
        WHEN 'f' THEN 'FOREIGN KEY'
        WHEN 'u' THEN 'UNIQUE'
        WHEN 'c' THEN 'CHECK'
        WHEN 'x' THEN 'EXCLUSION'
        WHEN 'n' THEN 'NOT NULL'
        WHEN 't' THEN 'CONSTRAINT TRIGGER'
        ELSE c.contype::text
    END AS tipo_constraint,
    pg_get_constraintdef(c.oid, true) AS definicion
FROM pg_constraint AS c
WHERE c.conrelid = 'catalogo.marcas'::regclass
ORDER BY tipo_constraint, nombre_constraint;

SELECT
    c.conname AS nombre_constraint,
    CASE c.contype
        WHEN 'p' THEN 'PRIMARY KEY'
        WHEN 'f' THEN 'FOREIGN KEY'
        WHEN 'u' THEN 'UNIQUE'
        WHEN 'c' THEN 'CHECK'
        WHEN 'x' THEN 'EXCLUSION'
        WHEN 'n' THEN 'NOT NULL'
        WHEN 't' THEN 'CONSTRAINT TRIGGER'
        ELSE c.contype::text
    END AS tipo_constraint,
    pg_get_constraintdef(c.oid, true) AS definicion
FROM pg_constraint AS c
WHERE c.conrelid = 'catalogo.categorias'::regclass
ORDER BY tipo_constraint, nombre_constraint;

SELECT
    c.conname AS nombre_constraint,
    CASE c.contype
        WHEN 'p' THEN 'PRIMARY KEY'
        WHEN 'f' THEN 'FOREIGN KEY'
        WHEN 'u' THEN 'UNIQUE'
        WHEN 'c' THEN 'CHECK'
        WHEN 'x' THEN 'EXCLUSION'
        WHEN 'n' THEN 'NOT NULL'
        WHEN 't' THEN 'CONSTRAINT TRIGGER'
        ELSE c.contype::text
    END AS tipo_constraint,
    pg_get_constraintdef(c.oid, true) AS definicion
FROM pg_constraint AS c
WHERE c.conrelid = 'catalogo.producto'::regclass
ORDER BY tipo_constraint, nombre_constraint;
