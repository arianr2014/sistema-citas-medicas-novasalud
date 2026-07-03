USE bd_citasmedicas;

-- ======================================================
-- MIGRACIÓN V2.1 - HASH DE CONTRASEÑAS
-- Proyecto: Sistema de Citas Médicas NovaSalud
-- Objetivo:
-- Reemplazar contraseñas en texto plano por hashes BCrypt.
-- ======================================================

CREATE TABLE IF NOT EXISTS usuario_backup_fase2 AS
SELECT * FROM usuario;

UPDATE usuario
SET password = '$2a$12$lluhfqKEhpzXxiDcd6aR8.nd945oJnjqrU.5UL0y4Dr7xlvN9IgOi'
WHERE username = 'administrador';

UPDATE usuario
SET password = '$2a$12$5incbj/ViGGJej9XxhQ5leaKsyAyi2QyMBQzIDKKIWrBP3Vok7WI6'
WHERE username = 'recepcionista';

UPDATE usuario
SET password = '$2a$12$qWKhfWTpbf3prz1.tmwhPu33RNdfrcCSb7ikyok04RAOGbJr/b3sC'
WHERE username = 'doctor';