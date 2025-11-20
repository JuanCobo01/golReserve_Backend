-- Script para arreglar las secuencias de PostgreSQL
-- Ejecutar con: psql -U postgres -d gol -f fix_sequences.sql

SELECT setval('usuarios_usuario_id_seq', COALESCE((SELECT MAX(usuario_id) FROM usuarios), 1));
SELECT setval('establecimiento_id_establecimiento_seq', COALESCE((SELECT MAX(id_establecimiento) FROM establecimiento), 1));
SELECT setval('cancha_id_cancha_seq', COALESCE((SELECT MAX(id_cancha) FROM cancha), 1));
SELECT setval('reserva_reserva_id_seq', COALESCE((SELECT MAX(reserva_id) FROM reserva), 1));
SELECT setval('pago_id_seq', COALESCE((SELECT MAX(id) FROM pago), 1));

-- Verificar las secuencias
SELECT 'usuarios_usuario_id_seq', last_value FROM usuarios_usuario_id_seq;
SELECT 'establecimiento_id_establecimiento_seq', last_value FROM establecimiento_id_establecimiento_seq;
SELECT 'cancha_id_cancha_seq', last_value FROM cancha_id_cancha_seq;
SELECT 'reserva_reserva_id_seq', last_value FROM reserva_reserva_id_seq;
SELECT 'pago_id_seq', last_value FROM pago_id_seq;
