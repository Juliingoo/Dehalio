DROP DATABASE IF EXISTS dehalioDB;
CREATE DATABASE IF NOT EXISTS dehalioDB;
USE dehalioDB;

-- Tabla usuario
CREATE TABLE usuario (
    idUsuario INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    apellidos VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL,
    contrasenia VARCHAR(50) NOT NULL,
    direccion VARCHAR(100),
    codigoPostal VARCHAR(5),
    municipio VARCHAR(50),
    provincia VARCHAR(50),
    propietario BOOLEAN NOT NULL
);

-- Tabla comercio
CREATE TABLE comercio (
    idComercio INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    NIF VARCHAR(9) NOT NULL UNIQUE,
    imagen LONGBLOB,
    direccion VARCHAR(100) NOT NULL,
    codigoPostal VARCHAR(5) NOT NULL,
    municipio VARCHAR(50) NOT NULL,
    provincia VARCHAR(50) NOT NULL,
    coordenadas VARCHAR(50) NOT NULL,
    propietario INT NOT NULL,
    FOREIGN KEY (propietario) REFERENCES usuario(idUsuario)
);

-- Tabla categoriaTipoProducto
CREATE TABLE categoriaTipoProducto (
    idCategoriaTipoProducto INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL
);

-- Tabla tipoProducto
CREATE TABLE tipoProducto (
    idTipoProducto INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombreBase VARCHAR(50) NOT NULL,
    categoria INT,
    FOREIGN KEY (categoria) REFERENCES categoriaTipoProducto(idCategoriaTipoProducto)
);

-- Tabla producto
CREATE TABLE producto (
    idProducto INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    imagen LONGBLOB,
    precio DOUBLE,
    comercio INT NOT NULL,
    tipo INT,
    FOREIGN KEY (comercio) REFERENCES comercio(idComercio),
    FOREIGN KEY (tipo) REFERENCES tipoProducto(idTipoProducto)
);

-- Tabla valoracion
CREATE TABLE valoracion (
    idValoracion INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    numeroValoracion TINYINT NOT NULL CHECK (numeroValoracion BETWEEN 1 AND 5),
    usuario INT NOT NULL,
    comercio INT NOT NULL,
    FOREIGN KEY (usuario) REFERENCES usuario(idUsuario),
    FOREIGN KEY (comercio) REFERENCES comercio(idComercio)
);

-- Tabla productoFavorito
CREATE TABLE productoFavorito (
    idProductoFavorito INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    usuario INT NOT NULL,
    producto INT NOT NULL,
    FOREIGN KEY (usuario) REFERENCES usuario(idUsuario),
    FOREIGN KEY (producto) REFERENCES producto(idProducto)
);

-- Tabla solicitudComercio
CREATE TABLE solicitudComercio (
    idSolicitudComercio INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    NIF VARCHAR(9) NOT NULL UNIQUE,
    imagen LONGBLOB,
    direccion VARCHAR(100) NOT NULL,
    codigoPostal VARCHAR(5) NOT NULL,
    municipio VARCHAR(50) NOT NULL,
    provincia VARCHAR(50) NOT NULL,
    coordenadas VARCHAR(50) NOT NULL,
    solicitante INT NOT NULL,
    FOREIGN KEY (solicitante) REFERENCES usuario(idUsuario)
);

-- Tabla adminitracion
CREATE TABLE administracion (
	idAdministracion INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    contrasenia VARCHAR(50)
);

-- Ejemplos
-- categoriaTipoProducto
INSERT INTO categoriaTipoProducto (nombre) VALUES
('Fruta'),
('Verdura'),
('Electrodoméstico'),
('Lácteos'),
('Panadería');

-- tipoProducto
INSERT INTO tipoProducto (nombreBase,  categoria) VALUES
('Uva', 1),
('Lechuga', 2),
('Lavadora', 3),
('Queso Manchego', 4),
('Pan Integral', 5),
('Tomate', 2),
('Nevera', 3);

-- usuario
INSERT INTO usuario (nombre, apellidos, email, contrasenia, direccion, codigoPostal, municipio, provincia, propietario) VALUES
('Ana', 'Pérez', 'e', 'dd1d812042362d6fadef5ed6585ab907', 'Calle Luna 12', '28001', 'Madrid', 'Madrid', TRUE),
('Luis', 'Ramírez', 'luis.ramirez@mail.com', 'dd1d812042362d6fadef5ed6585ab907', 'Av. Sol 45', '08001', 'Barcelona', 'Barcelona', FALSE),
('Carlos', 'Sánchez', 'carlos.sanchez@mail.com', 'dd1d812042362d6fadef5ed6585ab907', 'Calle Mayor 33', '03001', 'Alicante', 'Alicante', TRUE),
('María', 'López', 'maria.lopez@mail.com', 'dd1d812042362d6fadef5ed6585ab907', 'Camino del Río 8', '29001', 'Málaga', 'Málaga', TRUE),
('Elena', 'Torres', 'elena.torres@mail.com', 'dd1d812042362d6fadef5ed6585ab907', 'Av. del Parque 21', '50001', 'Zaragoza', 'Zaragoza', FALSE),
('a', 'a', 'a', '0cc175b9c0f1b6a831c399e269772661', 'Calle de A', '14004', 'Córdoba', 'Córdoba', FALSE);

-- comercio
INSERT INTO comercio (nombre, NIF, imagen, direccion, codigoPostal, municipio, provincia, coordenadas, propietario) VALUES
('Frutas Córdoba', 'X1234567A', NULL, 'Calle Frutas', '14004', 'Córdoba', 'Cördoba', '37.883246,-4.788189', 1),
('ElectroBarcelona', 'Y7654321B', NULL, 'Centro Comercial 10', '08001', 'Barcelona', 'Barcelona', '41.3851,2.1734', 1),
('Lácteos La Sierra', 'Z9876543C', NULL, 'Av. Quesos 5', '03001', 'Alicante', 'Alicante', '38.3452,-0.4810', 3),
('Panadería El Trigal', 'W1237890D', NULL, 'Calle Pan 22', '29001', 'Málaga', 'Málaga', '36.7213,-4.4214', 4);

-- producto
INSERT INTO producto (nombre, imagen, precio, comercio, tipo) VALUES
('Uva Moscatel', NULL, 3.25, 1, 1),
('Lechuga Iceberg', NULL, 1.10, 1, 2),
('Lavadora LG 8kg', NULL, 399.99, 2, 3),
('Lavadora Samsung EcoBubble', NULL, 449.50, 2, 3),
('Queso Manchego Curado', NULL, 12.99, 3, 4),
('Pan Integral con Semillas', NULL, 2.30, 4, 5),
('Tomate Raf', NULL, 2.60, 1, 6),
('Nevera Bosch 300L', NULL, 599.00, 2, 7);

-- productoFavorito
INSERT INTO productoFavorito (usuario, producto) VALUES
(2, 1),
(2, 3),
(2, 4),
(5, 5),
(5, 6),
(5, 7);

-- valoracion
INSERT INTO valoracion (numeroValoracion, usuario, comercio) VALUES
(5, 2, 1),
(4, 2, 2),
(5, 5, 3),
(4, 5, 4);

INSERT INTO administracion (contrasenia) VALUES
('nMZyeJFK52lZg745Cu+fGw==');

