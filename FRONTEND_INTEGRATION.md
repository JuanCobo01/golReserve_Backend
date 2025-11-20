# 🎯 Guía de Integración Frontend - GolReserve Backend

## 📋 Información General

**Base URL:** `http://localhost:9090/api`  
**Autenticación:** JWT Bearer Token  
**Formato:** JSON

---

## 🔐 Autenticación

### 1. Registro de Cliente (Público)

```javascript
POST /usuarios/registrar

Body:
{
  "nombre": "Juan Pérez",
  "cedula": "1234567890",
  "telefono": "3001234567",
  "email": "juan@example.com",
  "password": "MiPassword123"
}

Response 201:
{
  "idUsuario": 1,
  "nombre": "Juan Pérez",
  "email": "juan@example.com",
  "rolUsuario": "CLIENTE",
  "estadoUsuario": "ACTIVO"
}
```

**IMPORTANTE:** Solo se puede registrar con rol CLIENTE. Los roles ADMINISTRADOR y SUPER_ADMINISTRADOR son asignados por el Super Admin.

### 2. Login (Todos los roles)

```javascript
POST /usuarios/login

Body:
{
  "email": "juan@example.com",
  "password": "MiPassword123"
}

Response 200:
{
  "idUsuario": 1,
  "nombre": "Juan Pérez",
  "email": "juan@example.com",
  "rolUsuario": "CLIENTE",
  "message": "Login exitoso",
  "token": "eyJhbGciOiJIUzUxMiJ9..."
}
```

### 3. Uso del Token

Incluir en todas las peticiones autenticadas:

```javascript
headers: {
  'Authorization': 'Bearer ' + token,
  'Content-Type': 'application/json'
}
```

---

## 👥 Roles y Redirección

### Después del Login - Redirigir según rol:

```javascript
const redirectByRole = (usuario) => {
  switch(usuario.rolUsuario) {
    case 'SUPER_ADMINISTRADOR':
      navigate('/super-admin/dashboard');
      break;
    case 'ADMINISTRADOR':
      navigate('/admin/dashboard');
      break;
    case 'CLIENTE':
    default:
      navigate('/cliente/home');
      break;
  }
};
```

---

## 🌟 SUPER ADMINISTRADOR

### Estadísticas Globales
```javascript
GET /super-admin/estadisticas

Response:
{
  "totalUsuarios": 45,
  "totalClientes": 38,
  "totalAdministradores": 6,
  "totalEstablecimientos": 6,
  "totalCanchas": 18,
  "totalReservasActivas": 25,
  "totalReservasPasadas": 150,
  "totalReservasCanceladas": 10
}
```

### Listar Usuarios
```javascript
GET /super-admin/usuarios          // Todos
GET /super-admin/clientes          // Solo clientes
GET /super-admin/administradores   // Admins con establecimientos
```

### Crear Administrador + Establecimiento
```javascript
POST /super-admin/administradores

Body:
{
  "nombreCompleto": "Carlos Admin",
  "cedula": "9876543210",
  "telefono": "3109876543",
  "email": "carlos@admin.com",
  "password": "AdminPass123",
  "nombreEstablecimiento": "Canchas El Campeón",
  "direccionEstablecimiento": "Calle 50 #20-30"
}

Response 201:
{
  "usuario": { /* datos usuario */ },
  "establecimiento": { /* datos establecimiento */ },
  "message": "Administrador y establecimiento creados exitosamente"
}
```

### Cambiar Rol de Usuario
```javascript
PUT /super-admin/usuarios/{id}/rol

Body:
{
  "nuevoRol": "ADMINISTRADOR"  // o "CLIENTE"
}
```

### Activar/Desactivar Administrador
```javascript
PUT /super-admin/administradores/{id}/estado?estado=ACTIVO
PUT /super-admin/administradores/{id}/estado?estado=INACTIVO
```

---

## 🏢 ADMINISTRADOR

### Dashboard
```javascript
GET /admin/dashboard

Response:
{
  "establecimiento": {
    "idEstablecimiento": 1,
    "nombre": "Canchas El Campeón",
    "direccion": "Calle 50 #20-30"
  },
  "totalCanchas": 3,
  "canchasActivas": 2,
  "canchasMantenimiento": 1,
  "reservasActivas": 15,
  "reservasHoy": 5,
  "reservasProximaSemana": 12
}
```

### Gestión de Establecimiento
```javascript
// Ver mi establecimiento
GET /admin/establecimiento

// Actualizar mi establecimiento
PUT /admin/establecimiento
Body: {
  "nombre": "Nuevo Nombre",
  "direccion": "Nueva Dirección"
}
```

### Gestión de Canchas
```javascript
// Listar mis canchas
GET /admin/canchas

// Cambiar estado de cancha
PUT /admin/canchas/{id}/estado?estado=ACTIVA
PUT /admin/canchas/{id}/estado?estado=INACTIVA
```

### Gestión de Reservas
```javascript
// Ver todas las reservas de mi establecimiento
GET /admin/reservas

// Filtrar por estado
GET /admin/reservas?estado=CONFIRMADA
GET /admin/reservas?estado=CANCELADA

// Ver detalle de reserva
GET /admin/reservas/{id}

// Cancelar reserva
PUT /admin/reservas/{id}/cancelar
```

---

## 👤 CLIENTE

### Perfil
```javascript
// Ver mi perfil
GET /cliente/perfil

// Actualizar mi perfil
PUT /cliente/perfil
Body: {
  "nombre": "Nuevo Nombre",
  "telefono": "3001234567"
}
```

### Explorar Establecimientos y Canchas
```javascript
// Ver todos los establecimientos
GET /cliente/establecimientos

// Ver todas las canchas
GET /cliente/canchas
```

### Horarios Disponibles
```javascript
GET /cliente/canchas/{id}/horarios?fecha=2025-11-25

Response:
{
  "canchaId": 1,
  "nombreCancha": "Canchas El Campeón",
  "fecha": "2025-11-25",
  "horariosDisponibles": [
    {
      "horaInicio": "08:00:00",
      "horaFin": "09:00:00",
      "disponible": true
    },
    {
      "horaInicio": "09:00:00",
      "horaFin": "10:00:00",
      "disponible": false
    },
    // ... más horarios
  ]
}
```

### Mis Reservas
```javascript
// Ver mis reservas
GET /cliente/reservas

// Ver detalle de una reserva
GET /cliente/reservas/{id}

// Crear nueva reserva
POST /cliente/reservas
Body: {
  "cancha": { "id": 1 },
  "fecha": "2025-11-25",
  "horaInicio": "14:00:00",
  "horaFin": "15:00:00"
}

// Cancelar mi reserva
PUT /cliente/reservas/{id}/cancelar
```

---

## 🔒 Protección de Rutas en React

### Hook personalizado para verificar rol

```javascript
// hooks/useAuth.js
import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

export const useAuth = () => {
  const [usuario, setUsuario] = useState(null);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem('token');
    const usuarioGuardado = localStorage.getItem('usuario');
    
    if (!token || !usuarioGuardado) {
      navigate('/login');
      setLoading(false);
      return;
    }

    setUsuario(JSON.parse(usuarioGuardado));
    setLoading(false);
  }, [navigate]);

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('usuario');
    navigate('/login');
  };

  return { usuario, loading, logout };
};

// Uso en componente
const DashboardCliente = () => {
  const { usuario, loading } = useAuth();

  if (loading) return <div>Cargando...</div>;
  if (!usuario || usuario.rolUsuario !== 'CLIENTE') {
    return <div>No autorizado</div>;
  }

  return <div>Dashboard Cliente</div>;
};
```

### Componente ProtectedRoute

```javascript
// components/ProtectedRoute.jsx
import { Navigate } from 'react-router-dom';

const ProtectedRoute = ({ children, rolesPermitidos }) => {
  const token = localStorage.getItem('token');
  const usuario = JSON.parse(localStorage.getItem('usuario') || '{}');

  if (!token) {
    return <Navigate to="/login" />;
  }

  if (rolesPermitidos && !rolesPermitidos.includes(usuario.rolUsuario)) {
    return <Navigate to="/no-autorizado" />;
  }

  return children;
};

// Uso en App.jsx
<Routes>
  <Route path="/login" element={<Login />} />
  <Route path="/registro" element={<Registro />} />
  
  <Route path="/super-admin/*" element={
    <ProtectedRoute rolesPermitidos={['SUPER_ADMINISTRADOR']}>
      <SuperAdminLayout />
    </ProtectedRoute>
  } />
  
  <Route path="/admin/*" element={
    <ProtectedRoute rolesPermitidos={['ADMINISTRADOR']}>
      <AdminLayout />
    </ProtectedRoute>
  } />
  
  <Route path="/cliente/*" element={
    <ProtectedRoute rolesPermitidos={['CLIENTE']}>
      <ClienteLayout />
    </ProtectedRoute>
  } />
</Routes>
```

---

## 📦 Servicio de API (Axios)

```javascript
// services/api.js
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:9090/api',
  headers: {
    'Content-Type': 'application/json'
  }
});

// Interceptor para agregar token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Interceptor para manejar errores
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('usuario');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;
```

### Ejemplo de uso en componente

```javascript
// pages/ClienteReservas.jsx
import { useState, useEffect } from 'react';
import api from '../services/api';

const ClienteReservas = () => {
  const [reservas, setReservas] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    cargarReservas();
  }, []);

  const cargarReservas = async () => {
    try {
      const response = await api.get('/cliente/reservas');
      setReservas(response.data);
    } catch (error) {
      console.error('Error al cargar reservas:', error);
    } finally {
      setLoading(false);
    }
  };

  const cancelarReserva = async (id) => {
    try {
      await api.put(`/cliente/reservas/${id}/cancelar`);
      cargarReservas(); // Recargar lista
      alert('Reserva cancelada exitosamente');
    } catch (error) {
      alert('Error al cancelar: ' + error.response?.data);
    }
  };

  if (loading) return <div>Cargando...</div>;

  return (
    <div>
      <h2>Mis Reservas</h2>
      {reservas.map(reserva => (
        <div key={reserva.idReserva}>
          <p>{reserva.cancha.establecimiento.nombre}</p>
          <p>{reserva.fecha} - {reserva.horaInicio}</p>
          <button onClick={() => cancelarReserva(reserva.idReserva)}>
            Cancelar
          </button>
        </div>
      ))}
    </div>
  );
};
```

---

## ⚠️ Manejo de Errores

### Errores Comunes

| Código | Significado | Acción |
|--------|-------------|--------|
| 400 | Datos inválidos | Mostrar mensaje de error al usuario |
| 401 | No autenticado | Redirigir a login |
| 403 | Sin permisos | Mostrar "No autorizado" |
| 404 | No encontrado | Mostrar "Recurso no encontrado" |
| 500 | Error del servidor | Mostrar "Error interno, intente más tarde" |

```javascript
const handleError = (error) => {
  if (error.response) {
    switch (error.response.status) {
      case 400:
        return error.response.data; // Mensaje específico del backend
      case 401:
        localStorage.clear();
        window.location.href = '/login';
        return 'Sesión expirada';
      case 403:
        return 'No tienes permisos para esta acción';
      case 404:
        return 'Recurso no encontrado';
      default:
        return 'Error del servidor';
    }
  }
  return 'Error de conexión';
};
```

---

## 📝 Validaciones en Frontend

### Registro
- Nombre: mínimo 3 caracteres
- Cédula: 6-15 dígitos, única
- Teléfono: mínimo 10 dígitos
- Email: formato válido, único
- Password: mínimo 8 caracteres, al menos 1 letra y 1 número

### Crear Reserva
- Fecha: no puede ser anterior a hoy
- Hora: debe estar en horarios disponibles
- Validar disponibilidad antes de confirmar

---

## 🚀 Flujo Completo de Ejemplo

### Cliente hace una reserva:

```javascript
// 1. Ver establecimientos
const establecimientos = await api.get('/cliente/establecimientos');

// 2. Ver canchas de un establecimiento
const canchas = await api.get('/cliente/canchas');
const canchasFiltradas = canchas.data.filter(
  c => c.establecimiento.id === establecimientoSeleccionado
);

// 3. Ver horarios disponibles
const horarios = await api.get(
  `/cliente/canchas/${canchaId}/horarios?fecha=2025-11-25`
);

// 4. Crear reserva
const reserva = await api.post('/cliente/reservas', {
  cancha: { id: canchaId },
  fecha: '2025-11-25',
  horaInicio: '14:00:00',
  horaFin: '15:00:00'
});

// 5. Confirmar con mensaje
alert('¡Reserva creada exitosamente!');
```

---

## 🔧 Variables de Entorno

```env
# .env.local (React)
REACT_APP_API_URL=http://localhost:9090/api
REACT_APP_JWT_EXPIRY=7d
```

---

## ✅ Checklist de Implementación

- [ ] Sistema de login y registro
- [ ] Almacenamiento de token y usuario
- [ ] Redirección según rol después de login
- [ ] Rutas protegidas por rol
- [ ] Interceptor de Axios para token
- [ ] Manejo de errores 401/403
- [ ] Logout y limpieza de localStorage
- [ ] Vista de perfil para cada rol
- [ ] Dashboard según rol
- [ ] Gestión de reservas
- [ ] Validaciones de formularios

---

**Última actualización:** Noviembre 2025  
**Backend:** Spring Boot 3.5.6 + JWT + ChaCha20-Poly1305  
**Puerto:** 9090
