package com.golReserve.gol.controller;

import com.golReserve.gol.dto.DashboardAdminResponse;
import com.golReserve.gol.entity.Cancha;
import com.golReserve.gol.entity.Enums.EstadoCancha;
import com.golReserve.gol.entity.Enums.EstadoReserva;
import com.golReserve.gol.entity.Establecimiento;
import com.golReserve.gol.entity.Reserva;
import com.golReserve.gol.repository.CanchaRepository;
import com.golReserve.gol.repository.EstablecimientoRepository;
import com.golReserve.gol.repository.ReservaRepository;
import com.golReserve.gol.service.EstablecimientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class AdminController {

    @Autowired
    private EstablecimientoRepository establecimientoRepository;

    @Autowired
    private CanchaRepository canchaRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private EstablecimientoService establecimientoService;

    /**
     * Obtener información del dashboard del administrador
     */
    @GetMapping("/dashboard")
    public ResponseEntity<?> obtenerDashboard() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();

            // Buscar establecimientos del administrador
            List<Establecimiento> establecimientos = establecimientoService.buscarEstablecimientosPorAdministrador(email);
            
            if (establecimientos.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No se encontró establecimiento asociado");
            }

            Establecimiento establecimiento = establecimientos.get(0);
            Long establecimientoId = establecimiento.getId();

            // Obtener estadísticas
            DashboardAdminResponse dashboard = new DashboardAdminResponse();
            dashboard.setEstablecimiento(establecimiento);

            // Contar canchas
            List<Cancha> canchas = canchaRepository.findByEstablecimiento_Id(establecimientoId);
            dashboard.setTotalCanchas((long) canchas.size());
            dashboard.setCanchasActivas(canchas.stream()
                    .filter(c -> c.getEstadoCancha() == EstadoCancha.ACTIVA).count());
            dashboard.setCanchasMantenimiento(canchas.stream()
                    .filter(c -> c.getEstadoCancha() == EstadoCancha.INACTIVA).count());

            // Contar reservas
            dashboard.setReservasActivas(
                    reservaRepository.countByEstablecimientoIdAndEstado(establecimientoId, EstadoReserva.CONFIRMADA));
            
            dashboard.setReservasHoy(
                    reservaRepository.findByEstablecimientoIdAndFecha(establecimientoId, LocalDate.now()).stream()
                            .filter(r -> r.getEstado() == EstadoReserva.CONFIRMADA).count());

            dashboard.setReservasProximaSemana(
                    reservaRepository.findByEstablecimientoId(establecimientoId).stream()
                            .filter(r -> r.getFecha().isAfter(LocalDate.now()) && 
                                       r.getFecha().isBefore(LocalDate.now().plusDays(8)) &&
                                       r.getEstado() == EstadoReserva.CONFIRMADA)
                            .count());

            return ResponseEntity.ok(dashboard);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener dashboard: " + e.getMessage());
        }
    }

    /**
     * Obtener información del establecimiento
     */
    @GetMapping("/establecimiento")
    public ResponseEntity<?> obtenerEstablecimiento() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();

            List<Establecimiento> establecimientos = establecimientoService.buscarEstablecimientosPorAdministrador(email);
            
            if (establecimientos.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No se encontró establecimiento asociado");
            }

            return ResponseEntity.ok(establecimientos.get(0));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    /**
     * Actualizar información del establecimiento
     */
    @PutMapping("/establecimiento")
    public ResponseEntity<?> actualizarEstablecimiento(@RequestBody Establecimiento establecimientoActualizado) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();

            List<Establecimiento> establecimientos = establecimientoService.buscarEstablecimientosPorAdministrador(email);
            
            if (establecimientos.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No se encontró establecimiento asociado");
            }

            Establecimiento establecimiento = establecimientos.get(0);
            Long id = establecimiento.getId();

            Establecimiento actualizado = establecimientoService.actualizarEstablecimiento(id, establecimientoActualizado);
            return ResponseEntity.ok(actualizado);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar establecimiento: " + e.getMessage());
        }
    }

    /**
     * Listar canchas del establecimiento
     */
    @GetMapping("/canchas")
    public ResponseEntity<?> listarCanchas() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();

            List<Establecimiento> establecimientos = establecimientoService.buscarEstablecimientosPorAdministrador(email);
            
            if (establecimientos.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No se encontró establecimiento asociado");
            }

            Long establecimientoId = establecimientos.get(0).getId();
            List<Cancha> canchas = canchaRepository.findByEstablecimiento_Id(establecimientoId);

            return ResponseEntity.ok(canchas);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al listar canchas: " + e.getMessage());
        }
    }

    /**
     * Cambiar estado de una cancha
     */
    @PutMapping("/canchas/{id}/estado")
    public ResponseEntity<?> cambiarEstadoCancha(@PathVariable Long id, @RequestParam EstadoCancha estado) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();

            // Verificar que la cancha pertenece al administrador
            Optional<Cancha> canchaOpt = canchaRepository.findById(id);
            if (!canchaOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Cancha no encontrada");
            }

            Cancha cancha = canchaOpt.get();
            
            // Validar que es su establecimiento
            if (!establecimientoService.esAdministradorDelEstablecimiento(
                    cancha.getEstablecimiento().getId(), email)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("No tienes permisos para modificar esta cancha");
            }

            cancha.setEstadoCancha(estado);
            Cancha guardada = canchaRepository.save(cancha);

            return ResponseEntity.ok(guardada);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al cambiar estado: " + e.getMessage());
        }
    }

    /**
     * Listar todas las reservas del establecimiento
     */
    @GetMapping("/reservas")
    public ResponseEntity<?> listarReservas(@RequestParam(required = false) EstadoReserva estado) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();

            List<Establecimiento> establecimientos = establecimientoService.buscarEstablecimientosPorAdministrador(email);
            
            if (establecimientos.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No se encontró establecimiento asociado");
            }

            Long establecimientoId = establecimientos.get(0).getId();
            
            List<Reserva> reservas;
            if (estado != null) {
                reservas = reservaRepository.findByEstablecimientoIdAndEstado(establecimientoId, estado);
            } else {
                reservas = reservaRepository.findByEstablecimientoId(establecimientoId);
            }

            return ResponseEntity.ok(reservas);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al listar reservas: " + e.getMessage());
        }
    }

    /**
     * Cancelar una reserva (admin puede cancelar cualquier reserva de su establecimiento)
     */
    @PutMapping("/reservas/{id}/cancelar")
    public ResponseEntity<?> cancelarReserva(@PathVariable Long id) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();

            Optional<Reserva> reservaOpt = reservaRepository.findById(id);
            if (!reservaOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Reserva no encontrada");
            }

            Reserva reserva = reservaOpt.get();
            
            // Validar que es su establecimiento
            Long establecimientoId = reserva.getCancha().getEstablecimiento().getId();
            if (!establecimientoService.esAdministradorDelEstablecimiento(establecimientoId, email)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("No tienes permisos para cancelar esta reserva");
            }

            reserva.setEstado(EstadoReserva.CANCELADA);
            Reserva guardada = reservaRepository.save(reserva);

            return ResponseEntity.ok(guardada);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al cancelar reserva: " + e.getMessage());
        }
    }

    /**
     * Obtener detalle de una reserva
     */
    @GetMapping("/reservas/{id}")
    public ResponseEntity<?> obtenerDetalleReserva(@PathVariable Long id) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();

            Optional<Reserva> reservaOpt = reservaRepository.findById(id);
            if (!reservaOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Reserva no encontrada");
            }

            Reserva reserva = reservaOpt.get();
            
            // Validar que es su establecimiento
            Long establecimientoId = reserva.getCancha().getEstablecimiento().getId();
            if (!establecimientoService.esAdministradorDelEstablecimiento(establecimientoId, email)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("No tienes permisos para ver esta reserva");
            }

            return ResponseEntity.ok(reserva);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }
}
