package com.golReserve.gol.service;

import com.golReserve.gol.entity.Establecimiento;
import java.util.List;
import java.util.Optional;

public interface EstablecimientoService {
    Establecimiento registrarEstablecimiento(Establecimiento establecimiento);
    List<Establecimiento> listarEstablecimientos();
    Optional<Establecimiento> buscarEstablecimientoPorId(Long id);
    Optional<Establecimiento> buscarEstablecimientoPorNombre(String nombre);
    Establecimiento actualizarEstablecimiento(Long id, Establecimiento establecimiento);
    void eliminarEstablecimiento(Long id);
}
