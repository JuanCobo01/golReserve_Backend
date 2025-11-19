package com.golReserve.gol.controller;

import com.golReserve.gol.entity.Establecimiento;
import com.golReserve.gol.service.EstablecimientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @GetMapping("/mis-establecimientos")
    public ResponseEntity<?> listarMisEstablecimientos() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        try {
            List<Establecimiento> establecimientos = establecimientoService.buscarEstablecimientosPorAdministrador(email);
            return ResponseEntity.ok(establecimientos);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
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

    @GetMapping("/contar-canchas/{id}")
    public ResponseEntity<?> contarCanchasDisponibles(@PathVariable Long id) {
        try {
            int cantidadCanchas = establecimientoService.contarCanchasDisponibles(id);
            return ResponseEntity.ok().body("{ \"establecimientoId\": " + id + ", \"canchasDisponibles\": " + cantidadCanchas + " }");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error al contar canchas del establecimiento con id: " + id);
        }
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Establecimiento establecimiento) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        String role = auth.getAuthorities().iterator().next().getAuthority();

        try {
            // Si es ADMINISTRADOR, validar que sea su establecimiento
            if ("ADMINISTRADOR".equals(role)) {
                if (!establecimientoService.esAdministradorDelEstablecimiento(id, email)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body("No tienes permisos para modificar este establecimiento");
                }
            }

            Establecimiento actualizado = establecimientoService.actualizarEstablecimiento(id, establecimiento);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        String role = auth.getAuthorities().iterator().next().getAuthority();

        // Si es ADMINISTRADOR, validar que sea su establecimiento
        if ("ADMINISTRADOR".equals(role)) {
            if (!establecimientoService.esAdministradorDelEstablecimiento(id, email)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("No tienes permisos para eliminar este establecimiento");
            }
        }

        establecimientoService.eliminarEstablecimiento(id);
        return ResponseEntity.ok("Establecimiento eliminado correctamente");
    }
}
