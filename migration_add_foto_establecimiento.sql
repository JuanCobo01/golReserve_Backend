-- Script para agregar el campo foto_establecimiento a la tabla establecimiento
-- Ejecutar este script en la base de datos PostgreSQL

ALTER TABLE establecimiento
ADD COLUMN foto_establecimiento VARCHAR(500);

-- Comentario para documentar el nuevo campo
COMMENT ON COLUMN establecimiento.foto_establecimiento IS 'URL de la foto del establecimiento';

-- Opcional: Agregar algunos valores de ejemplo para los establecimientos existentes
-- UPDATE establecimiento SET foto_establecimiento = 'https://ejemplo.com/fotos/establecimiento1.jpg' WHERE id_establecimiento = 1;
-- UPDATE establecimiento SET foto_establecimiento = 'https://ejemplo.com/fotos/establecimiento2.jpg' WHERE id_establecimiento = 2;
