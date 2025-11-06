package com.golReserve.gol.controller;

import com.golReserve.gol.entity.Reserva;
import com.golReserve.gol.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reserva")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarReserva(@RequestBody Reserva reserva) {
        Reserva nueva = reservaService.registrarReserva(reserva);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
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
        if ("CLIENTE".equals(role)) {
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

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Reserva reserva) {
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
}
