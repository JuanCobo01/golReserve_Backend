package com.golReserve.gol.repository;

import com.golReserve.gol.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    Optional<Reserva> findById(Long id);
    List<Reserva> findByUsuario_IdUsuario(Long idUsuario);
    List<Reserva> findByCancha_Id(Long idCancha);
}

