-- Insertar roles si no existen
INSERT IGNORE INTO roles (nombre, descripcion, activo) VALUES
('ROLE_ADMIN', 'Administrador del sistema', 1),
('ROLE_PACIENTE', 'Paciente del hospital', 1),
('ROLE_PROFESIONAL', 'Profesional de la salud', 1),
('ROLE_TUTOR', 'Tutor de paciente', 1);

-- Insertar versión de términos y condiciones si no existe
INSERT IGNORE INTO terminos_condiciones (titulo, contenido, version, activo, fecha_creacion, fecha_vigencia) VALUES
('Términos y Condiciones', 'Acepta los términos y condiciones del servicio', 1, 1, NOW(), DATE_ADD(NOW(), INTERVAL 1 YEAR));
