package com.golReserve.gol.controller;

import com.golReserve.gol.dto.CambiarRolRequest;
import com.golReserve.gol.dto.CrearAdministradorRequest;
import com.golReserve.gol.dto.EstadisticasResponse;
import com.golReserve.gol.entity.Enums.EstadoUsuario;
import com.golReserve.gol.entity.Enums.RolUsuario;
import com.golReserve.gol.entity.Establecimiento;
import com.golReserve.gol.entity.Usuario;
import com.golReserve.gol.repository.CanchaRepository;
import com.golReserve.gol.repository.EstablecimientoRepository;
import com.golReserve.gol.repository.ReservaRepository;
import com.golReserve.gol.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/super-admin")
@PreAuthorize("hasRole('SUPER_ADMINISTRADOR')")
public class SuperAdminController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EstablecimientoRepository establecimientoRepository;

    @Autowired
    private CanchaRepository canchaRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Listar todos los usuarios/clientes
     */
    @GetMapping("/usuarios")
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return ResponseEntity.ok(usuarios);
    }

    /**
     * Listar solo clientes
     */
    @GetMapping("/clientes")
    public ResponseEntity<List<Usuario>> listarClientes() {
        List<Usuario> clientes = usuarioRepository.findAll().stream()
                .filter(u -> u.getRolUsuario() == RolUsuario.CLIENTE)
                .collect(Collectors.toList());
        return ResponseEntity.ok(clientes);
    }

    /**
     * Listar administradores con sus establecimientos
     */
    @GetMapping("/administradores")
    public ResponseEntity<?> listarAdministradores() {
        List<Usuario> administradores = usuarioRepository.findAll().stream()
                .filter(u -> u.getRolUsuario() == RolUsuario.ADMINISTRADOR)
                .collect(Collectors.toList());

        // Enriquecer con información del establecimiento
        List<Map<String, Object>> resultado = administradores.stream().map(admin -> {
            Map<String, Object> data = new HashMap<>();
            data.put("usuario", admin);
            
            // Buscar establecimiento del administrador
            Optional<Establecimiento> establecimiento = establecimientoRepository.findByAdminId(admin.getIdUsuario());
            data.put("establecimiento", establecimiento.orElse(null));
            
            return data;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(resultado);
    }

    /**
     * Crear administrador + establecimiento
     */
    @PostMapping("/administradores")
    public ResponseEntity<?> crearAdministrador(@RequestBody CrearAdministradorRequest request) {
        try {
            // Validar que el email no exista
            if (usuarioRepository.findByEmail(request.getEmail()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("El email ya está registrado");
            }

            // Validar que la cédula no exista
            if (usuarioRepository.findByCedula(request.getCedula()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("La cédula ya está registrada");
            }

            // Crear usuario administrador
            Usuario admin = new Usuario();
            admin.setNombre(request.getNombreCompleto());
            admin.setCedula(request.getCedula());
            admin.setTelefono(request.getTelefono());
            admin.setEmail(request.getEmail());
            admin.setPassword(passwordEncoder.encode(request.getPassword()));
            admin.setRolUsuario(RolUsuario.ADMINISTRADOR);
            admin.setEstadoUsuario(EstadoUsuario.ACTIVO);

            Usuario adminGuardado = usuarioRepository.save(admin);

            // Crear establecimiento asociado
            Establecimiento establecimiento = new Establecimiento();
            establecimiento.setNombre(request.getNombreEstablecimiento());
            establecimiento.setDireccion(request.getDireccionEstablecimiento());
            establecimiento.setAdministrador(adminGuardado);

            Establecimiento establecimientoGuardado = establecimientoRepository.save(establecimiento);

            // Respuesta
            Map<String, Object> response = new HashMap<>();
            response.put("usuario", adminGuardado);
            response.put("establecimiento", establecimientoGuardado);
            response.put("message", "Administrador y establecimiento creados exitosamente");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear administrador: " + e.getMessage());
        }
    }

    /**
     * Editar administrador
     */
    @PutMapping("/administradores/{id}")
    public ResponseEntity<?> editarAdministrador(@PathVariable Long id, @RequestBody Usuario usuarioActualizado) {
        try {
            Usuario admin = usuarioRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Administrador no encontrado"));

            if (admin.getRolUsuario() != RolUsuario.ADMINISTRADOR) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("El usuario no es un administrador");
            }

            // Actualizar campos permitidos
            if (usuarioActualizado.getNombre() != null) {
                admin.setNombre(usuarioActualizado.getNombre());
            }
            if (usuarioActualizado.getTelefono() != null) {
                admin.setTelefono(usuarioActualizado.getTelefono());
            }
            if (usuarioActualizado.getPassword() != null && !usuarioActualizado.getPassword().isEmpty()) {
                admin.setPassword(passwordEncoder.encode(usuarioActualizado.getPassword()));
            }

            Usuario guardado = usuarioRepository.save(admin);
            return ResponseEntity.ok(guardado);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    /**
     * Eliminar administrador (y su establecimiento en cascada)
     */
    @DeleteMapping("/administradores/{id}")
    public ResponseEntity<?> eliminarAdministrador(@PathVariable Long id) {
        try {
            Usuario admin = usuarioRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Administrador no encontrado"));

            if (admin.getRolUsuario() != RolUsuario.ADMINISTRADOR) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("El usuario no es un administrador");
            }

            usuarioRepository.delete(admin);
            return ResponseEntity.ok("Administrador eliminado correctamente");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    /**
     * Activar/Desactivar administrador
     */
    @PutMapping("/administradores/{id}/estado")
    public ResponseEntity<?> cambiarEstadoAdministrador(@PathVariable Long id, @RequestParam EstadoUsuario estado) {
        try {
            Usuario admin = usuarioRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Administrador no encontrado"));

            admin.setEstadoUsuario(estado);
            Usuario guardado = usuarioRepository.save(admin);

            return ResponseEntity.ok(guardado);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    /**
     * Cambiar rol de usuario
     */
    @PutMapping("/usuarios/{id}/rol")
    public ResponseEntity<?> cambiarRolUsuario(@PathVariable Long id, @RequestBody CambiarRolRequest request) {
        try {
            Usuario usuario = usuarioRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            RolUsuario rolAnterior = usuario.getRolUsuario();
            usuario.setRolUsuario(request.getNuevoRol());
            Usuario guardado = usuarioRepository.save(usuario);

            Map<String, Object> response = new HashMap<>();
            response.put("usuario", guardado);
            response.put("rolAnterior", rolAnterior);
            response.put("rolNuevo", request.getNuevoRol());
            response.put("message", "Rol actualizado correctamente");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    /**
     * Obtener estadísticas globales del sistema
     */
    @GetMapping("/estadisticas")
    public ResponseEntity<EstadisticasResponse> obtenerEstadisticas() {
        EstadisticasResponse estadisticas = new EstadisticasResponse();

        // Contar usuarios por rol
        List<Usuario> todosUsuarios = usuarioRepository.findAll();
        estadisticas.setTotalUsuarios((long) todosUsuarios.size());
        estadisticas.setTotalClientes(todosUsuarios.stream()
                .filter(u -> u.getRolUsuario() == RolUsuario.CLIENTE).count());
        estadisticas.setTotalAdministradores(todosUsuarios.stream()
                .filter(u -> u.getRolUsuario() == RolUsuario.ADMINISTRADOR).count());

        // Contar establecimientos y canchas
        estadisticas.setTotalEstablecimientos(establecimientoRepository.count());
        estadisticas.setTotalCanchas(canchaRepository.count());

        // Contar reservas por estado
        estadisticas.setTotalReservasActivas(reservaRepository.countByEstado(
                com.golReserve.gol.entity.Enums.EstadoReserva.CONFIRMADA));
        estadisticas.setTotalReservasCanceladas(reservaRepository.countByEstado(
                com.golReserve.gol.entity.Enums.EstadoReserva.CANCELADA));

        return ResponseEntity.ok(estadisticas);
    }
}
