USE bd_citasmedicas;

-- ======================================================
-- MIGRACIÓN V2.1 - ELIMINACIÓN LÓGICA
-- Proyecto: Sistema de Citas Médicas NovaSalud
-- Objetivo:
-- Agregar estado_registro a tablas principales y reemplazar
-- eliminaciones físicas por eliminaciones lógicas.
-- ======================================================

ALTER TABLE paciente
ADD COLUMN estado_registro VARCHAR(10) NOT NULL DEFAULT 'ACTIVO';

ALTER TABLE medico
ADD COLUMN estado_registro VARCHAR(10) NOT NULL DEFAULT 'ACTIVO';

ALTER TABLE especialidad
ADD COLUMN estado_registro VARCHAR(10) NOT NULL DEFAULT 'ACTIVO';

ALTER TABLE horario
ADD COLUMN estado_registro VARCHAR(10) NOT NULL DEFAULT 'ACTIVO';

DROP PROCEDURE IF EXISTS sp_eliminar_paciente;
DROP PROCEDURE IF EXISTS sp_eliminar_medico;
DROP PROCEDURE IF EXISTS sp_eliminar_especialidad;
DROP PROCEDURE IF EXISTS sp_eliminar_horario;

DELIMITER $$

CREATE PROCEDURE sp_eliminar_paciente(
    IN p_id INT
)
BEGIN
    UPDATE paciente
    SET estado_registro = 'INACTIVO'
    WHERE id_paciente = p_id;
END$$

CREATE PROCEDURE sp_eliminar_medico(
    IN p_id INT
)
BEGIN
    UPDATE medico
    SET estado_registro = 'INACTIVO'
    WHERE id_medico = p_id;
END$$

CREATE PROCEDURE sp_eliminar_especialidad(
    IN p_id INT
)
BEGIN
    UPDATE especialidad
    SET estado_registro = 'INACTIVO'
    WHERE id_especialidad = p_id;
END$$

CREATE PROCEDURE sp_eliminar_horario(
    IN p_id INT
)
BEGIN
    UPDATE horario
    SET estado_registro = 'INACTIVO'
    WHERE id_horario = p_id;
END$$

DELIMITER ;