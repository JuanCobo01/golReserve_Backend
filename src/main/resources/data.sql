-- Insertar usuarios (admin y clientes de prueba)
-- Nota: La contraseña debe estar hasheada con BCrypt
-- Password para todos: "password123" hasheado con BCrypt
INSERT INTO usuarios (nombre_usuario, email_usuario, password_usuario, cc_usuario, cel_usuario, rol_usuario, estado) VALUES
('Admin Principal', 'admin@golreserve.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '1234567890', '3001234567', 'SUPER_ADMINISTRADOR', 'ACTIVO'),
('Juan Perez', 'juan@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '1111111111', '3101111111', 'CLIENTE', 'ACTIVO'),
('Maria Lopez', 'maria@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '2222222222', '3202222222', 'CLIENTE', 'ACTIVO'),
('Carlos Gomez', 'carlos@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '3333333333', '3303333333', 'CLIENTE', 'ACTIVO'),
('Alberto Rodriguez', 'alberto@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '4444444444', '3404444444', 'CLIENTE', 'ACTIVO');

-- Insertar un establecimiento (usando el admin recién creado)
INSERT INTO establecimiento (nombre_cancha, direccion_establecimiento, admin_id) 
VALUES ('Complejo Deportivo Central', 'Calle 123 #45-67', 1);

-- Insertar las 3 canchas (FUTBOL_5, FUTBOL_7 y FUTBOL_11 según el enum TipoCancha)
-- Asociadas al establecimiento recién creado (ID=1)
INSERT INTO cancha (tipo_cancha, precio_hora, estado_cancha, establecimiento_id) VALUES
('FUTBOL_5', 50000, 'ACTIVA', 1),
('FUTBOL_7', 70000, 'ACTIVA', 1),
('FUTBOL_11', 100000, 'ACTIVA', 1);

-- Reiniciar las secuencias para que los próximos IDs sean correctos
SELECT setval('usuarios_usuario_id_seq', (SELECT MAX(usuario_id) FROM usuarios));
SELECT setval('establecimiento_id_establecimiento_seq', (SELECT MAX(id_establecimiento) FROM establecimiento));
SELECT setval('cancha_id_cancha_seq', (SELECT MAX(id_cancha) FROM cancha));
