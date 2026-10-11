CREATE DATABASE IF NOT EXISTS gestor_tareas
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE gestor_tareas;

CREATE TABLE IF NOT EXISTS usuario (
    id INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(150) NOT NULL,
    clave_hash CHAR(64) NOT NULL,
    sal CHAR(32) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_usuario_correo (correo)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS tarea (
    id INT NOT NULL AUTO_INCREMENT,
    titulo VARCHAR(120) NOT NULL,
    descripcion VARCHAR(500),
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    usuario_id INT NOT NULL,
    PRIMARY KEY (id),
    KEY ix_tarea_usuario (usuario_id),
    CONSTRAINT fk_tarea_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuario (id)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT ck_tarea_estado CHECK (estado IN ('PENDIENTE', 'EN_PROCESO', 'FINALIZADA'))
) ENGINE=InnoDB;

-- Cuenta inicial para la demostración. La clave de prueba se documenta en README.md.
-- La aplicación verifica PBKDF2WithHmacSHA256 con 600000 iteraciones.
INSERT INTO usuario (nombre, correo, clave_hash, sal)
SELECT 'Administrador', 'admin@clase.local',
       '86c24a3dc2bab2f804519df761627624649994723f012fd65f33d8d4ad42ad03',
       'cfd00e0535026d76b6a80cf361e28886'
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE correo = 'admin@clase.local');
