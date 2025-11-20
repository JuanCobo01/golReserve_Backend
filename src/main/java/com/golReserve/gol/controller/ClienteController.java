package com.golReserve.gol.controller;

import com.golReserve.gol.dto.HorariosDisponiblesResponse;
import com.golReserve.gol.entity.*;
import com.golReserve.gol.entity.Enums.EstadoReserva;
import com.golReserve.gol.repository.CanchaRepository;
import com.golReserve.gol.repository.EstablecimientoRepository;
import com.golReserve.gol.repository.ReservaRepository;
import com.golReserve.gol.repository.UsuarioRepository;
import com.golReserve.gol.service.ReservaService;
import com.golReserve.gol.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cliente")
@PreAuthorize("hasRole('CLIENTE')")
public class ClienteController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EstablecimientoRepository establecimientoRepository;

    @Autowired
    private CanchaRepository canchaRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Ver perfil del cliente
     */
    @GetMapping("/perfil")
    public ResponseEntity<?> verPerfil() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();

            Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
            if (!usuarioOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Usuario no encontrado");
            }

            return ResponseEntity.ok(usuarioOpt.get());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener perfil: " + e.getMessage());
        }
    }

    /**
     * Actualizar perfil del cliente
     */
    @PutMapping("/perfil")
    public ResponseEntity<?> actualizarPerfil(@RequestBody Usuario usuarioActualizado) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();

            Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
            if (!usuarioOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Usuario no encontrado");
            }

            Usuario usuario = usuarioOpt.get();

            // Actualizar solo campos permitidos para cliente
            if (usuarioActualizado.getNombre() != null) {
                usuario.setNombre(usuarioActualizado.getNombre());
            }
            if (usuarioActualizado.getTelefono() != null) {
                usuario.setTelefono(usuarioActualizado.getTelefono());
            }
            if (usuarioActualizado.getPassword() != null && !usuarioActualizado.getPassword().isEmpty()) {
                usuario.setPassword(passwordEncoder.encode(usuarioActualizado.getPassword()));
            }

            Usuario guardado = usuarioRepository.save(usuario);
            return ResponseEntity.ok(guardado);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar perfil: " + e.getMessage());
        }
    }

    /**
     * Listar todos los establecimientos disponibles
     */
    @GetMapping("/establecimientos")
    public ResponseEntity<List<Establecimiento>> listarEstablecimientos() {
        List<Establecimiento> establecimientos = establecimientoRepository.findAll();
        return ResponseEntity.ok(establecimientos);
    }

    /**
     * Listar todas las canchas disponibles
     */
    @GetMapping("/canchas")
    public ResponseEntity<List<Cancha>> listarCanchas() {
        List<Cancha> canchas = canchaRepository.findAll();
        return ResponseEntity.ok(canchas);
    }

    /**
     * Ver horarios disponibles de una cancha en una fecha específica
     */
    @GetMapping("/canchas/{id}/horarios")
    public ResponseEntity<?> verHorariosDisponibles(
            @PathVariable Long id,
            @RequestParam String fecha) {
        try {
            Optional<Cancha> canchaOpt = canchaRepository.findById(id);
            if (!canchaOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Cancha no encontrada");
            }

            Cancha cancha = canchaOpt.get();
            LocalDate fechaReserva = LocalDate.parse(fecha);

            // Obtener reservas confirmadas para esa fecha
            List<Reserva> reservasExistentes = reservaRepository.findByCancha_IdAndFechaAndEstado(
                    id, fechaReserva, EstadoReserva.CONFIRMADA);

            // Generar horarios disponibles (ejemplo: de 8:00 a 22:00)
            List<HorariosDisponiblesResponse.HorarioDisponible> horarios = new ArrayList<>();
            
            for (int hora = 8; hora < 22; hora++) {
                LocalTime horaInicio = LocalTime.of(hora, 0);
                LocalTime horaFin = LocalTime.of(hora + 1, 0);
                
                // Verificar si está ocupado
                boolean disponible = reservasExistentes.stream()
                        .noneMatch(r -> 
                            (r.getHoraInicio().isBefore(horaFin) && r.getHoraFin().isAfter(horaInicio))
                        );
                
                horarios.add(new HorariosDisponiblesResponse.HorarioDisponible(
                        horaInicio, horaFin, disponible));
            }

            HorariosDisponiblesResponse response = new HorariosDisponiblesResponse();
            response.setCanchaId(id);
            response.setNombreCancha(cancha.getEstablecimiento().getNombre());
            response.setFecha(fechaReserva);
            response.setHorariosDisponibles(horarios);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al obtener horarios: " + e.getMessage());
        }
    }

    /**
     * Crear una nueva reserva
     */
    @PostMapping("/reservas")
    public ResponseEntity<?> crearReserva(@RequestBody Reserva reserva) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();

            Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
            if (!usuarioOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Usuario no encontrado");
            }

            // Asignar el usuario autenticado a la reserva
            reserva.setUsuario(usuarioOpt.get());
            
            // Establecer estado inicial
            reserva.setEstado(EstadoReserva.CONFIRMADA);

            Reserva nuevaReserva = reservaService.registrarReserva(reserva);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaReserva);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al crear reserva: " + e.getMessage());
        }
    }

    /**
     * Ver mis reservas
     */
    @GetMapping("/reservas")
    public ResponseEntity<?> verMisReservas() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();

            Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
            if (!usuarioOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Usuario no encontrado");
            }

            Long usuarioId = usuarioOpt.get().getIdUsuario();
            List<Reserva> reservas = reservaRepository.findByUsuario_IdUsuario(usuarioId);

            return ResponseEntity.ok(reservas);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener reservas: " + e.getMessage());
        }
    }

    /**
     * Ver detalle de una reserva
     */
    @GetMapping("/reservas/{id}")
    public ResponseEntity<?> verDetalleReserva(@PathVariable Long id) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();

            Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
            if (!usuarioOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Usuario no encontrado");
            }

            Optional<Reserva> reservaOpt = reservaRepository.findById(id);
            if (!reservaOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Reserva no encontrada");
            }

            Reserva reserva = reservaOpt.get();
            
            // Verificar que la reserva pertenece al usuario
            if (!reserva.getUsuario().getIdUsuario().equals(usuarioOpt.get().getIdUsuario())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("No tienes permisos para ver esta reserva");
            }

            return ResponseEntity.ok(reserva);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener reserva: " + e.getMessage());
        }
    }

    /**
     * Cancelar mi reserva
     */
    @PutMapping("/reservas/{id}/cancelar")
    public ResponseEntity<?> cancelarMiReserva(@PathVariable Long id) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();

            Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
            if (!usuarioOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Usuario no encontrado");
            }

            Optional<Reserva> reservaOpt = reservaRepository.findById(id);
            if (!reservaOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Reserva no encontrada");
            }

            Reserva reserva = reservaOpt.get();
            
            // Verificar que la reserva pertenece al usuario
            if (!reserva.getUsuario().getIdUsuario().equals(usuarioOpt.get().getIdUsuario())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("No tienes permisos para cancelar esta reserva");
            }

            // Validar que la reserva esté confirmada
            if (reserva.getEstado() != EstadoReserva.CONFIRMADA) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Solo puedes cancelar reservas confirmadas");
            }

            reserva.setEstado(EstadoReserva.CANCELADA);
            Reserva guardada = reservaRepository.save(reserva);

            return ResponseEntity.ok(guardada);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al cancelar reserva: " + e.getMessage());
        }
    }
}
