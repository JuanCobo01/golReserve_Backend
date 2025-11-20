package com.golReserve.gol.controller;

import com.golReserve.gol.entity.Cancha;
import com.golReserve.gol.entity.Enums.EstadoCancha;
import com.golReserve.gol.entity.Enums.TipoCancha;
import com.golReserve.gol.service.CanchaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cancha")
public class CanchaController {

    @Autowired
    private CanchaService canchaService;

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarCancha(@RequestBody Cancha cancha){
        Cancha nuevaCancha = canchaService.registrarCancha(cancha);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaCancha);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Cancha>> listarCancha(){
        List<Cancha> canchas = canchaService.listarCanchas();
        return ResponseEntity.ok(canchas);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Cancha>> buscarCanchasConFiltros(
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) Double precioMin,
            @RequestParam(required = false) Double precioMax,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String nombreEstablecimiento) {

        TipoCancha tipoCancha = null;
        if (tipo != null && !tipo.isEmpty()) {
            try {
                tipoCancha = TipoCancha.valueOf(tipo.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }
        }

        EstadoCancha estadoCancha = null;
        if (estado != null && !estado.isEmpty()) {
            try {
                estadoCancha = EstadoCancha.valueOf(estado.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }
        }

        List<Cancha> canchas = canchaService.buscarCanchasConFiltros(
            tipoCancha, precioMin, precioMax, estadoCancha, null, nombreEstablecimiento
        );

        return ResponseEntity.ok(canchas);
    }

    @GetMapping("/buscar/idcancha/{id}")
    public ResponseEntity<?> buscarCanchaPorId(@PathVariable Long id){
        Optional<Cancha> cancha = canchaService.buscarCanchaPorId(id);
        return cancha.isPresent() ? ResponseEntity.ok(cancha.get())
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cancha con id: " +id+ " no encontrada");
    }

    @GetMapping("/buscar/tipo/{tipoCancha}")
    public ResponseEntity<?> buscarCanchaPorTipo(@PathVariable String tipoCancha) {
        try {
            TipoCancha tipo = TipoCancha.valueOf(tipoCancha.toUpperCase());
            Optional<Cancha> cancha = canchaService.buscarCanchaPorTipo(tipo);

            return cancha.isPresent()
                    ? ResponseEntity.ok(cancha.get())
                    : ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Cancha con tipo: " + tipoCancha + " no encontrada");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Tipo de cancha inválido: " + tipoCancha);
        }
    }

}
