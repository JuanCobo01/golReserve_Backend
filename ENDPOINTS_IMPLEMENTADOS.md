# Endpoints Implementados - Sprint 1

## ✅ TAREAS COMPLETADAS

### 1. ✅ Validación de datos (correo, contraseña, duplicados)

**Implementado en:** `UsuarioServiceImpl.java`

**Validaciones agregadas:**
- ✅ Formato de email válido (expresión regular)
- ✅ Email único (no duplicados)
- ✅ Cédula única (no duplicados)
- ✅ Contraseña fuerte (mínimo 8 caracteres, letras y números)
- ✅ Nombre obligatorio y no vacío
- ✅ Teléfono con mínimo 10 dígitos

**Errores que devuelve:**
- "El formato del correo electrónico no es válido"
- "El correo electrónico ya está registrado"
- "La cédula ya está registrada"
- "La contraseña debe tener al menos 8 caracteres, incluyendo letras y números"
- "El nombre es obligatorio"
- "El teléfono debe tener al menos 10 dígitos"

---

### 2. ✅ API para inicio de sesión y autenticación

**Endpoint:** `POST /api/usuarios/login`

**Ya estaba implementado previamente con JWT**

---

### 3. ✅ Endpoint /api/canchas/buscar con filtros

**Endpoint:** `GET /api/cancha/buscar`

**Parámetros opcionales (query params):**
- `tipo` - Tipo de cancha (FUTBOL_5, FUTBOL_7, FUTBOL_11, etc.)
- `precioMin` - Precio mínimo por hora
- `precioMax` - Precio máximo por hora
- `estado` - Estado de la cancha (DISPONIBLE, MANTENIMIENTO, INACTIVA)
- `establecimientoId` - ID del establecimiento

**Ejemplos de uso:**
```
GET /api/cancha/buscar
GET /api/cancha/buscar?tipo=FUTBOL_5
GET /api/cancha/buscar?precioMin=50000&precioMax=100000
GET /api/cancha/buscar?tipo=FUTBOL_5&precioMin=50000&precioMax=80000&estado=DISPONIBLE
GET /api/cancha/buscar?establecimientoId=1
GET /api/cancha/buscar?tipo=FUTBOL_7&establecimientoId=2&estado=DISPONIBLE
```

**Roles permitidos:** CLIENTE, ADMINISTRADOR, SUPER_ADMINISTRADOR

**Respuesta:** Lista de canchas que coincidan con los filtros

---

### 4. ✅ Campo de precio ya existía en el modelo

**Verificado:** El campo `precioHora` (Double) ya estaba en la entidad Cancha

---

### 5. ✅ Endpoint /api/reservas/confirmar

**Endpoint:** `POST /api/reserva/confirmar/{id}`

**Descripción:** Confirma una reserva que está en estado PENDIENTE

**Validaciones:**
- Solo confirma reservas en estado PENDIENTE
- La fecha de la reserva no puede ser pasada
- Cambia el estado a CONFIRMADA

**Roles permitidos:** ADMINISTRADOR, SUPER_ADMINISTRADOR

**Ejemplo:**
```
POST /api/reserva/confirmar/1
```

**Respuesta exitosa:**
```json
{
  "id": 1,
  "fecha": "2025-11-10",
  "horaInicio": "14:00",
  "horaFin": "16:00",
  "estado": "CONFIRMADA",
  ...
}
```

**Errores posibles:**
- "Reserva con ID X no encontrada"
- "Solo se pueden confirmar reservas en estado PENDIENTE"
- "No se puede confirmar una reserva con fecha pasada"

---

### 6. ✅ Endpoint /api/reservas/cancelar

**Endpoint:** `POST /api/reserva/cancelar/{id}?motivo=texto_opcional`

**Descripción:** Cancela una reserva activa

**Validaciones:**
- No se puede cancelar una reserva ya cancelada
- No se puede cancelar una reserva completada
- Cambia el estado a CANCELADA

**Roles permitidos:** CLIENTE, ADMINISTRADOR, SUPER_ADMINISTRADOR

**Ejemplo:**
```
POST /api/reserva/cancelar/1?motivo=No podré asistir
```

**Respuesta exitosa:**
```json
{
  "mensaje": "Reserva cancelada exitosamente",
  "reserva": {
    "id": 1,
    "estado": "CANCELADA",
    ...
  },
  "motivo": "No podré asistir"
}
```

**Errores posibles:**
- "La reserva ya está cancelada"
- "No se puede cancelar una reserva completada"

---

### 7. ✅ Endpoint /api/reservas/modificar

**Endpoint:** `PUT /api/reserva/modificar/{id}`

**Descripción:** Modifica fecha y horarios de una reserva

**Validaciones:**
- No se puede modificar una reserva cancelada o completada
- La nueva fecha no puede ser pasada
- Hora fin debe ser posterior a hora inicio
- Cambia el estado a PENDIENTE después de modificar

**Roles permitidos:** CLIENTE, ADMINISTRADOR, SUPER_ADMINISTRADOR

**Body (campos opcionales):**
```json
{
  "fecha": "2025-11-15",
  "horaInicio": "15:00",
  "horaFin": "17:00"
}
```

**Respuesta exitosa:**
```json
{
  "mensaje": "Reserva modificada exitosamente. Estado cambiado a PENDIENTE para confirmación",
  "reserva": {
    "id": 1,
    "fecha": "2025-11-15",
    "horaInicio": "15:00",
    "horaFin": "17:00",
    "estado": "PENDIENTE",
    ...
  }
}
```

**Errores posibles:**
- "No se puede modificar una reserva cancelada o completada"
- "No se puede modificar a una fecha pasada"
- "La hora de fin debe ser posterior a la hora de inicio"

---

### 8. ✅ Endpoint /api/reservas/compartir

**Endpoint:** `GET /api/reserva/compartir/{id}`

**Descripción:** Genera un mensaje de texto formateado para compartir la reserva (por WhatsApp, email, etc.)

**Roles permitidos:** CLIENTE, ADMINISTRADOR, SUPER_ADMINISTRADOR

**Ejemplo:**
```
GET /api/reserva/compartir/1
```

**Respuesta exitosa:**
```json
{
  "mensaje": "¡Reserva de cancha confirmada!\n\n📅 Fecha: 2025-11-10\n⏰ Horario: 14:00 - 16:00\n🏟️ Cancha: FUTBOL_5\n📍 Establecimiento: Centro Deportivo Los Pinos\n💰 Precio: $60000.0 por hora\n📊 Estado: CONFIRMADA\n\nCódigo de reserva: #1",
  "tipo": "texto"
}
```

---

## 📊 RESUMEN DE IMPLEMENTACIÓN

### Tareas completadas: 7/7 (100%) ✅

1. ✅ Validación de datos (correo, contraseña, duplicados)
2. ✅ API para inicio de sesión y autenticación
3. ✅ Endpoint /api/canchas/buscar con filtros
4. ✅ Campo de precio en modelo de canchas (ya existía)
5. ✅ Endpoint /api/reservas/confirmar
6. ✅ Endpoint /api/reservas/cancelar
7. ✅ Endpoint /api/reservas/modificar
8. ✅ Endpoint /api/reservas/compartir

---

## 🔐 CONTROL DE ACCESO POR ROL

### SUPER_ADMINISTRADOR
- Acceso total a todos los endpoints
- Gestión de usuarios

### ADMINISTRADOR
- Gestión de sus propios establecimientos
- Gestión de canchas de sus establecimientos
- Confirmar reservas
- Ver todas las reservas de sus establecimientos

### CLIENTE
- Consultar establecimientos y canchas
- Crear reservas
- Ver sus propias reservas
- Modificar sus reservas
- Cancelar sus reservas
- Compartir sus reservas

---

## 🧪 PRUEBAS RECOMENDADAS

### 1. Probar validaciones de registro:
```bash
POST /api/usuarios/registrar
{
  "email": "invalido",  # Debe fallar
  "password": "123",    # Debe fallar (muy corta)
  ...
}
```

### 2. Probar búsqueda de canchas:
```bash
GET /api/cancha/buscar?tipo=FUTBOL_5&precioMin=40000&precioMax=80000
```

### 3. Probar flujo completo de reserva:
```bash
# 1. Crear reserva (estado PENDIENTE)
POST /api/reserva/registrar

# 2. Confirmar reserva (ADMIN)
POST /api/reserva/confirmar/1

# 3. Modificar reserva (vuelve a PENDIENTE)
PUT /api/reserva/modificar/1

# 4. Compartir reserva
GET /api/reserva/compartir/1

# 5. Cancelar si es necesario
POST /api/reserva/cancelar/1?motivo=Cambio de planes
```

---

## 📝 NOTAS IMPORTANTES

1. **Autenticación requerida:** Todos los endpoints (excepto login y registro) requieren el header:
   ```
   Authorization: Bearer {token-jwt}
   ```

2. **CORS configurado:** El backend acepta peticiones desde `http://localhost:5173`

3. **Validaciones implementadas:** Todas las operaciones tienen validaciones de lógica de negocio

4. **Estados de reserva:**
   - PENDIENTE: Recién creada o modificada
   - CONFIRMADA: Aprobada por administrador
   - CANCELADA: Cancelada por cliente o admin
   - COMPLETADA: Reserva finalizada

5. **Formato de fechas:** LocalDate (YYYY-MM-DD)

6. **Formato de horas:** LocalTime (HH:mm)

