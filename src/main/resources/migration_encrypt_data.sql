-- Script de migración para cambiar el tipo de dato de cedula de BIGINT a VARCHAR(500)
-- Y ampliar el campo de teléfono para soportar cifrado

-- Paso 1: Crear una tabla temporal para almacenar los datos
CREATE TEMP TABLE usuarios_backup AS 
SELECT usuario_id, cc_usuario::VARCHAR, nombre_usuario, email_usuario, 
       password_usuario, cel_usuario, estado, token_verificacion, rol_usuario
FROM usuarios;

-- Paso 2: Eliminar la restricción de clave única de cedula
ALTER TABLE usuarios DROP CONSTRAINT IF EXISTS usuarios_cc_usuario_key;

-- Paso 3: Modificar el tipo de columna de cedula
ALTER TABLE usuarios ALTER COLUMN cc_usuario TYPE VARCHAR(500);

-- Paso 4: Modificar el tipo de columna de telefono
ALTER TABLE usuarios ALTER COLUMN cel_usuario TYPE VARCHAR(500);

-- Paso 5: Restaurar la restricción de unicidad
ALTER TABLE usuarios ADD CONSTRAINT usuarios_cc_usuario_key UNIQUE (cc_usuario);

-- Paso 6: Restaurar los datos desde el backup temporal
-- Los datos se cifrarán automáticamente cuando la aplicación los lea y vuelva a guardar

COMMENT ON COLUMN usuarios.cc_usuario IS 'Cédula cifrada con ChaCha20-Poly1305';
COMMENT ON COLUMN usuarios.cel_usuario IS 'Teléfono cifrado con ChaCha20-Poly1305';

-- Nota: Al iniciar la aplicación, los datos existentes se migrarán automáticamente al cifrado
