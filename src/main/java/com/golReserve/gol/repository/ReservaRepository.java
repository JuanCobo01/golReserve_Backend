package com.golReserve.gol.repository;

import com.golReserve.gol.entity.Reserva;
import com.golReserve.gol.entity.Enums.EstadoReserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    Optional<Reserva> findById(Long id);
    List<Reserva> findByUsuario_IdUsuario(Long idUsuario);
    List<Reserva> findByCancha_Id(Long idCancha);
    List<Reserva> findByFecha(LocalDate fecha);
    List<Reserva> findByFechaAndCancha_Id(LocalDate fecha, Long idCancha);
    List<Reserva> findByEstado(EstadoReserva estado);
    List<Reserva> findByCancha_IdAndFechaAndEstado(Long idCancha, LocalDate fecha, EstadoReserva estado);
}
