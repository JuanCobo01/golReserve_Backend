-- Usuarios
INSERT INTO usuarios (usuario_id, cc_usuario, nombre_usuario, email_usuario, password_usuario, cel_usuario, estado, token_verificacion, rol_usuario)
VALUES (1, 1234567890, 'Juan Pérez', 'juanperez@example.com', 'miPasswordSeguro123', '3109876543', 'ACTIVO', NULL, 'ADMINISTRADOR');
INSERT INTO usuarios (usuario_id, cc_usuario, nombre_usuario, email_usuario, password_usuario, cel_usuario, estado, token_verificacion, rol_usuario)
VALUES (2, 9876543210, 'Ana Gómez', 'anagomez@example.com', 'PasswordAna2025', '3201234567', 'ACTIVO', NULL, 'CLIENTE');
INSERT INTO usuarios (usuario_id, cc_usuario, nombre_usuario, email_usuario, password_usuario, cel_usuario, estado, token_verificacion, rol_usuario)
VALUES (3, 1122334455, 'Carlos Ruiz', 'carlosruiz@example.com', 'PasswordCarlos2025', '3111234567', 'ACTIVO', NULL, 'CLIENTE');
INSERT INTO usuarios (usuario_id, cc_usuario, nombre_usuario, email_usuario, password_usuario, cel_usuario, estado, token_verificacion, rol_usuario)
VALUES (4, 2233445566, 'Laura Torres', 'lauratorres@example.com', 'PasswordLaura2025', '3121234567', 'ACTIVO', NULL, 'ADMINISTRADOR');
INSERT INTO usuarios (usuario_id, cc_usuario, nombre_usuario, email_usuario, password_usuario, cel_usuario, estado, token_verificacion, rol_usuario)
VALUES (5, 3344556677, 'Miguel Salazar', 'miguelsalazar@example.com', 'PasswordMiguel2025', '3131234567', 'ACTIVO', NULL, 'CLIENTE');

-- Establecimientos
INSERT INTO establecimiento (id_establecimiento, nombre_cancha, direccion_establecimiento, admin_id)
VALUES (1, 'Estadio Central', 'Calle 123 #45-67', 1);
INSERT INTO establecimiento (id_establecimiento, nombre_cancha, direccion_establecimiento, admin_id)
VALUES (2, 'Polideportivo Norte', 'Avenida 10 #20-30', 4);
INSERT INTO establecimiento (id_establecimiento, nombre_cancha, direccion_establecimiento, admin_id)
VALUES (3, 'Complejo Sur', 'Carrera 15 #50-60', 1);
INSERT INTO establecimiento (id_establecimiento, nombre_cancha, direccion_establecimiento, admin_id)
VALUES (4, 'Club Deportivo', 'Calle 200 #10-20', 4);
INSERT INTO establecimiento (id_establecimiento, nombre_cancha, direccion_establecimiento, admin_id)
VALUES (5, 'Centro Futbol', 'Diagonal 30 #100-200', 1);

-- Canchas
INSERT INTO cancha (id_cancha, tipo_cancha, precio_hora, estado_cancha, establecimiento_id)
VALUES (1, 'FUTBOL_5', 50000, 'ACTIVA', 1);
INSERT INTO cancha (id_cancha, tipo_cancha, precio_hora, estado_cancha, establecimiento_id)
VALUES (2, 'FUTBOL_7', 30000, 'ACTIVA', 2);
INSERT INTO cancha (id_cancha, tipo_cancha, precio_hora, estado_cancha, establecimiento_id)
VALUES (3, 'FUTBOL_5', 40000, 'ACTIVA', 3);
INSERT INTO cancha (id_cancha, tipo_cancha, precio_hora, estado_cancha, establecimiento_id)
VALUES (4, 'FUTBOL_7', 60000, 'ACTIVA', 4);
INSERT INTO cancha (id_cancha, tipo_cancha, precio_hora, estado_cancha, establecimiento_id)
VALUES (5, 'FUTBOL_5', 25000, 'ACTIVA', 5);

-- Reservas
INSERT INTO reserva (reserva_id, usuario_id, cancha_id, fecha, hora_inicio, hora_fin, estado)
VALUES (1, 2, 1, '2025-10-10', '15:00:00', '16:00:00', 'CONFIRMADA');
INSERT INTO reserva (reserva_id, usuario_id, cancha_id, fecha, hora_inicio, hora_fin, estado)
VALUES (2, 3, 2, '2025-10-11', '10:00:00', '11:00:00', 'CONFIRMADA');
INSERT INTO reserva (reserva_id, usuario_id, cancha_id, fecha, hora_inicio, hora_fin, estado)
VALUES (3, 5, 3, '2025-10-12', '12:00:00', '13:00:00', 'CONFIRMADA');
INSERT INTO reserva (reserva_id, usuario_id, cancha_id, fecha, hora_inicio, hora_fin, estado)
VALUES (4, 2, 4, '2025-10-13', '14:00:00', '15:00:00', 'CONFIRMADA');
INSERT INTO reserva (reserva_id, usuario_id, cancha_id, fecha, hora_inicio, hora_fin, estado)
VALUES (5, 3, 5, '2025-10-14', '16:00:00', '17:00:00', 'CONFIRMADA');
