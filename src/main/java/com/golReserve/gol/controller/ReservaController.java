package com.golReserve.gol.controller;

import com.golReserve.gol.dto.ReservaDTO;
import com.golReserve.gol.entity.Reserva;
import com.golReserve.gol.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarReserva(@RequestBody Reserva reserva) {
        Reserva nueva = reservaService.registrarReserva(reserva);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    @GetMapping
    public ResponseEntity<List<Reserva>> obtenerReservas() {
        List<Reserva> lista = reservaService.listarReservas();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Reserva>> listarReservas() {
        List<Reserva> lista = reservaService.listarReservas();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/mis-reservas")
    public ResponseEntity<?> listarMisReservas() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        try {
            List<Reserva> reservas = reservaService.buscarReservasPorEmailUsuario(email);
            return ResponseEntity.ok(reservas);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<?> obtenerReservasPorUsuario(@PathVariable Long idUsuario) {
        try {
            List<ReservaDTO> reservas = reservaService.getReservasByUsuarioId(idUsuario);
            return ResponseEntity.ok(reservas);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("mensaje", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    @GetMapping("/buscar/id/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Optional<Reserva> reserva = reservaService.buscarReservaPorId(id);
        return reserva.isPresent() ? ResponseEntity.ok(reserva.get())
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reserva con id: " + id + " no encontrada");
    }

    @GetMapping("/buscar/usuario/{idUsuario}")
    public ResponseEntity<?> buscarPorUsuario(@PathVariable Long idUsuario) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        String role = auth.getAuthorities().iterator().next().getAuthority();

        // Si es CLIENTE, solo puede ver sus propias reservas
        if ("ROLE_CLIENTE".equals(role)) {
            try {
                List<Reserva> reservasDelCliente = reservaService.buscarReservasPorEmailUsuario(email);
                return ResponseEntity.ok(reservasDelCliente);
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permisos para ver estas reservas");
            }
        }

        List<Reserva> reservas = reservaService.buscarReservasPorUsuario(idUsuario);
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/buscar/cancha/{idCancha}")
    public ResponseEntity<List<Reserva>> buscarPorCancha(@PathVariable Long idCancha) {
        List<Reserva> reservas = reservaService.buscarReservasPorCancha(idCancha);
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/buscar/cancha/{idCancha}/fecha")
    public ResponseEntity<?> buscarPorCanchayFecha(@PathVariable Long idCancha,
                                                    @RequestParam(required = false) String fecha) {
        try {
            if (fecha == null || fecha.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("El parámetro 'fecha' es obligatorio. Formato: yyyy-MM-dd");
            }
            List<Reserva> reservas = reservaService.buscarReservasPorCanchayFecha(idCancha, fecha);
            return ResponseEntity.ok(reservas);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Reserva reserva) {
        try {
            Reserva actualizado = reservaService.actualizarReserva(id, reserva);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarReserva(@PathVariable Long id, @RequestBody Reserva reserva) {
        try {
            Reserva actualizado = reservaService.actualizarReserva(id, reserva);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        reservaService.eliminarReserva(id);
        return ResponseEntity.ok("Reserva eliminada correctamente");
    }

    @PostMapping("/confirmar/{id}")
    public ResponseEntity<?> confirmarReserva(@PathVariable Long id) {
        try {
            Reserva reservaConfirmada = reservaService.confirmarReserva(id);
            return ResponseEntity.ok(reservaConfirmada);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/cancelar/{id}")
    public ResponseEntity<?> cancelarReserva(@PathVariable Long id,
                                              @RequestParam(required = false) String motivo) {
        try {
            Reserva reservaCancelada = reservaService.cancelarReserva(id, motivo);
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Reserva cancelada exitosamente");
            response.put("reserva", reservaCancelada);
            response.put("motivo", motivo != null ? motivo : "No especificado");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/cancelar/{id}")
    public ResponseEntity<?> cancelarReservaPut(@PathVariable Long id,
                                                 @RequestParam(required = false) String motivo) {
        try {
            Reserva reservaCancelada = reservaService.cancelarReserva(id, motivo);
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Reserva cancelada exitosamente");
            response.put("reserva", reservaCancelada);
            response.put("motivo", motivo != null ? motivo : "No especificado");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/modificar/{id}")
    public ResponseEntity<?> modificarReserva(@PathVariable Long id, @RequestBody Reserva reserva) {
        try {
            Reserva reservaModificada = reservaService.modificarReserva(id, reserva);
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Reserva modificada exitosamente. Estado cambiado a PENDIENTE para confirmación");
            response.put("reserva", reservaModificada);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/compartir/{id}")
    public ResponseEntity<?> compartirReserva(@PathVariable Long id) {
        try {
            String mensajeCompartible = reservaService.compartirReserva(id);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", mensajeCompartible);
            response.put("tipo", "texto");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    // Nuevos endpoints con DTO y validación de disponibilidad
    
    @PostMapping("/crear")
    public ResponseEntity<Map<String, Object>> crearReservaConValidacion(@RequestBody ReservaDTO reservaDTO) {
        try {
            Map<String, Object> response = new HashMap<>();
            
            // Verificar disponibilidad
            boolean disponible = reservaService.verificarDisponibilidad(
                reservaDTO.getIdCancha(),
                reservaDTO.getFechaReserva(),
                reservaDTO.getHoraInicio(),
                reservaDTO.getHoraFin()
            );
            
            if (!disponible) {
                response.put("mensaje", "El horario seleccionado no está disponible");
                response.put("success", false);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
            
            Reserva nuevaReserva = reservaService.crearReservaDesdeDTO(reservaDTO);
            response.put("mensaje", "Reserva creada exitosamente");
            response.put("success", true);
            response.put("reserva", nuevaReserva);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("mensaje", "Error al crear la reserva: " + e.getMessage());
            errorResponse.put("success", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    @PutMapping("/actualizar-dto/{id}")
    public ResponseEntity<Map<String, Object>> actualizarReservaConDTO(
            @PathVariable Long id,
            @RequestBody ReservaDTO reservaDTO) {
        try {
            Map<String, Object> response = new HashMap<>();
            Reserva reservaActualizada = reservaService.actualizarReservaDesdeDTO(id, reservaDTO);
            
            response.put("mensaje", "Reserva actualizada exitosamente");
            response.put("success", true);
            response.put("reserva", reservaActualizada);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("mensaje", e.getMessage());
            errorResponse.put("success", false);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("mensaje", "Error al actualizar la reserva");
            errorResponse.put("success", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    @GetMapping("/verificar-disponibilidad")
    public ResponseEntity<Map<String, Object>> verificarDisponibilidad(
            @RequestParam Long idCancha,
            @RequestParam String fecha,
            @RequestParam String horaInicio,
            @RequestParam String horaFin) {
        try {
            Map<String, Object> response = new HashMap<>();
            boolean disponible = reservaService.verificarDisponibilidad(
                idCancha,
                LocalDate.parse(fecha),
                LocalTime.parse(horaInicio),
                LocalTime.parse(horaFin)
            );
            
            response.put("disponible", disponible);
            response.put("mensaje", disponible ? "Horario disponible" : "Horario no disponible");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("mensaje", "Error al verificar disponibilidad: " + e.getMessage());
            errorResponse.put("disponible", false);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
    
    @GetMapping("/buscar/fecha/{fecha}")
    public ResponseEntity<List<Reserva>> buscarPorFecha(@PathVariable String fecha) {
        try {
            List<Reserva> reservas = reservaService.obtenerReservasPorFecha(LocalDate.parse(fecha));
            return ResponseEntity.ok(reservas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
