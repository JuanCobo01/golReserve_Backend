package com.golReserve.gol.repository;

import com.golReserve.gol.entity.Establecimiento;
import com.golReserve.gol.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EstablecimientoRepository extends JpaRepository<Establecimiento, Long> {
    Optional<Establecimiento> findById(Long id);
    Optional<Establecimiento> findByNombre(String nombre);
    List<Establecimiento> findByAdministrador(Usuario administrador);
}
