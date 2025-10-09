package com.golReserve.gol.controller;

import com.golReserve.gol.entity.Reserva;
import com.golReserve.gol.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

    @GetMapping("/buscar/id/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Optional<Reserva> reserva = reservaService.buscarReservaPorId(id);
        return reserva.isPresent() ? ResponseEntity.ok(reserva.get())
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reserva con id: " + id + " no encontrada");
    }

    @GetMapping("/buscar/usuario/{idUsuario}")
    public ResponseEntity<List<Reserva>> buscarPorUsuario(@PathVariable Long idUsuario) {
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
}

