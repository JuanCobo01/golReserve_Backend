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

    // Métodos para validación de permisos
    List<Establecimiento> buscarEstablecimientosPorAdministrador(String emailAdministrador);
    boolean esAdministradorDelEstablecimiento(Long idEstablecimiento, String emailAdministrador);
}
