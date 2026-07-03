CREATE DATABASE IF NOT EXISTS bd_citasmedicas
DEFAULT CHARACTER SET utf8mb4
COLLATE utf8mb4_general_ci;

USE bd_citasmedicas;

-- OPCIONAL (evita problemas de orden de FK durante creación)
SET FOREIGN_KEY_CHECKS = 0;

-- 1. TABLAS BASE (sin dependencias)
CREATE TABLE especialidad (
  id_especialidad int(11) NOT NULL AUTO_INCREMENT,
  nombre varchar(100) DEFAULT NULL,
  descripcion varchar(255) DEFAULT NULL,
  usuario_registro varchar(50) DEFAULT NULL,
  fecha_registro datetime DEFAULT NULL,
  estado_registro varchar(10) NOT NULL DEFAULT 'ACTIVO',
  PRIMARY KEY (id_especialidad)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE paciente (
  id_paciente int(11) NOT NULL AUTO_INCREMENT,
  dni varchar(15) DEFAULT NULL,
  nombres varchar(100) DEFAULT NULL,
  apellidos varchar(100) DEFAULT NULL,
  telefono varchar(20) DEFAULT NULL,
  correo varchar(100) DEFAULT NULL,
  direccion varchar(255) DEFAULT NULL,
  fecha_nacimiento date DEFAULT NULL,
  usuario_registro varchar(50) DEFAULT NULL,
  fecha_registro datetime DEFAULT NULL,
  estado_registro varchar(10) NOT NULL DEFAULT 'ACTIVO',
  PRIMARY KEY (id_paciente),
  UNIQUE KEY dni (dni)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE usuario (
  id_usuario int(11) NOT NULL AUTO_INCREMENT,
  username varchar(50) DEFAULT NULL,
  password varchar(100) DEFAULT NULL,
  rol varchar(20) DEFAULT NULL,
  usuario_registro varchar(50) DEFAULT NULL,
  fecha_registro datetime DEFAULT NULL,
  PRIMARY KEY (id_usuario),
  UNIQUE KEY username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 2. TABLAS CON DEPENDENCIAS
CREATE TABLE medico (
  id_medico int(11) NOT NULL AUTO_INCREMENT,
  nombres varchar(100) DEFAULT NULL,
  apellidos varchar(100) DEFAULT NULL,
  id_especialidad int(11) DEFAULT NULL,
  telefono varchar(20) DEFAULT NULL,
  correo varchar(100) DEFAULT NULL,
  usuario_registro varchar(50) DEFAULT NULL,
  fecha_registro datetime DEFAULT NULL,
  estado_registro varchar(10) NOT NULL DEFAULT 'ACTIVO',
  PRIMARY KEY (id_medico),
  KEY id_especialidad (id_especialidad),
  CONSTRAINT medico_ibfk_1 
    FOREIGN KEY (id_especialidad) REFERENCES especialidad (id_especialidad)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE cita (
  id_cita int(11) NOT NULL AUTO_INCREMENT,
  id_paciente int(11) DEFAULT NULL,
  id_medico int(11) DEFAULT NULL,
  fecha date DEFAULT NULL,
  hora time DEFAULT NULL,
  estado varchar(20) DEFAULT NULL,
  observaciones varchar(255) DEFAULT NULL,
  usuario_registro varchar(50) DEFAULT NULL,
  fecha_registro datetime DEFAULT NULL,
  estado_registro varchar(10) NOT NULL DEFAULT 'ACTIVO',
  PRIMARY KEY (id_cita),
  KEY id_paciente (id_paciente),
  KEY id_medico (id_medico),
  CONSTRAINT cita_ibfk_1 
    FOREIGN KEY (id_paciente) REFERENCES paciente (id_paciente),
  CONSTRAINT cita_ibfk_2 
    FOREIGN KEY (id_medico) REFERENCES medico (id_medico)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE horario (
  id_horario int(11) NOT NULL AUTO_INCREMENT,
  id_medico int(11) DEFAULT NULL,
  dia varchar(20) DEFAULT NULL,
  hora_inicio time DEFAULT NULL,
  hora_fin time DEFAULT NULL,
  usuario_registro varchar(50) DEFAULT NULL,
  fecha_registro datetime DEFAULT NULL,
  estado_registro varchar(10) NOT NULL DEFAULT 'ACTIVO',
  PRIMARY KEY (id_horario),
  KEY id_medico (id_medico),
  CONSTRAINT horario_ibfk_1 
    FOREIGN KEY (id_medico) REFERENCES medico (id_medico)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- REACTIVAR FK CHECK
SET FOREIGN_KEY_CHECKS = 1;