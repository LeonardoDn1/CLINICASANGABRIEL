-- =====================================================================
-- VERSIÓN CORREGIDA Y ENDURECIDA
-- Conserva los nombres y firmas de los procedimientos usados por Java.
-- Corrige integridad, citas, cierre clínico, inventario, auditoría,
-- validaciones de laboratorio/pagos y portabilidad.
-- =====================================================================

-- =====================================================================
-- BASE DE DATOS: clinica_san_gabriel
-- Proyecto Integrador - Sistema Integral de Gestión de Atención Médica
-- Motor: MySQL 8.x
-- Basado en las Reglas de Negocio RN-01 a RN-52 del enunciado
-- =====================================================================

CREATE DATABASE clinica_san_gabriel CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE clinica_san_gabriel;

-- =====================================================================
-- MÓDULO 1: SEGURIDAD Y USUARIOS  (RN-01 a RN-06)
-- =====================================================================

-- RN-04: roles institucionales permitidos (tabla en vez de ENUM para
-- permitir escalabilidad: 4.5 pide admitir nuevos perfiles a futuro)
CREATE TABLE roles (
    id_rol      INT AUTO_INCREMENT PRIMARY KEY,
    nombre_rol  VARCHAR(30) NOT NULL UNIQUE,
    descripcion VARCHAR(150)
);

INSERT INTO roles (nombre_rol, descripcion) VALUES
    ('ADMINISTRADOR', 'Configura el sistema y administra usuarios'),
    ('RECEPCIONISTA', 'Registra pacientes y agenda citas'),
    ('MEDICO', 'Atiende consultas y registra diagnósticos'),
    ('ENFERMERA', 'Apoya en la atención médica y registra signos vitales'),
    ('LABORATORISTA', 'Registra resultados de laboratorio'),
    ('FARMACEUTICO', 'Gestiona la entrega de medicamentos'),
    ('CAJERO', 'Registra pagos y emite comprobantes'),
    ('DIRECTOR_MEDICO', 'Consulta indicadores y reportes');

-- RN-01 (usuario único), RN-02 (contraseña segura -> validada en capa
-- de negocio, aquí se guarda el hash), RN-03 (autenticación obligatoria),
-- RN-04 (rol único por usuario)
CREATE TABLE usuarios (
    id_usuario      INT AUTO_INCREMENT PRIMARY KEY,
    username        VARCHAR(50) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    id_rol          INT NOT NULL,
    nombres         VARCHAR(100) NOT NULL,
    apellidos       VARCHAR(100) NOT NULL,
    email           VARCHAR(150),
    estado          ENUM('ACTIVO','BLOQUEADO') NOT NULL DEFAULT 'ACTIVO',
    fecha_creacion  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ultimo_acceso   DATETIME NULL,
    CONSTRAINT fk_usuario_rol FOREIGN KEY (id_rol) REFERENCES roles(id_rol)
);

-- RN-06: auditoría automática de creación/modificación/eliminación
CREATE TABLE auditoria (
    id_auditoria    BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_usuario      INT NOT NULL,
    fecha_hora      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modulo          VARCHAR(50) NOT NULL,
    operacion       VARCHAR(50) NOT NULL,
    detalle         VARCHAR(500),
    CONSTRAINT fk_auditoria_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);

-- =====================================================================
-- MÓDULO 2: PACIENTES  (RN-07 a RN-11)
-- =====================================================================

-- RN-07 (DNI único), RN-09 (datos obligatorios), RN-10 (historia clínica
-- única), RN-11 (eliminación lógica: solo ACTIVO/INACTIVO)
CREATE TABLE pacientes (
    id_paciente             INT AUTO_INCREMENT PRIMARY KEY,
    numero_historia_clinica VARCHAR(20) NOT NULL UNIQUE,
    dni                     VARCHAR(15) NOT NULL UNIQUE,
    nombres                 VARCHAR(100) NOT NULL,
    apellidos               VARCHAR(100) NOT NULL,
    fecha_nacimiento        DATE NOT NULL,
    sexo                    ENUM('M','F') NOT NULL,
    telefono                VARCHAR(20) NOT NULL,
    direccion               VARCHAR(200) NOT NULL,
    estado                  ENUM('ACTIVO','INACTIVO') NOT NULL DEFAULT 'ACTIVO',
    fecha_registro          DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- RN-08: si el paciente es menor de edad, debe registrarse un apoderado
CREATE TABLE apoderados (
    id_apoderado    INT AUTO_INCREMENT PRIMARY KEY,
    id_paciente     INT NOT NULL,
    dni             VARCHAR(15) NOT NULL,
    nombres         VARCHAR(100) NOT NULL,
    apellidos       VARCHAR(100) NOT NULL,
    telefono        VARCHAR(20) NOT NULL,
    parentesco      VARCHAR(50) NOT NULL,
    CONSTRAINT uq_apoderado_paciente UNIQUE (id_paciente),
    CONSTRAINT fk_apoderado_paciente FOREIGN KEY (id_paciente) REFERENCES pacientes(id_paciente)
);

-- Información de seguro médico (HU-09), un paciente puede tener varios
-- seguros a lo largo del tiempo
CREATE TABLE seguros_medicos (
    id_seguro       INT AUTO_INCREMENT PRIMARY KEY,
    id_paciente     INT NOT NULL,
    aseguradora     VARCHAR(100) NOT NULL,
    numero_poliza   VARCHAR(50) NOT NULL,
    vigencia_inicio DATE,
    vigencia_fin    DATE,
    CONSTRAINT chk_seguro_vigencia CHECK (
        vigencia_fin IS NULL OR vigencia_inicio IS NULL OR vigencia_fin >= vigencia_inicio
    ),
    CONSTRAINT fk_seguro_paciente FOREIGN KEY (id_paciente) REFERENCES pacientes(id_paciente)
);

-- =====================================================================
-- MÓDULO 3: MÉDICOS  (RN-12 a RN-15)
-- =====================================================================

CREATE TABLE especialidades (
    id_especialidad INT AUTO_INCREMENT PRIMARY KEY,
    nombre          VARCHAR(100) NOT NULL UNIQUE
);

-- RN-13: colegiatura única
CREATE TABLE medicos (
    id_medico           INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario          INT NOT NULL UNIQUE,
    numero_colegiatura  VARCHAR(20) NOT NULL UNIQUE,
    CONSTRAINT fk_medico_usuario FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);

-- RN-12: todo médico debe pertenecer al menos a una especialidad
-- (N:M porque un médico puede tener más de una)
CREATE TABLE medico_especialidad (
    id_medico       INT NOT NULL,
    id_especialidad INT NOT NULL,
    PRIMARY KEY (id_medico, id_especialidad),
    CONSTRAINT fk_me_medico FOREIGN KEY (id_medico) REFERENCES medicos(id_medico),
    CONSTRAINT fk_me_especialidad FOREIGN KEY (id_especialidad) REFERENCES especialidades(id_especialidad)
);

-- RN-14: los médicos solo reciben citas dentro de su horario registrado
CREATE TABLE horarios_medico (
    id_horario      INT AUTO_INCREMENT PRIMARY KEY,
    id_medico       INT NOT NULL,
    dia_semana      ENUM('LUNES','MARTES','MIERCOLES','JUEVES','VIERNES','SABADO','DOMINGO') NOT NULL,
    hora_inicio     TIME NOT NULL,
    hora_fin        TIME NOT NULL,
    CONSTRAINT chk_horario_medico_horas CHECK (hora_fin > hora_inicio),
    CONSTRAINT fk_horario_medico FOREIGN KEY (id_medico) REFERENCES medicos(id_medico)
);

-- =====================================================================
-- MÓDULO 4: CITAS  (RN-16 a RN-22)
-- =====================================================================

-- RN-16 (paciente registrado), RN-17 (disponibilidad médica verificada
-- en capa de negocio), RN-18 (duración estándar 30 min -> se calcula
-- hora_fin en la aplicación), RN-19 (horario oficial), RN-20 (estados),
-- RN-21 (cancelación hasta 2h antes -> validado en negocio),
-- RN-22 (reprogramación solo si no atendida)
CREATE TABLE citas (
    id_cita         INT AUTO_INCREMENT PRIMARY KEY,
    id_paciente     INT NOT NULL,
    id_medico       INT NOT NULL,
    fecha           DATE NOT NULL,
    hora_inicio     TIME NOT NULL,
    hora_fin        TIME NOT NULL,
    estado          ENUM('PROGRAMADA','CONFIRMADA','ATENDIDA','CANCELADA','NO_ASISTIO')
                    NOT NULL DEFAULT 'PROGRAMADA',
    fecha_creacion  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_cita_horas CHECK (hora_fin > hora_inicio),
    CONSTRAINT fk_cita_paciente FOREIGN KEY (id_paciente) REFERENCES pacientes(id_paciente),
    CONSTRAINT fk_cita_medico FOREIGN KEY (id_medico) REFERENCES medicos(id_medico)
);

-- =====================================================================
-- MÓDULO 5: ATENCIÓN MÉDICA  (RN-23 a RN-26)
-- =====================================================================

-- RN-23 (solo si la cita está confirmada -> validado en negocio antes
-- del insert), RN-26 (no editable una vez cerrada -> campo "cerrada")
CREATE TABLE atenciones_medicas (
    id_atencion         INT AUTO_INCREMENT PRIMARY KEY,
    id_cita             INT NOT NULL UNIQUE,
    motivo_consulta     VARCHAR(300) NOT NULL,
    antecedentes        VARCHAR(500),
    tratamiento         VARCHAR(500),
    observaciones       VARCHAR(500),
    cerrada             BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_atencion      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_atencion_cita FOREIGN KEY (id_cita) REFERENCES citas(id_cita)
);

-- RN-25: signos vitales forman parte del registro clínico de la atención
CREATE TABLE signos_vitales (
    id_signos           INT AUTO_INCREMENT PRIMARY KEY,
    id_atencion         INT NOT NULL UNIQUE,
    presion_arterial    VARCHAR(15),
    temperatura         DECIMAL(4,1),
    frecuencia_cardiaca INT,
    frecuencia_respiratoria INT,
    peso_kg             DECIMAL(5,2),
    talla_cm             DECIMAL(5,2),
    saturacion_oxigeno   INT,
    CONSTRAINT chk_signos_temperatura CHECK (temperatura IS NULL OR temperatura BETWEEN 25.0 AND 45.0),
    CONSTRAINT chk_signos_frecuencia_cardiaca CHECK (
        frecuencia_cardiaca IS NULL OR frecuencia_cardiaca BETWEEN 20 AND 250
    ),
    CONSTRAINT chk_signos_frecuencia_respiratoria CHECK (
        frecuencia_respiratoria IS NULL OR frecuencia_respiratoria BETWEEN 5 AND 80
    ),
    CONSTRAINT chk_signos_peso CHECK (peso_kg IS NULL OR peso_kg > 0),
    CONSTRAINT chk_signos_talla CHECK (talla_cm IS NULL OR talla_cm > 0),
    CONSTRAINT chk_signos_saturacion CHECK (
        saturacion_oxigeno IS NULL OR saturacion_oxigeno BETWEEN 0 AND 100
    ),
    CONSTRAINT fk_signos_atencion FOREIGN KEY (id_atencion) REFERENCES atenciones_medicas(id_atencion)
);

-- RN-24: toda atención debe registrar al menos un diagnóstico
-- (tabla separada porque puede haber más de uno)
CREATE TABLE diagnosticos (
    id_diagnostico  INT AUTO_INCREMENT PRIMARY KEY,
    id_atencion     INT NOT NULL,
    codigo_cie10    VARCHAR(10),
    descripcion     VARCHAR(300) NOT NULL,
    CONSTRAINT fk_diagnostico_atencion FOREIGN KEY (id_atencion) REFERENCES atenciones_medicas(id_atencion)
);

-- =====================================================================
-- MÓDULO 6: FARMACIA E INVENTARIO  (RN-28, RN-29, RN-33 a RN-35)
-- =====================================================================

-- RN-35: alerta de stock mínimo
CREATE TABLE medicamentos (
    id_medicamento  INT AUTO_INCREMENT PRIMARY KEY,
    nombre          VARCHAR(150) NOT NULL,
    descripcion     VARCHAR(300),
    unidad_medida   VARCHAR(30) NOT NULL,
    stock_actual    INT NOT NULL DEFAULT 0,
    stock_minimo    INT NOT NULL DEFAULT 10,
    precio_unitario DECIMAL(10,2) NOT NULL,
    CONSTRAINT chk_medicamento_stock_actual CHECK (stock_actual >= 0),
    CONSTRAINT chk_medicamento_stock_minimo CHECK (stock_minimo >= 0),
    CONSTRAINT chk_medicamento_precio CHECK (precio_unitario > 0)
);

-- RN-27 (receta asociada a una atención), RN-19 no aplica aquí
CREATE TABLE recetas (
    id_receta       INT AUTO_INCREMENT PRIMARY KEY,
    id_atencion     INT NOT NULL,
    fecha_emision   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_receta_atencion UNIQUE (id_atencion),
    CONSTRAINT fk_receta_atencion FOREIGN KEY (id_atencion) REFERENCES atenciones_medicas(id_atencion)
);

-- RN-28 (medicamento debe existir en inventario), RN-29 (cantidad > 0)
CREATE TABLE receta_detalle (
    id_receta_detalle  INT AUTO_INCREMENT PRIMARY KEY,
    id_receta          INT NOT NULL,
    id_medicamento     INT NOT NULL,
    cantidad           INT NOT NULL,
    indicaciones       VARCHAR(300),
    CONSTRAINT fk_rd_receta FOREIGN KEY (id_receta) REFERENCES recetas(id_receta),
    CONSTRAINT fk_rd_medicamento FOREIGN KEY (id_medicamento) REFERENCES medicamentos(id_medicamento),
    CONSTRAINT chk_cantidad_valida CHECK (cantidad > 0)
);

-- RN-33 (stock suficiente), RN-34 (actualización automática del
-- inventario -> se hace en la capa de negocio/transacción)
CREATE TABLE entregas_medicamentos (
    id_entrega          INT AUTO_INCREMENT PRIMARY KEY,
    id_receta_detalle   INT NOT NULL,
    id_farmaceutico     INT NOT NULL,
    cantidad_entregada  INT NOT NULL,
    fecha_entrega       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_entrega_detalle UNIQUE (id_receta_detalle),
    CONSTRAINT chk_entrega_cantidad CHECK (cantidad_entregada > 0),
    CONSTRAINT fk_entrega_detalle FOREIGN KEY (id_receta_detalle) REFERENCES receta_detalle(id_receta_detalle),
    CONSTRAINT fk_entrega_farmaceutico FOREIGN KEY (id_farmaceutico) REFERENCES usuarios(id_usuario)
);

-- =====================================================================
-- MÓDULO 7: LABORATORIO  (RN-30 a RN-32)
-- =====================================================================

-- RN-30 (no se registran resultados sin orden médica previa),
-- RN-32 (estados permitidos)
CREATE TABLE examenes_laboratorio (
    id_examen       INT AUTO_INCREMENT PRIMARY KEY,
    id_atencion     INT NOT NULL,
    tipo_examen     VARCHAR(150) NOT NULL,
    estado          ENUM('PENDIENTE','EN_PROCESO','FINALIZADO','ENTREGADO')
                    NOT NULL DEFAULT 'PENDIENTE',
    fecha_solicitud DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_examen_atencion FOREIGN KEY (id_atencion) REFERENCES atenciones_medicas(id_atencion)
);

-- RN-31: toda solicitud debe finalizar con un resultado
CREATE TABLE resultados_laboratorio (
    id_resultado        INT AUTO_INCREMENT PRIMARY KEY,
    id_examen           INT NOT NULL UNIQUE,
    id_laboratorista    INT NOT NULL,
    resultado_texto     VARCHAR(1000),
    ruta_archivo        VARCHAR(255),
    fecha_resultado     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_resultado_contenido CHECK (
        (resultado_texto IS NOT NULL AND CHAR_LENGTH(TRIM(resultado_texto)) > 0)
        OR
        (ruta_archivo IS NOT NULL AND CHAR_LENGTH(TRIM(ruta_archivo)) > 0)
    ),
    CONSTRAINT fk_resultado_examen FOREIGN KEY (id_examen) REFERENCES examenes_laboratorio(id_examen),
    CONSTRAINT fk_resultado_laboratorista FOREIGN KEY (id_laboratorista) REFERENCES usuarios(id_usuario)
);

-- =====================================================================
-- MÓDULO 8: CAJA / PAGOS  (RN-36 a RN-38)
-- =====================================================================

-- RN-36 (pago asociado a una atención), RN-38 (métodos de pago)
CREATE TABLE pagos (
    id_pago         INT AUTO_INCREMENT PRIMARY KEY,
    id_atencion     INT NOT NULL,
    id_cajero       INT NOT NULL,
    monto           DECIMAL(10,2) NOT NULL,
    metodo_pago     ENUM('EFECTIVO','TARJETA','TRANSFERENCIA','BILLETERA_ELECTRONICA') NOT NULL,
    fecha_pago      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_pago_atencion UNIQUE (id_atencion),
    CONSTRAINT chk_pago_monto CHECK (monto > 0),
    CONSTRAINT fk_pago_atencion FOREIGN KEY (id_atencion) REFERENCES atenciones_medicas(id_atencion),
    CONSTRAINT fk_pago_cajero FOREIGN KEY (id_cajero) REFERENCES usuarios(id_usuario)
);

-- RN-37: comprobante único por pago
CREATE TABLE comprobantes (
    id_comprobante      INT AUTO_INCREMENT PRIMARY KEY,
    id_pago             INT NOT NULL UNIQUE,
    numero_comprobante  VARCHAR(30) NOT NULL UNIQUE,
    tipo_comprobante    ENUM('BOLETA','FACTURA') NOT NULL DEFAULT 'BOLETA',
    fecha_emision       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_comprobante_pago FOREIGN KEY (id_pago) REFERENCES pagos(id_pago)
);

-- =====================================================================
-- ÍNDICES DE APOYO (para cumplir el requisito de rendimiento: consultas
-- de pacientes < 3 segundos, búsqueda por DNI/nombre/historia clínica)
-- =====================================================================

CREATE INDEX idx_paciente_nombres ON pacientes(apellidos, nombres);
CREATE INDEX idx_cita_fecha_medico ON citas(id_medico, fecha);
CREATE INDEX idx_cita_fecha_paciente ON citas(id_paciente, fecha);
CREATE INDEX idx_examen_estado ON examenes_laboratorio(estado);
CREATE INDEX idx_medicamento_stock ON medicamentos(stock_actual);
CREATE INDEX idx_auditoria_usuario_fecha ON auditoria(id_usuario, fecha_hora);

-- =====================================================================
-- NOTA SOBRE RN-44 (Eliminación restringida):
-- Todas las FK anteriores usan el comportamiento por defecto de MySQL
-- (ON DELETE RESTRICT), por lo que MySQL impedirá automáticamente
-- eliminar un médico con citas, un paciente con historial, o una
-- atención con pagos asociados, sin necesidad de configurar CASCADE.
-- =====================================================================

-- =====================================================================
-- PROCEDIMIENTOS ALMACENADOS - Sistema Clínica San Gabriel
-- Generado a partir de la auditoría completa de las 24 clases DAL.
-- Motor: MySQL 8.x
--
-- Incluye:
--   - 2 procedimientos ya existentes que SÍ calzan con Java (se mantienen)
--   - 6 procedimientos ya existentes que NO calzaban con Java (corregidos)
--   - 56 procedimientos nuevos, uno por cada {call ...} usado en el DAL
-- =====================================================================

USE clinica_san_gabriel;

-- =====================================================================
-- MÓDULO: USUARIOS
-- =====================================================================

DROP PROCEDURE IF EXISTS `SP_AutenticarUsuario`;
DELIMITER $$
CREATE PROCEDURE `SP_AutenticarUsuario`(IN p_username VARCHAR(50))
BEGIN
    -- Correcto tal como estaba: Java hace la verificación de contraseña
    -- (BCrypt.checkpw) del lado de la aplicación, no en SQL.
    SELECT * FROM usuarios WHERE username = p_username;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `sp_insertar_usuario`;
DELIMITER $$
CREATE PROCEDURE `sp_insertar_usuario`(
    IN p_username VARCHAR(50), IN p_password_hash VARCHAR(255), IN p_id_rol INT,
    IN p_nombres VARCHAR(100), IN p_apellidos VARCHAR(100), IN p_email VARCHAR(150), IN p_estado VARCHAR(20)
)
BEGIN
    -- RN-01: username único -> lo protege la restricción UNIQUE de la tabla,
    -- MySQL lanzará SQLException (capturada en DAL_Usuario.insertar) si se repite.
    INSERT INTO usuarios (username, password_hash, id_rol, nombres, apellidos, email, estado)
    VALUES (p_username, p_password_hash, p_id_rol, p_nombres, p_apellidos, p_email, p_estado);
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `sp_actualizar_usuario`;
DELIMITER $$
CREATE PROCEDURE `sp_actualizar_usuario`(
    IN p_id_rol INT, IN p_nombres VARCHAR(100), IN p_apellidos VARCHAR(100),
    IN p_email VARCHAR(150), IN p_id_usuario INT
)
BEGIN
    UPDATE usuarios
    SET id_rol = p_id_rol, nombres = p_nombres, apellidos = p_apellidos, email = p_email
    WHERE id_usuario = p_id_usuario;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `sp_bloquear_usuario`;
DELIMITER $$
CREATE PROCEDURE `sp_bloquear_usuario`(IN p_id_usuario INT)
BEGIN
    UPDATE usuarios SET estado = 'BLOQUEADO' WHERE id_usuario = p_id_usuario;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `sp_activar_usuario`;
DELIMITER $$
CREATE PROCEDURE `sp_activar_usuario`(IN p_id_usuario INT)
BEGIN
    UPDATE usuarios SET estado = 'ACTIVO' WHERE id_usuario = p_id_usuario;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `sp_buscar_usuario_por_id`;
DELIMITER $$
CREATE PROCEDURE `sp_buscar_usuario_por_id`(IN p_id_usuario INT)
BEGIN
    SELECT * FROM usuarios WHERE id_usuario = p_id_usuario;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `sp_listar_usuarios`;
DELIMITER $$
CREATE PROCEDURE `sp_listar_usuarios`()
BEGIN
    SELECT * FROM usuarios ORDER BY username;
END$$
DELIMITER ;

-- =====================================================================
-- MÓDULO: PACIENTES
-- =====================================================================

-- CORREGIDO: la versión original pedía 13 parámetros (paciente + apoderado);
-- Java solo manda 9 (el apoderado se inserta aparte vía DAL_Apoderado).
DROP PROCEDURE IF EXISTS `SP_RegistrarPacienteConApoderado`;
DELIMITER $$
CREATE PROCEDURE `SP_RegistrarPacienteConApoderado`(
    IN p_hc VARCHAR(20), IN p_dni VARCHAR(15), IN p_nombres VARCHAR(100), IN p_apellidos VARCHAR(100),
    IN p_fecha_nac DATE, IN p_sexo VARCHAR(1), IN p_telefono VARCHAR(20), IN p_direccion VARCHAR(200),
    IN p_estado VARCHAR(20)
)
BEGIN
    INSERT INTO pacientes (numero_historia_clinica, dni, nombres, apellidos, fecha_nacimiento, sexo, telefono, direccion, estado)
    VALUES (p_hc, p_dni, p_nombres, p_apellidos, p_fecha_nac, p_sexo, p_telefono, p_direccion, p_estado);

    SELECT LAST_INSERT_ID() AS id;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `sp_actualizar_paciente`;
DELIMITER $$
CREATE PROCEDURE `sp_actualizar_paciente`(
    IN p_nombres VARCHAR(100), IN p_apellidos VARCHAR(100), IN p_fecha_nac DATE, IN p_sexo VARCHAR(1),
    IN p_telefono VARCHAR(20), IN p_direccion VARCHAR(200), IN p_estado VARCHAR(20), IN p_id_paciente INT
)
BEGIN
    UPDATE pacientes
    SET nombres = p_nombres, apellidos = p_apellidos, fecha_nacimiento = p_fecha_nac,
        sexo = p_sexo, telefono = p_telefono, direccion = p_direccion, estado = p_estado
    WHERE id_paciente = p_id_paciente;
END$$
DELIMITER ;

-- RN-11: eliminación lógica, nunca DELETE físico
DROP PROCEDURE IF EXISTS `sp_eliminar_paciente`;
DELIMITER $$
CREATE PROCEDURE `sp_eliminar_paciente`(IN p_id_paciente INT)
BEGIN
    UPDATE pacientes SET estado = 'INACTIVO' WHERE id_paciente = p_id_paciente;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `sp_buscar_paciente_por_dni`;
DELIMITER $$
CREATE PROCEDURE `sp_buscar_paciente_por_dni`(IN p_dni VARCHAR(15))
BEGIN
    SELECT * FROM pacientes WHERE dni = p_dni;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `sp_buscar_paciente_por_id`;
DELIMITER $$
CREATE PROCEDURE `sp_buscar_paciente_por_id`(IN p_id_paciente INT)
BEGIN
    SELECT * FROM pacientes WHERE id_paciente = p_id_paciente;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `sp_buscar_paciente_por_historia_clinica`;
DELIMITER $$
CREATE PROCEDURE `sp_buscar_paciente_por_historia_clinica`(IN p_hc VARCHAR(20))
BEGIN
    SELECT * FROM pacientes WHERE numero_historia_clinica = p_hc;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `sp_buscar_paciente_por_nombre_apellido`;
DELIMITER $$
CREATE PROCEDURE `sp_buscar_paciente_por_nombre_apellido`(
    IN p_filtro_nombre VARCHAR(150), IN p_filtro_apellido VARCHAR(150)
)
BEGIN
    -- DAL_Paciente.buscarPorNombreOApellido arma el mismo "%criterio%" dos veces,
    -- así que ambos parámetros llegan con el mismo valor.
    SELECT * FROM pacientes
    WHERE nombres LIKE p_filtro_nombre OR apellidos LIKE p_filtro_apellido;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `sp_listar_pacientes`;
DELIMITER $$
CREATE PROCEDURE `sp_listar_pacientes`()
BEGIN
    SELECT * FROM pacientes ORDER BY apellidos, nombres;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `sp_generar_nueva_historia_clinica`;
DELIMITER $$
CREATE PROCEDURE `sp_generar_nueva_historia_clinica`()
BEGIN
    -- Java lee rs.getInt("ultimo"); toma el mayor correlativo usado hasta ahora
    SELECT COALESCE(MAX(CAST(SUBSTRING(numero_historia_clinica, 4) AS UNSIGNED)), 0) AS ultimo
    FROM pacientes
    WHERE numero_historia_clinica LIKE 'HC-%';
END$$
DELIMITER ;

-- =====================================================================
-- MÓDULO: MÉDICOS
-- =====================================================================

DROP PROCEDURE IF EXISTS `SP_RegistrarMedicoConEspecialidades`;
DELIMITER $$
CREATE PROCEDURE `SP_RegistrarMedicoConEspecialidades`(
    IN p_id_usuario INT, IN p_numero_colegiatura VARCHAR(20)
)
BEGIN
    -- RN-13: colegiatura única (además de la restricción UNIQUE de la tabla)
    IF EXISTS (SELECT 1 FROM medicos WHERE numero_colegiatura = p_numero_colegiatura) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'El número de colegiatura ya está registrado';
    END IF;

    INSERT INTO medicos (id_usuario, numero_colegiatura) VALUES (p_id_usuario, p_numero_colegiatura);
    SELECT LAST_INSERT_ID() AS id;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `sp_actualizar_medico`;
DELIMITER $$
CREATE PROCEDURE `sp_actualizar_medico`(
    IN p_numero_colegiatura VARCHAR(20), IN p_id_medico INT
)
BEGIN
    UPDATE medicos SET numero_colegiatura = p_numero_colegiatura WHERE id_medico = p_id_medico;
END$$
DELIMITER ;

-- RN-44: si el médico tiene citas, la FK impedirá el DELETE y MySQL lanzará
-- el error de integridad referencial, que Java captura como SQLException.
DROP PROCEDURE IF EXISTS `sp_eliminar_medico`;
DELIMITER $$
CREATE PROCEDURE `sp_eliminar_medico`(IN p_id_medico INT)
BEGIN
    DELETE FROM medico_especialidad WHERE id_medico = p_id_medico;
    DELETE FROM horarios_medico WHERE id_medico = p_id_medico;
    DELETE FROM medicos WHERE id_medico = p_id_medico;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `sp_buscar_medico_por_id`;
DELIMITER $$
CREATE PROCEDURE `sp_buscar_medico_por_id`(IN p_id_medico INT)
BEGIN
    SELECT * FROM medicos WHERE id_medico = p_id_medico;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `sp_buscar_medico_por_colegiatura`;
DELIMITER $$
CREATE PROCEDURE `sp_buscar_medico_por_colegiatura`(IN p_colegiatura VARCHAR(20))
BEGIN
    SELECT * FROM medicos WHERE numero_colegiatura = p_colegiatura;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `sp_buscar_medico_por_usuario`;
DELIMITER $$
CREATE PROCEDURE `sp_buscar_medico_por_usuario`(IN p_id_usuario INT)
BEGIN
    SELECT * FROM medicos WHERE id_usuario = p_id_usuario;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `sp_listar_medicos`;
DELIMITER $$
CREATE PROCEDURE `sp_listar_medicos`()
BEGIN
    SELECT * FROM medicos;
END$$
DELIMITER ;

-- =====================================================================
-- MÓDULO: CITAS
-- =====================================================================

-- CORREGIDO: se quitó el OUT (Java nunca lo registraba) y se ajustó a 6
-- parámetros IN exactos, terminando con SELECT LAST_INSERT_ID().
DROP PROCEDURE IF EXISTS `SP_RegistrarCitaConValidacion`;
DELIMITER $$
CREATE PROCEDURE `SP_RegistrarCitaConValidacion`(
    IN p_id_paciente INT, IN p_id_medico INT, IN p_fecha DATE,
    IN p_hora_inicio TIME, IN p_hora_fin TIME, IN p_estado VARCHAR(20)
)
BEGIN
    DECLARE v_conflictos INT DEFAULT 0;
    DECLARE v_horario_valido INT DEFAULT 0;
    DECLARE v_existe INT DEFAULT 0;
    DECLARE v_lock_medico INT;
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    IF p_hora_inicio IS NULL OR p_hora_fin IS NULL OR p_hora_fin <= p_hora_inicio THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El horario de la cita no es válido';
    END IF;

    IF TIMESTAMPDIFF(
        MINUTE,
        TIMESTAMP(p_fecha, p_hora_inicio),
        TIMESTAMP(p_fecha, p_hora_fin)
    ) <> 30 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'La cita debe tener una duración exacta de 30 minutos';
    END IF;

    SELECT COUNT(*) INTO v_existe
    FROM pacientes
    WHERE id_paciente = p_id_paciente AND estado = 'ACTIVO';

    IF v_existe = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El paciente no existe o se encuentra inactivo';
    END IF;

    SELECT COUNT(*) INTO v_existe
    FROM medicos
    WHERE id_medico = p_id_medico;

    IF v_existe = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El médico no existe';
    END IF;

    SELECT COUNT(*) INTO v_horario_valido
    FROM horarios_medico h
    WHERE h.id_medico = p_id_medico
      AND h.dia_semana = ELT(
            WEEKDAY(p_fecha) + 1,
            'LUNES','MARTES','MIERCOLES','JUEVES','VIERNES','SABADO','DOMINGO'
          )
      AND p_hora_inicio >= h.hora_inicio
      AND p_hora_fin <= h.hora_fin;

    IF v_horario_valido = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'La cita se encuentra fuera del horario registrado del médico';
    END IF;

    START TRANSACTION;

        -- Serializa las reservas del mismo médico y evita conflictos concurrentes.
        SELECT id_medico INTO v_lock_medico
        FROM medicos
        WHERE id_medico = p_id_medico
        FOR UPDATE;

        SELECT COUNT(*) INTO v_conflictos
        FROM citas
        WHERE id_medico = p_id_medico
          AND fecha = p_fecha
          AND estado NOT IN ('CANCELADA', 'NO_ASISTIO')
          AND p_hora_inicio < hora_fin
          AND p_hora_fin > hora_inicio;

        IF v_conflictos > 0 THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'El médico ya tiene una cita que se cruza con ese horario';
        END IF;

        INSERT INTO citas (
            id_paciente, id_medico, fecha, hora_inicio, hora_fin, estado
        )
        VALUES (
            p_id_paciente, p_id_medico, p_fecha, p_hora_inicio, p_hora_fin,
            IFNULL(NULLIF(p_estado, ''), 'PROGRAMADA')
        );

    COMMIT;

    SELECT LAST_INSERT_ID() AS id;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `sp_actualizar_cita`;
DELIMITER $$
CREATE PROCEDURE `sp_actualizar_cita`(
    IN p_fecha DATE, IN p_hora_inicio TIME, IN p_hora_fin TIME,
    IN p_estado VARCHAR(20), IN p_id_cita INT
)
BEGIN
    DECLARE v_id_medico INT;
    DECLARE v_estado_actual VARCHAR(20);
    DECLARE v_conflictos INT DEFAULT 0;
    DECLARE v_horario_valido INT DEFAULT 0;
    DECLARE v_lock_medico INT;
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    SELECT id_medico, estado
    INTO v_id_medico, v_estado_actual
    FROM citas
    WHERE id_cita = p_id_cita;

    IF v_id_medico IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'La cita indicada no existe';
    END IF;

    IF v_estado_actual = 'ATENDIDA' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Una cita atendida no puede reprogramarse';
    END IF;

    IF p_hora_inicio IS NULL OR p_hora_fin IS NULL OR p_hora_fin <= p_hora_inicio THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El horario de la cita no es válido';
    END IF;

    IF TIMESTAMPDIFF(
        MINUTE,
        TIMESTAMP(p_fecha, p_hora_inicio),
        TIMESTAMP(p_fecha, p_hora_fin)
    ) <> 30 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'La cita debe tener una duración exacta de 30 minutos';
    END IF;

    IF p_estado NOT IN ('CANCELADA', 'NO_ASISTIO') THEN
        SELECT COUNT(*) INTO v_horario_valido
        FROM horarios_medico h
        WHERE h.id_medico = v_id_medico
          AND h.dia_semana = ELT(
                WEEKDAY(p_fecha) + 1,
                'LUNES','MARTES','MIERCOLES','JUEVES','VIERNES','SABADO','DOMINGO'
              )
          AND p_hora_inicio >= h.hora_inicio
          AND p_hora_fin <= h.hora_fin;

        IF v_horario_valido = 0 THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'La cita se encuentra fuera del horario registrado del médico';
        END IF;
    END IF;

    START TRANSACTION;

        SELECT id_medico INTO v_lock_medico
        FROM medicos
        WHERE id_medico = v_id_medico
        FOR UPDATE;

        IF p_estado NOT IN ('CANCELADA', 'NO_ASISTIO') THEN
            SELECT COUNT(*) INTO v_conflictos
            FROM citas
            WHERE id_medico = v_id_medico
              AND fecha = p_fecha
              AND id_cita <> p_id_cita
              AND estado NOT IN ('CANCELADA', 'NO_ASISTIO')
              AND p_hora_inicio < hora_fin
              AND p_hora_fin > hora_inicio;

            IF v_conflictos > 0 THEN
                SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'El médico ya tiene una cita que se cruza con ese horario';
            END IF;
        END IF;

        UPDATE citas
        SET fecha = p_fecha,
            hora_inicio = p_hora_inicio,
            hora_fin = p_hora_fin,
            estado = p_estado
        WHERE id_cita = p_id_cita;

    COMMIT;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `SP_CancelarCita`;
DELIMITER $$
CREATE PROCEDURE `SP_CancelarCita`(IN p_estado VARCHAR(20), IN p_id_cita INT)
BEGIN
    -- Correcto tal como estaba: se usa tanto para cambiar de estado como
    -- para la cancelación (RN-20/RN-21 se validan en la capa de negocio).
    UPDATE citas SET estado = p_estado WHERE id_cita = p_id_cita;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `sp_listar_citas_por_medico_y_fecha`;
DELIMITER $$
CREATE PROCEDURE `sp_listar_citas_por_medico_y_fecha`(
    IN p_id_medico INT, IN p_fecha DATE
)
BEGIN
    SELECT * FROM citas WHERE id_medico = p_id_medico AND fecha = p_fecha ORDER BY hora_inicio;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `sp_listar_citas_por_paciente`;
DELIMITER $$
CREATE PROCEDURE `sp_listar_citas_por_paciente`(IN p_id_paciente INT)
BEGIN
    SELECT * FROM citas WHERE id_paciente = p_id_paciente ORDER BY fecha DESC, hora_inicio DESC;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `sp_listar_citas`;
DELIMITER $$
CREATE PROCEDURE `sp_listar_citas`()
BEGIN
    SELECT * FROM citas ORDER BY fecha DESC, hora_inicio DESC;
END$$
DELIMITER ;

-- =====================================================================
-- MÓDULO: ATENCIÓN MÉDICA
-- =====================================================================

-- CORREGIDO: la versión original pedía 14 parámetros (mezclaba signos
-- vitales y diagnóstico, que se insertan aparte); ahora son los 6
-- exactos que manda DAL_AtencionMedica.insertar.
DROP PROCEDURE IF EXISTS `SP_RegistrarAtencionCompleta`;
DELIMITER $$
CREATE PROCEDURE `SP_RegistrarAtencionCompleta`(
    IN p_id_cita INT, IN p_motivo VARCHAR(300), IN p_antecedentes VARCHAR(500),
    IN p_tratamiento VARCHAR(500), IN p_observaciones VARCHAR(500), IN p_cerrada BOOLEAN
)
BEGIN
    DECLARE v_estado_cita VARCHAR(20);
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    SELECT estado INTO v_estado_cita
    FROM citas
    WHERE id_cita = p_id_cita;

    IF v_estado_cita IS NULL OR v_estado_cita <> 'CONFIRMADA' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'La cita debe estar CONFIRMADA para registrar la atención';
    END IF;

    IF p_motivo IS NULL OR CHAR_LENGTH(TRIM(p_motivo)) = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El motivo de consulta es obligatorio';
    END IF;

    -- Los signos vitales y diagnósticos se registran después.
    -- Por integridad clínica, una atención nueva no puede nacer cerrada.
    IF p_cerrada = TRUE THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'La atención debe registrarse abierta y cerrarse después de completar el registro clínico';
    END IF;

    START TRANSACTION;

        INSERT INTO atenciones_medicas (
            id_cita, motivo_consulta, antecedentes,
            tratamiento, observaciones, cerrada
        )
        VALUES (
            p_id_cita, p_motivo, p_antecedentes,
            p_tratamiento, p_observaciones, FALSE
        );

        UPDATE citas
        SET estado = 'ATENDIDA'
        WHERE id_cita = p_id_cita;

    COMMIT;

    SELECT LAST_INSERT_ID() AS id;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `sp_actualizar_atencion_medica`;
DELIMITER $$
CREATE PROCEDURE `sp_actualizar_atencion_medica`(
    IN p_motivo VARCHAR(300), IN p_antecedentes VARCHAR(500),
    IN p_tratamiento VARCHAR(500), IN p_observaciones VARCHAR(500),
    IN p_cerrada BOOLEAN, IN p_id_atencion INT
)
BEGIN
    DECLARE v_cerrada_actual BOOLEAN;
    DECLARE v_signos INT DEFAULT 0;
    DECLARE v_diagnosticos INT DEFAULT 0;

    SELECT cerrada INTO v_cerrada_actual
    FROM atenciones_medicas
    WHERE id_atencion = p_id_atencion;

    IF v_cerrada_actual IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'La atención indicada no existe';
    END IF;

    IF v_cerrada_actual = TRUE THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'La atención ya está cerrada y no puede modificarse';
    END IF;

    IF p_motivo IS NULL OR CHAR_LENGTH(TRIM(p_motivo)) = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El motivo de consulta es obligatorio';
    END IF;

    IF p_cerrada = TRUE THEN
        SELECT COUNT(*) INTO v_signos
        FROM signos_vitales
        WHERE id_atencion = p_id_atencion;

        SELECT COUNT(*) INTO v_diagnosticos
        FROM diagnosticos
        WHERE id_atencion = p_id_atencion;

        IF v_signos <> 1 THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'No se puede cerrar la atención sin registrar signos vitales';
        END IF;

        IF v_diagnosticos < 1 THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'No se puede cerrar la atención sin registrar al menos un diagnóstico';
        END IF;
    END IF;

    UPDATE atenciones_medicas
    SET motivo_consulta = p_motivo,
        antecedentes = p_antecedentes,
        tratamiento = p_tratamiento,
        observaciones = p_observaciones,
        cerrada = p_cerrada
    WHERE id_atencion = p_id_atencion;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `sp_cerrar_atencion_medica`;
DELIMITER $$
CREATE PROCEDURE `sp_cerrar_atencion_medica`(IN p_id_atencion INT)
BEGIN
    DECLARE v_existe INT DEFAULT 0;
    DECLARE v_cerrada BOOLEAN;
    DECLARE v_signos INT DEFAULT 0;
    DECLARE v_diagnosticos INT DEFAULT 0;

    SELECT COUNT(*), MAX(cerrada)
    INTO v_existe, v_cerrada
    FROM atenciones_medicas
    WHERE id_atencion = p_id_atencion;

    IF v_existe = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'La atención indicada no existe';
    END IF;

    IF v_cerrada = TRUE THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'La atención ya se encuentra cerrada';
    END IF;

    SELECT COUNT(*) INTO v_signos
    FROM signos_vitales
    WHERE id_atencion = p_id_atencion;

    SELECT COUNT(*) INTO v_diagnosticos
    FROM diagnosticos
    WHERE id_atencion = p_id_atencion;

    IF v_signos <> 1 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'No se puede cerrar la atención sin registrar signos vitales';
    END IF;

    IF v_diagnosticos < 1 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'No se puede cerrar la atención sin registrar al menos un diagnóstico';
    END IF;

    UPDATE atenciones_medicas
    SET cerrada = TRUE
    WHERE id_atencion = p_id_atencion;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `sp_buscar_atencion_medica_por_id`;
DELIMITER $$
CREATE PROCEDURE `sp_buscar_atencion_medica_por_id`(IN p_id_atencion INT)
BEGIN
    SELECT * FROM atenciones_medicas WHERE id_atencion = p_id_atencion;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `sp_listar_atenciones_medicas`;
DELIMITER $$
CREATE PROCEDURE `sp_listar_atenciones_medicas`()
BEGIN
    SELECT * FROM atenciones_medicas ORDER BY fecha_atencion DESC;
END$$
DELIMITER ;

-- =====================================================================
-- MÓDULO: AUDITORÍA
-- =====================================================================

DROP PROCEDURE IF EXISTS `sp_actualizar_auditoria`;
DELIMITER $$
CREATE PROCEDURE `sp_actualizar_auditoria`(
    IN p_id_usuario INT, IN p_modulo VARCHAR(50), IN p_operacion VARCHAR(50),
    IN p_detalle VARCHAR(500), IN p_id_auditoria BIGINT
)
BEGIN
    UPDATE auditoria
    SET id_usuario = p_id_usuario, modulo = p_modulo, operacion = p_operacion, detalle = p_detalle
    WHERE id_auditoria = p_id_auditoria;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `sp_eliminar_auditoria`;
DELIMITER $$
CREATE PROCEDURE `sp_eliminar_auditoria`(IN p_id_auditoria BIGINT)
BEGIN
    DELETE FROM auditoria WHERE id_auditoria = p_id_auditoria;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `sp_buscar_auditoria_por_id`;
DELIMITER $$
CREATE PROCEDURE `sp_buscar_auditoria_por_id`(IN p_id_auditoria BIGINT)
BEGIN
    SELECT * FROM auditoria WHERE id_auditoria = p_id_auditoria;
END$$
DELIMITER ;

-- CORREGIDO: la versión original pedía 3 parámetros de fecha/módulo;
-- DAL_Auditoria.listarPorUsuario solo manda 1 (idUsuario) y espera
-- filtrar exactamente por ese usuario.
DROP PROCEDURE IF EXISTS `SP_ListarAuditoriaPorFiltros`;
DELIMITER $$
CREATE PROCEDURE `SP_ListarAuditoriaPorFiltros`(IN p_id_usuario INT)
BEGIN
    SELECT * FROM auditoria WHERE id_usuario = p_id_usuario ORDER BY fecha_hora DESC;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `sp_listar_auditorias_por_modulo`;
DELIMITER $$
CREATE PROCEDURE `sp_listar_auditorias_por_modulo`(IN p_modulo VARCHAR(50))
BEGIN
    SELECT * FROM auditoria WHERE modulo = p_modulo ORDER BY fecha_hora DESC;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `sp_listar_auditorias`;
DELIMITER $$
CREATE PROCEDURE `sp_listar_auditorias`()
BEGIN
    SELECT * FROM auditoria ORDER BY fecha_hora DESC;
END$$
DELIMITER ;

-- =====================================================================
-- MÓDULO: ENTREGA DE MEDICAMENTOS
-- =====================================================================

-- CORREGIDO: se quitó el OUT (Java no lo registraba), se movió
-- START TRANSACTION antes del SELECT ... FOR UPDATE (el bloqueo de fila
-- no servía de nada si la transacción empezaba después), se usa SIGNAL
-- para stock insuficiente (RN-33) y se agrega SELECT LAST_INSERT_ID().
DROP PROCEDURE IF EXISTS `SP_EntregarMedicamentoConStock`;
DELIMITER $$
CREATE PROCEDURE `SP_EntregarMedicamentoConStock`(
    IN p_id_receta_detalle INT, IN p_id_farmaceutico INT, IN p_cantidad INT
)
BEGIN
    DECLARE v_id_medicamento INT;
    DECLARE v_stock_actual INT;
    DECLARE v_cantidad_prescrita INT;
    DECLARE v_rol VARCHAR(30);
    DECLARE v_ya_entregado INT DEFAULT 0;
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    IF p_cantidad IS NULL OR p_cantidad <= 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'La cantidad entregada debe ser mayor que cero';
    END IF;

    SELECT r.nombre_rol INTO v_rol
    FROM usuarios u
    INNER JOIN roles r ON r.id_rol = u.id_rol
    WHERE u.id_usuario = p_id_farmaceutico
      AND u.estado = 'ACTIVO';

    IF v_rol IS NULL OR v_rol <> 'FARMACEUTICO' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El usuario responsable debe tener el rol FARMACEUTICO';
    END IF;

    SELECT id_medicamento, cantidad
    INTO v_id_medicamento, v_cantidad_prescrita
    FROM receta_detalle
    WHERE id_receta_detalle = p_id_receta_detalle;

    IF v_id_medicamento IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El detalle de receta indicado no existe';
    END IF;

    -- El modelo considera una sola entrega completa por detalle de receta.
    IF p_cantidad <> v_cantidad_prescrita THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'La cantidad entregada debe coincidir con la cantidad prescrita';
    END IF;

    SELECT COUNT(*) INTO v_ya_entregado
    FROM entregas_medicamentos
    WHERE id_receta_detalle = p_id_receta_detalle;

    IF v_ya_entregado > 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El medicamento de este detalle ya fue entregado';
    END IF;

    START TRANSACTION;

        SELECT stock_actual INTO v_stock_actual
        FROM medicamentos
        WHERE id_medicamento = v_id_medicamento
        FOR UPDATE;

        IF v_stock_actual < p_cantidad THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Stock insuficiente para realizar la entrega';
        END IF;

        INSERT INTO entregas_medicamentos (
            id_receta_detalle, id_farmaceutico, cantidad_entregada
        )
        VALUES (
            p_id_receta_detalle, p_id_farmaceutico, p_cantidad
        );

        UPDATE medicamentos
        SET stock_actual = stock_actual - p_cantidad
        WHERE id_medicamento = v_id_medicamento;

    COMMIT;

    SELECT LAST_INSERT_ID() AS id;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `sp_actualizar_entrega_medicamento`;
DELIMITER $$
CREATE PROCEDURE `sp_actualizar_entrega_medicamento`(
    IN p_id_receta_detalle INT, IN p_id_farmaceutico INT,
    IN p_cantidad_entregada INT, IN p_id_entrega INT
)
BEGIN
    DECLARE v_detalle_anterior INT;
    DECLARE v_cantidad_anterior INT;
    DECLARE v_medicamento_anterior INT;
    DECLARE v_medicamento_nuevo INT;
    DECLARE v_cantidad_prescrita INT;
    DECLARE v_stock_anterior INT;
    DECLARE v_stock_nuevo INT;
    DECLARE v_rol VARCHAR(30);
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    IF p_cantidad_entregada IS NULL OR p_cantidad_entregada <= 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'La cantidad entregada debe ser mayor que cero';
    END IF;

    SELECT r.nombre_rol INTO v_rol
    FROM usuarios u
    INNER JOIN roles r ON r.id_rol = u.id_rol
    WHERE u.id_usuario = p_id_farmaceutico
      AND u.estado = 'ACTIVO';

    IF v_rol IS NULL OR v_rol <> 'FARMACEUTICO' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El usuario responsable debe tener el rol FARMACEUTICO';
    END IF;

    START TRANSACTION;

        SELECT em.id_receta_detalle,
               em.cantidad_entregada,
               rd.id_medicamento
        INTO v_detalle_anterior,
             v_cantidad_anterior,
             v_medicamento_anterior
        FROM entregas_medicamentos em
        INNER JOIN receta_detalle rd
            ON rd.id_receta_detalle = em.id_receta_detalle
        WHERE em.id_entrega = p_id_entrega
        FOR UPDATE;

        IF v_detalle_anterior IS NULL THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'La entrega indicada no existe';
        END IF;

        SELECT id_medicamento, cantidad
        INTO v_medicamento_nuevo, v_cantidad_prescrita
        FROM receta_detalle
        WHERE id_receta_detalle = p_id_receta_detalle;

        IF v_medicamento_nuevo IS NULL THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'El nuevo detalle de receta no existe';
        END IF;

        IF p_cantidad_entregada <> v_cantidad_prescrita THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'La cantidad entregada debe coincidir con la cantidad prescrita';
        END IF;

        IF v_medicamento_anterior = v_medicamento_nuevo THEN

            SELECT stock_actual INTO v_stock_anterior
            FROM medicamentos
            WHERE id_medicamento = v_medicamento_anterior
            FOR UPDATE;

            IF v_stock_anterior + v_cantidad_anterior < p_cantidad_entregada THEN
                SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Stock insuficiente para actualizar la entrega';
            END IF;

            UPDATE medicamentos
            SET stock_actual = stock_actual
                               + v_cantidad_anterior
                               - p_cantidad_entregada
            WHERE id_medicamento = v_medicamento_anterior;

        ELSE

            -- Bloqueo en orden determinista para reducir riesgo de interbloqueo.
            IF v_medicamento_anterior < v_medicamento_nuevo THEN
                SELECT stock_actual INTO v_stock_anterior
                FROM medicamentos
                WHERE id_medicamento = v_medicamento_anterior
                FOR UPDATE;

                SELECT stock_actual INTO v_stock_nuevo
                FROM medicamentos
                WHERE id_medicamento = v_medicamento_nuevo
                FOR UPDATE;
            ELSE
                SELECT stock_actual INTO v_stock_nuevo
                FROM medicamentos
                WHERE id_medicamento = v_medicamento_nuevo
                FOR UPDATE;

                SELECT stock_actual INTO v_stock_anterior
                FROM medicamentos
                WHERE id_medicamento = v_medicamento_anterior
                FOR UPDATE;
            END IF;

            IF v_stock_nuevo < p_cantidad_entregada THEN
                SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Stock insuficiente para el nuevo medicamento';
            END IF;

            UPDATE medicamentos
            SET stock_actual = stock_actual + v_cantidad_anterior
            WHERE id_medicamento = v_medicamento_anterior;

            UPDATE medicamentos
            SET stock_actual = stock_actual - p_cantidad_entregada
            WHERE id_medicamento = v_medicamento_nuevo;

        END IF;

        UPDATE entregas_medicamentos
        SET id_receta_detalle = p_id_receta_detalle,
            id_farmaceutico = p_id_farmaceutico,
            cantidad_entregada = p_cantidad_entregada
        WHERE id_entrega = p_id_entrega;

    COMMIT;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `sp_eliminar_entrega_medicamento`;
DELIMITER $$
CREATE PROCEDURE `sp_eliminar_entrega_medicamento`(IN p_id_entrega INT)
BEGIN
    DECLARE v_id_medicamento INT;
    DECLARE v_cantidad INT;
    DECLARE v_stock_actual INT;
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;

        SELECT rd.id_medicamento, em.cantidad_entregada
        INTO v_id_medicamento, v_cantidad
        FROM entregas_medicamentos em
        INNER JOIN receta_detalle rd
            ON rd.id_receta_detalle = em.id_receta_detalle
        WHERE em.id_entrega = p_id_entrega
        FOR UPDATE;

        IF v_id_medicamento IS NULL THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'La entrega indicada no existe';
        END IF;

        SELECT stock_actual INTO v_stock_actual
        FROM medicamentos
        WHERE id_medicamento = v_id_medicamento
        FOR UPDATE;

        UPDATE medicamentos
        SET stock_actual = stock_actual + v_cantidad
        WHERE id_medicamento = v_id_medicamento;

        DELETE FROM entregas_medicamentos
        WHERE id_entrega = p_id_entrega;

    COMMIT;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `sp_buscar_entrega_medicamento_por_id`;
DELIMITER $$
CREATE PROCEDURE `sp_buscar_entrega_medicamento_por_id`(IN p_id_entrega INT)
BEGIN
    SELECT * FROM entregas_medicamentos WHERE id_entrega = p_id_entrega;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `sp_listar_entregas_por_receta_detalle`;
DELIMITER $$
CREATE PROCEDURE `sp_listar_entregas_por_receta_detalle`(IN p_id_receta_detalle INT)
BEGIN
    SELECT * FROM entregas_medicamentos WHERE id_receta_detalle = p_id_receta_detalle;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `sp_listar_entregas_por_farmaceutico`;
DELIMITER $$
CREATE PROCEDURE `sp_listar_entregas_por_farmaceutico`(IN p_id_farmaceutico INT)
BEGIN
    SELECT * FROM entregas_medicamentos WHERE id_farmaceutico = p_id_farmaceutico;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `sp_listar_entregas_medicamentos`;
DELIMITER $$
CREATE PROCEDURE `sp_listar_entregas_medicamentos`()
BEGIN
    SELECT * FROM entregas_medicamentos ORDER BY fecha_entrega DESC;
END$$
DELIMITER ;

-- =====================================================================
-- MÓDULO: RESULTADOS DE LABORATORIO
-- =====================================================================

-- CORREGIDO: solo le faltaba retornar el ID generado (DAL_ResultadoLaboratorio
-- .insertar llama a getGeneratedKeys()).
DROP PROCEDURE IF EXISTS `SP_RegistrarResultadoLaboratorio`;
DELIMITER $$
CREATE PROCEDURE `SP_RegistrarResultadoLaboratorio`(
    IN p_id_examen INT, IN p_id_laboratorista INT,
    IN p_resultado TEXT, IN p_ruta VARCHAR(255)
)
BEGIN
    DECLARE v_rol VARCHAR(30);
    DECLARE v_estado_examen VARCHAR(20);
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    IF (p_resultado IS NULL OR CHAR_LENGTH(TRIM(p_resultado)) = 0)
       AND
       (p_ruta IS NULL OR CHAR_LENGTH(TRIM(p_ruta)) = 0) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Debe registrar un resultado escrito o una ruta de archivo';
    END IF;

    SELECT r.nombre_rol INTO v_rol
    FROM usuarios u
    INNER JOIN roles r ON r.id_rol = u.id_rol
    WHERE u.id_usuario = p_id_laboratorista
      AND u.estado = 'ACTIVO';

    IF v_rol IS NULL OR v_rol <> 'LABORATORISTA' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El usuario responsable debe tener el rol LABORATORISTA';
    END IF;

    SELECT estado INTO v_estado_examen
    FROM examenes_laboratorio
    WHERE id_examen = p_id_examen;

    IF v_estado_examen IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'La solicitud de examen no existe';
    END IF;

    IF v_estado_examen = 'ENTREGADO' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'No se puede registrar un resultado para un examen ya entregado';
    END IF;

    START TRANSACTION;

        INSERT INTO resultados_laboratorio (
            id_examen, id_laboratorista, resultado_texto, ruta_archivo
        )
        VALUES (
            p_id_examen, p_id_laboratorista, NULLIF(TRIM(p_resultado), ''),
            NULLIF(TRIM(p_ruta), '')
        );

        UPDATE examenes_laboratorio
        SET estado = 'EN_PROCESO'
        WHERE id_examen = p_id_examen;

    COMMIT;

    SELECT LAST_INSERT_ID() AS id;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `sp_actualizar_resultado_laboratorio`;
DELIMITER $$
CREATE PROCEDURE `sp_actualizar_resultado_laboratorio`(
    IN p_id_laboratorista INT, IN p_resultado TEXT,
    IN p_ruta VARCHAR(255), IN p_id_resultado INT
)
BEGIN
    DECLARE v_rol VARCHAR(30);
    DECLARE v_id_examen INT;

    IF (p_resultado IS NULL OR CHAR_LENGTH(TRIM(p_resultado)) = 0)
       AND
       (p_ruta IS NULL OR CHAR_LENGTH(TRIM(p_ruta)) = 0) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Debe registrar un resultado escrito o una ruta de archivo';
    END IF;

    SELECT r.nombre_rol INTO v_rol
    FROM usuarios u
    INNER JOIN roles r ON r.id_rol = u.id_rol
    WHERE u.id_usuario = p_id_laboratorista
      AND u.estado = 'ACTIVO';

    IF v_rol IS NULL OR v_rol <> 'LABORATORISTA' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El usuario responsable debe tener el rol LABORATORISTA';
    END IF;

    SELECT id_examen INTO v_id_examen
    FROM resultados_laboratorio
    WHERE id_resultado = p_id_resultado;

    IF v_id_examen IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El resultado indicado no existe';
    END IF;

    UPDATE resultados_laboratorio
    SET id_laboratorista = p_id_laboratorista,
        resultado_texto = NULLIF(TRIM(p_resultado), ''),
        ruta_archivo = NULLIF(TRIM(p_ruta), '')
    WHERE id_resultado = p_id_resultado;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `sp_eliminar_resultado_laboratorio`;
DELIMITER $$
CREATE PROCEDURE `sp_eliminar_resultado_laboratorio`(IN p_id_resultado INT)
BEGIN
    DECLARE v_id_examen INT;
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    SELECT id_examen INTO v_id_examen
    FROM resultados_laboratorio
    WHERE id_resultado = p_id_resultado;

    IF v_id_examen IS NULL THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El resultado indicado no existe';
    END IF;

    START TRANSACTION;

        DELETE FROM resultados_laboratorio
        WHERE id_resultado = p_id_resultado;

        UPDATE examenes_laboratorio
        SET estado = 'EN_PROCESO'
        WHERE id_examen = v_id_examen;

    COMMIT;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `sp_buscar_resultado_laboratorio_por_id`;
DELIMITER $$
CREATE PROCEDURE `sp_buscar_resultado_laboratorio_por_id`(IN p_id_resultado INT)
BEGIN
    SELECT * FROM resultados_laboratorio WHERE id_resultado = p_id_resultado;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `sp_buscar_resultado_laboratorio_por_examen`;
DELIMITER $$
CREATE PROCEDURE `sp_buscar_resultado_laboratorio_por_examen`(IN p_id_examen INT)
BEGIN
    SELECT * FROM resultados_laboratorio WHERE id_examen = p_id_examen;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `sp_listar_resultados_laboratorio`;
DELIMITER $$
CREATE PROCEDURE `sp_listar_resultados_laboratorio`()
BEGIN
    SELECT * FROM resultados_laboratorio ORDER BY fecha_resultado DESC;
END$$
DELIMITER ;

-- =====================================================================
-- MÓDULO: PAGOS
-- =====================================================================

-- CORREGIDO: la versión original pedía 5 parámetros (incluía tipo_comprobante);
-- Java solo manda 4. Se fija 'BOLETA' por defecto y se retorna LAST_INSERT_ID.
DROP PROCEDURE IF EXISTS `SP_RegistrarPagoConComprobante`;
DELIMITER $$
CREATE PROCEDURE `SP_RegistrarPagoConComprobante`(
    IN p_id_atencion INT, IN p_id_cajero INT,
    IN p_monto DECIMAL(10,2), IN p_metodo VARCHAR(30)
)
BEGIN
    DECLARE v_id_pago INT;
    DECLARE v_correlativo VARCHAR(30);
    DECLARE v_rol VARCHAR(30);
    DECLARE v_pago_existente INT DEFAULT 0;
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    IF p_monto IS NULL OR p_monto <= 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El monto del pago debe ser mayor que cero';
    END IF;

    IF p_metodo NOT IN ('EFECTIVO','TARJETA','TRANSFERENCIA','BILLETERA_ELECTRONICA') THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El método de pago no es válido';
    END IF;

    SELECT r.nombre_rol INTO v_rol
    FROM usuarios u
    INNER JOIN roles r ON r.id_rol = u.id_rol
    WHERE u.id_usuario = p_id_cajero
      AND u.estado = 'ACTIVO';

    IF v_rol IS NULL OR v_rol <> 'CAJERO' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El usuario responsable debe tener el rol CAJERO';
    END IF;

    SELECT COUNT(*) INTO v_pago_existente
    FROM pagos
    WHERE id_atencion = p_id_atencion;

    IF v_pago_existente > 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'La atención ya tiene un pago registrado';
    END IF;

    START TRANSACTION;

        INSERT INTO pagos (
            id_atencion, id_cajero, monto, metodo_pago
        )
        VALUES (
            p_id_atencion, p_id_cajero, p_monto, p_metodo
        );

        SET v_id_pago = LAST_INSERT_ID();
        SET v_correlativo = CONCAT('BOL-', LPAD(v_id_pago, 6, '0'));

        INSERT INTO comprobantes (
            id_pago, numero_comprobante, tipo_comprobante
        )
        VALUES (
            v_id_pago, v_correlativo, 'BOLETA'
        );

    COMMIT;

    SELECT v_id_pago AS id;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `sp_actualizar_pago`;
DELIMITER $$
CREATE PROCEDURE `sp_actualizar_pago`(
    IN p_id_atencion INT, IN p_id_cajero INT,
    IN p_monto DECIMAL(10,2), IN p_metodo_pago VARCHAR(30),
    IN p_id_pago INT
)
BEGIN
    DECLARE v_rol VARCHAR(30);
    DECLARE v_existe INT DEFAULT 0;

    IF p_monto IS NULL OR p_monto <= 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El monto del pago debe ser mayor que cero';
    END IF;

    IF p_metodo_pago NOT IN ('EFECTIVO','TARJETA','TRANSFERENCIA','BILLETERA_ELECTRONICA') THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El método de pago no es válido';
    END IF;

    SELECT r.nombre_rol INTO v_rol
    FROM usuarios u
    INNER JOIN roles r ON r.id_rol = u.id_rol
    WHERE u.id_usuario = p_id_cajero
      AND u.estado = 'ACTIVO';

    IF v_rol IS NULL OR v_rol <> 'CAJERO' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El usuario responsable debe tener el rol CAJERO';
    END IF;

    SELECT COUNT(*) INTO v_existe
    FROM pagos
    WHERE id_pago = p_id_pago;

    IF v_existe = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El pago indicado no existe';
    END IF;

    UPDATE pagos
    SET id_atencion = p_id_atencion,
        id_cajero = p_id_cajero,
        monto = p_monto,
        metodo_pago = p_metodo_pago
    WHERE id_pago = p_id_pago;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `sp_eliminar_pago`;
DELIMITER $$
CREATE PROCEDURE `sp_eliminar_pago`(IN p_id_pago INT)
BEGIN
    DELETE FROM pagos WHERE id_pago = p_id_pago;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `sp_buscar_pago_por_id`;
DELIMITER $$
CREATE PROCEDURE `sp_buscar_pago_por_id`(IN p_id_pago INT)
BEGIN
    SELECT * FROM pagos WHERE id_pago = p_id_pago;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `sp_buscar_pago_por_atencion`;
DELIMITER $$
CREATE PROCEDURE `sp_buscar_pago_por_atencion`(IN p_id_atencion INT)
BEGIN
    SELECT * FROM pagos WHERE id_atencion = p_id_atencion;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `sp_listar_pagos_por_cajero`;
DELIMITER $$
CREATE PROCEDURE `sp_listar_pagos_por_cajero`(IN p_id_cajero INT)
BEGIN
    SELECT * FROM pagos WHERE id_cajero = p_id_cajero ORDER BY fecha_pago DESC;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `sp_listar_pagos`;
DELIMITER $$
CREATE PROCEDURE `sp_listar_pagos`()
BEGIN
    SELECT * FROM pagos ORDER BY fecha_pago DESC;
END$$
DELIMITER ;

-- =====================================================================
-- MÓDULO: REPORTES
-- (SP_ReporteGeneralDiario original no se usa: DAL_Reporte llama a
-- estos 4 procedimientos independientes)
-- =====================================================================

DROP PROCEDURE IF EXISTS `sp_obtener_total_pacientes`;
DELIMITER $$
CREATE PROCEDURE `sp_obtener_total_pacientes`()
BEGIN
    -- DAL_Reporte lee rs.getInt(1), no necesita alias
    SELECT COUNT(*) FROM pacientes WHERE estado = 'ACTIVO';
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `sp_obtener_ingresos_diarios`;
DELIMITER $$
CREATE PROCEDURE `sp_obtener_ingresos_diarios`()
BEGIN
    -- DAL_Reporte lee rs.getBigDecimal(1), no necesita alias
    SELECT COALESCE(SUM(monto), 0) FROM pagos WHERE DATE(fecha_pago) = CURDATE();
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `sp_obtener_especialidad_mas_demandada`;
DELIMITER $$
CREATE PROCEDURE `sp_obtener_especialidad_mas_demandada`()
BEGIN
    -- DAL_Reporte lee rs.getString("nombre") y rs.getInt("total")
    SELECT e.nombre AS nombre, COUNT(c.id_cita) AS total
    FROM citas c
    JOIN medicos m ON c.id_medico = m.id_medico
    JOIN medico_especialidad me ON m.id_medico = me.id_medico
    JOIN especialidades e ON me.id_especialidad = e.id_especialidad
    GROUP BY e.id_especialidad, e.nombre
    ORDER BY total DESC
    LIMIT 1;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS `sp_obtener_medicamento_mas_utilizado`;
DELIMITER $$
CREATE PROCEDURE `sp_obtener_medicamento_mas_utilizado`()
BEGIN
    -- DAL_Reporte lee rs.getString("nombre") y rs.getInt("total")
    SELECT m.nombre AS nombre, SUM(rd.cantidad) AS total
    FROM receta_detalle rd
    JOIN medicamentos m ON rd.id_medicamento = m.id_medicamento
    GROUP BY m.id_medicamento, m.nombre
    ORDER BY total DESC
    LIMIT 1;
END$$
DELIMITER ;

-- =====================================================================
-- VALIDACIONES DE ROLES EN BASE DE DATOS
-- Protegen la integridad incluso si alguien intenta insertar directamente.
-- =====================================================================

DELIMITER $$

DROP TRIGGER IF EXISTS bi_medicos_validar_rol$$
CREATE TRIGGER bi_medicos_validar_rol
BEFORE INSERT ON medicos
FOR EACH ROW
BEGIN
    DECLARE v_valido INT DEFAULT 0;
    SELECT COUNT(*) INTO v_valido
    FROM usuarios u
    INNER JOIN roles r ON r.id_rol = u.id_rol
    WHERE u.id_usuario = NEW.id_usuario
      AND u.estado = 'ACTIVO'
      AND r.nombre_rol = 'MEDICO';

    IF v_valido = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El usuario asociado al médico debe tener el rol MEDICO';
    END IF;
END$$

DROP TRIGGER IF EXISTS bu_medicos_validar_rol$$
CREATE TRIGGER bu_medicos_validar_rol
BEFORE UPDATE ON medicos
FOR EACH ROW
BEGIN
    DECLARE v_valido INT DEFAULT 0;
    SELECT COUNT(*) INTO v_valido
    FROM usuarios u
    INNER JOIN roles r ON r.id_rol = u.id_rol
    WHERE u.id_usuario = NEW.id_usuario
      AND u.estado = 'ACTIVO'
      AND r.nombre_rol = 'MEDICO';

    IF v_valido = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El usuario asociado al médico debe tener el rol MEDICO';
    END IF;
END$$

DROP TRIGGER IF EXISTS bi_entregas_validar_rol$$
CREATE TRIGGER bi_entregas_validar_rol
BEFORE INSERT ON entregas_medicamentos
FOR EACH ROW
BEGIN
    DECLARE v_valido INT DEFAULT 0;
    SELECT COUNT(*) INTO v_valido
    FROM usuarios u
    INNER JOIN roles r ON r.id_rol = u.id_rol
    WHERE u.id_usuario = NEW.id_farmaceutico
      AND u.estado = 'ACTIVO'
      AND r.nombre_rol = 'FARMACEUTICO';

    IF v_valido = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El usuario responsable debe tener el rol FARMACEUTICO';
    END IF;
END$$

DROP TRIGGER IF EXISTS bu_entregas_validar_rol$$
CREATE TRIGGER bu_entregas_validar_rol
BEFORE UPDATE ON entregas_medicamentos
FOR EACH ROW
BEGIN
    DECLARE v_valido INT DEFAULT 0;
    SELECT COUNT(*) INTO v_valido
    FROM usuarios u
    INNER JOIN roles r ON r.id_rol = u.id_rol
    WHERE u.id_usuario = NEW.id_farmaceutico
      AND u.estado = 'ACTIVO'
      AND r.nombre_rol = 'FARMACEUTICO';

    IF v_valido = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El usuario responsable debe tener el rol FARMACEUTICO';
    END IF;
END$$

DROP TRIGGER IF EXISTS bi_resultados_validar_rol$$
CREATE TRIGGER bi_resultados_validar_rol
BEFORE INSERT ON resultados_laboratorio
FOR EACH ROW
BEGIN
    DECLARE v_valido INT DEFAULT 0;
    SELECT COUNT(*) INTO v_valido
    FROM usuarios u
    INNER JOIN roles r ON r.id_rol = u.id_rol
    WHERE u.id_usuario = NEW.id_laboratorista
      AND u.estado = 'ACTIVO'
      AND r.nombre_rol = 'LABORATORISTA';

    IF v_valido = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El usuario responsable debe tener el rol LABORATORISTA';
    END IF;
END$$

DROP TRIGGER IF EXISTS bu_resultados_validar_rol$$
CREATE TRIGGER bu_resultados_validar_rol
BEFORE UPDATE ON resultados_laboratorio
FOR EACH ROW
BEGIN
    DECLARE v_valido INT DEFAULT 0;
    SELECT COUNT(*) INTO v_valido
    FROM usuarios u
    INNER JOIN roles r ON r.id_rol = u.id_rol
    WHERE u.id_usuario = NEW.id_laboratorista
      AND u.estado = 'ACTIVO'
      AND r.nombre_rol = 'LABORATORISTA';

    IF v_valido = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El usuario responsable debe tener el rol LABORATORISTA';
    END IF;
END$$

DROP TRIGGER IF EXISTS bi_pagos_validar_rol$$
CREATE TRIGGER bi_pagos_validar_rol
BEFORE INSERT ON pagos
FOR EACH ROW
BEGIN
    DECLARE v_valido INT DEFAULT 0;
    SELECT COUNT(*) INTO v_valido
    FROM usuarios u
    INNER JOIN roles r ON r.id_rol = u.id_rol
    WHERE u.id_usuario = NEW.id_cajero
      AND u.estado = 'ACTIVO'
      AND r.nombre_rol = 'CAJERO';

    IF v_valido = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El usuario responsable debe tener el rol CAJERO';
    END IF;
END$$

DROP TRIGGER IF EXISTS bu_pagos_validar_rol$$
CREATE TRIGGER bu_pagos_validar_rol
BEFORE UPDATE ON pagos
FOR EACH ROW
BEGIN
    DECLARE v_valido INT DEFAULT 0;
    SELECT COUNT(*) INTO v_valido
    FROM usuarios u
    INNER JOIN roles r ON r.id_rol = u.id_rol
    WHERE u.id_usuario = NEW.id_cajero
      AND u.estado = 'ACTIVO'
      AND r.nombre_rol = 'CAJERO';

    IF v_valido = 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El usuario responsable debe tener el rol CAJERO';
    END IF;
END$$

DELIMITER ;

-- =====================================================================
-- RN-06: AUDITORÍA
-- =====================================================================
-- La tabla auditoria queda preparada y el proyecto ya dispone de
-- BLLAuditoria/DAL_Auditoria. El usuario real que ejecuta cada operación
-- pertenece a la sesión Java, no a la sesión de MySQL; por ello, el registro
-- de auditoría debe invocarse desde la BLL después de cada operación exitosa.
-- No se crean triggers genéricos que atribuyan acciones al usuario equivocado.
