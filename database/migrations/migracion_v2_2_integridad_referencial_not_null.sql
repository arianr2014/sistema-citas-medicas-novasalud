USE bd_citasmedicas;

-- ======================================================
-- MIGRACION V2.2 - INTEGRIDAD REFERENCIAL OBLIGATORIA
-- Proyecto: Sistema de Citas Medicas NovaSalud
-- Objetivo:
-- 1) Evitar registros huerfanos o incompletos en tablas con FK obligatoria.
-- 2) Forzar NOT NULL en llaves foraneas criticas.
--
-- IMPORTANTE:
-- Esta migracion elimina registros invalidos (huerfanos o con FK NULL)
-- luego de respaldarlos en tablas backup_*.
-- ======================================================

-- 1) RESPALDOS PREVIOS DE REGISTROS INVALIDOS
CREATE TABLE IF NOT EXISTS backup_v2_2_medico_fk_null AS
SELECT * FROM medico WHERE id_especialidad IS NULL;

CREATE TABLE IF NOT EXISTS backup_v2_2_cita_fk_null AS
SELECT * FROM cita WHERE id_paciente IS NULL OR id_medico IS NULL;

CREATE TABLE IF NOT EXISTS backup_v2_2_horario_fk_null AS
SELECT * FROM horario WHERE id_medico IS NULL;

CREATE TABLE IF NOT EXISTS backup_v2_2_medico_huerfano AS
SELECT m.*
FROM medico m
LEFT JOIN especialidad e ON e.id_especialidad = m.id_especialidad
WHERE m.id_especialidad IS NOT NULL
  AND e.id_especialidad IS NULL;

CREATE TABLE IF NOT EXISTS backup_v2_2_cita_huerfana AS
SELECT c.*
FROM cita c
LEFT JOIN paciente p ON p.id_paciente = c.id_paciente
LEFT JOIN medico m ON m.id_medico = c.id_medico
WHERE (c.id_paciente IS NOT NULL AND p.id_paciente IS NULL)
   OR (c.id_medico IS NOT NULL AND m.id_medico IS NULL);

CREATE TABLE IF NOT EXISTS backup_v2_2_horario_huerfano AS
SELECT h.*
FROM horario h
LEFT JOIN medico m ON m.id_medico = h.id_medico
WHERE h.id_medico IS NOT NULL
  AND m.id_medico IS NULL;

-- 2) LIMPIEZA DE REGISTROS INVALIDOS
DELETE c
FROM cita c
LEFT JOIN paciente p ON p.id_paciente = c.id_paciente
LEFT JOIN medico m ON m.id_medico = c.id_medico
WHERE c.id_paciente IS NULL
   OR c.id_medico IS NULL
   OR p.id_paciente IS NULL
   OR m.id_medico IS NULL;

DELETE h
FROM horario h
LEFT JOIN medico m ON m.id_medico = h.id_medico
WHERE h.id_medico IS NULL
   OR m.id_medico IS NULL;

DELETE m
FROM medico m
LEFT JOIN especialidad e ON e.id_especialidad = m.id_especialidad
WHERE m.id_especialidad IS NULL
   OR e.id_especialidad IS NULL;

-- 3) ENDURECER COLUMNAS FK COMO NOT NULL
ALTER TABLE medico
MODIFY COLUMN id_especialidad INT(11) NOT NULL;

ALTER TABLE cita
MODIFY COLUMN id_paciente INT(11) NOT NULL,
MODIFY COLUMN id_medico INT(11) NOT NULL;

ALTER TABLE horario
MODIFY COLUMN id_medico INT(11) NOT NULL;
