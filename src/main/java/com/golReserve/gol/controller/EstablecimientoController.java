package com.golReserve.gol.controller;

import com.golReserve.gol.entity.Establecimiento;
import com.golReserve.gol.service.EstablecimientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/establecimiento")
public class EstablecimientoController {

    @Autowired
    private EstablecimientoService establecimientoService;

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarEstablecimiento(@RequestBody Establecimiento establecimiento) {
        Establecimiento nuevo = establecimientoService.registrarEstablecimiento(establecimiento);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Establecimiento>> listarEstablecimientos() {
        List<Establecimiento> lista = establecimientoService.listarEstablecimientos();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/buscar/id/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        Optional<Establecimiento> est = establecimientoService.buscarEstablecimientoPorId(id);
        return est.isPresent() ? ResponseEntity.ok(est.get())
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Establecimiento con id: " + id + " no encontrado");
    }

    @GetMapping("/buscar/nombre/{nombre}")
    public ResponseEntity<?> buscarPorNombre(@PathVariable String nombre) {
        Optional<Establecimiento> est = establecimientoService.buscarEstablecimientoPorNombre(nombre);
        return est.isPresent() ? ResponseEntity.ok(est.get())
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Establecimiento con nombre: " + nombre + " no encontrado");
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Establecimiento establecimiento) {
        try {
            Establecimiento actualizado = establecimientoService.actualizarEstablecimiento(id, establecimiento);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        establecimientoService.eliminarEstablecimiento(id);
        return ResponseEntity.ok("Establecimiento eliminado correctamente");
    }
}
