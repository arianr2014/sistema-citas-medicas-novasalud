DELIMITER $$

CREATE PROCEDURE sp_actualizar_cita(
    IN p_id_cita INT,
    IN p_id_paciente INT,
    IN p_id_medico INT,
    IN p_fecha DATE,
    IN p_hora TIME,
    IN p_estado VARCHAR(20),
    IN p_observaciones VARCHAR(255)
)
BEGIN
    UPDATE cita
    SET 
        id_paciente = p_id_paciente,
        id_medico = p_id_medico,
        fecha = p_fecha,
        hora = p_hora,
        estado = p_estado,
        observaciones = p_observaciones
    WHERE id_cita = p_id_cita
      AND estado_registro = 'ACTIVO';
END$$

CREATE PROCEDURE sp_actualizar_medico(
    IN p_id INT,
    IN p_nombres VARCHAR(100),
    IN p_apellidos VARCHAR(100),
    IN p_especialidad INT,
    IN p_telefono VARCHAR(20)
)
BEGIN
    UPDATE medico
    SET nombres = p_nombres,
        apellidos = p_apellidos,
        id_especialidad = p_especialidad,
        telefono = p_telefono
    WHERE id_medico = p_id
      AND estado_registro = 'ACTIVO';
END$$

CREATE PROCEDURE sp_actualizar_paciente(
    IN p_id INT,
    IN p_dni VARCHAR(15),
    IN p_nombres VARCHAR(100),
    IN p_apellidos VARCHAR(100),
    IN p_telefono VARCHAR(20),
    IN p_direccion VARCHAR(255)
)
BEGIN
    UPDATE paciente
    SET dni = p_dni,
        nombres = p_nombres,
        apellidos = p_apellidos,
        telefono = p_telefono,
        direccion = p_direccion
    WHERE id_paciente = p_id
      AND estado_registro = 'ACTIVO';
END$$

CREATE PROCEDURE sp_eliminar_cita(
    IN p_id_cita INT
)
BEGIN
    UPDATE cita
    SET estado_registro = 'INACTIVO'
    WHERE id_cita = p_id_cita;
END$$

CREATE PROCEDURE sp_eliminar_medico(
    IN p_id INT
)
BEGIN
    UPDATE medico
    SET estado_registro = 'INACTIVO'
    WHERE id_medico = p_id;
END$$

CREATE PROCEDURE sp_eliminar_paciente(
    IN p_id INT
)
BEGIN
    UPDATE paciente
    SET estado_registro = 'INACTIVO'
    WHERE id_paciente = p_id;
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

CREATE PROCEDURE sp_listar_citas()
BEGIN
    SELECT 
        c.id_cita,
        p.nombres AS paciente_nombres,
        p.apellidos AS paciente_apellidos,
        m.nombres AS medico_nombres,
        m.apellidos AS medico_apellidos,
        e.nombre AS especialidad,
        c.fecha,
        c.hora,
        c.estado,
        c.observaciones
    FROM cita c
    INNER JOIN paciente p ON c.id_paciente = p.id_paciente
    INNER JOIN medico m ON c.id_medico = m.id_medico
    INNER JOIN especialidad e ON m.id_especialidad = e.id_especialidad
    WHERE c.estado_registro = 'ACTIVO'
      AND p.estado_registro = 'ACTIVO'
      AND m.estado_registro = 'ACTIVO'
      AND e.estado_registro = 'ACTIVO';
END$$

CREATE PROCEDURE sp_listar_medicos()
BEGIN
    SELECT 
        m.id_medico,
        CONCAT(m.nombres, ' ', m.apellidos) AS medico,
        e.nombre AS especialidad,
        m.telefono
    FROM medico m
    INNER JOIN especialidad e ON m.id_especialidad = e.id_especialidad
    WHERE m.estado_registro = 'ACTIVO'
      AND e.estado_registro = 'ACTIVO';
END$$

CREATE PROCEDURE sp_registrar_cita(
    IN p_id_paciente INT,
    IN p_id_medico INT,
    IN p_fecha DATE,
    IN p_hora TIME,
    IN p_estado VARCHAR(20),
    IN p_observaciones VARCHAR(255),
    IN p_usuario VARCHAR(50)
)
BEGIN
    INSERT INTO cita (
        id_paciente,
        id_medico,
        fecha,
        hora,
        estado,
        observaciones,
        usuario_registro,
        fecha_registro,
        estado_registro
    )
    VALUES (
        p_id_paciente,
        p_id_medico,
        p_fecha,
        p_hora,
        p_estado,
        p_observaciones,
        p_usuario,
        NOW(),
        'ACTIVO'
    );
END$$

CREATE PROCEDURE sp_registrar_medico(
    IN p_nombres VARCHAR(100),
    IN p_apellidos VARCHAR(100),
    IN p_especialidad INT,
    IN p_telefono VARCHAR(20),
    IN p_usuario VARCHAR(50)
)
BEGIN
    INSERT INTO medico (
        nombres,
        apellidos,
        id_especialidad,
        telefono,
        usuario_registro,
        fecha_registro,
        estado_registro
    )
    VALUES (
        p_nombres,
        p_apellidos,
        p_especialidad,
        p_telefono,
        p_usuario,
        NOW(),
        'ACTIVO'
    );
END$$

CREATE PROCEDURE sp_registrar_paciente(
    IN p_dni VARCHAR(15),
    IN p_nombres VARCHAR(100),
    IN p_apellidos VARCHAR(100),
    IN p_telefono VARCHAR(20),
    IN p_direccion VARCHAR(255),
    IN p_usuario VARCHAR(50)
)
BEGIN
    INSERT INTO paciente (
        dni,
        nombres,
        apellidos,
        telefono,
        direccion,
        usuario_registro,
        fecha_registro,
        estado_registro
    )
    VALUES (
        p_dni,
        p_nombres,
        p_apellidos,
        p_telefono,
        p_direccion,
        p_usuario,
        NOW(),
        'ACTIVO'
    );
END$$

DELIMITER ;