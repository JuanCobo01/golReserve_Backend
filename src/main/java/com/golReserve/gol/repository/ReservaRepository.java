package com.golReserve.gol.repository;

import com.golReserve.gol.entity.Reserva;
import com.golReserve.gol.entity.Enums.EstadoReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
    
    Long countByEstado(EstadoReserva estado);
    
    @Query("SELECT r FROM Reserva r WHERE r.cancha.establecimiento.id = :establecimientoId")
    List<Reserva> findByEstablecimientoId(@Param("establecimientoId") Long establecimientoId);
    
    @Query("SELECT r FROM Reserva r WHERE r.cancha.establecimiento.id = :establecimientoId AND r.estado = :estado")
    List<Reserva> findByEstablecimientoIdAndEstado(@Param("establecimientoId") Long establecimientoId, @Param("estado") EstadoReserva estado);
    
    @Query("SELECT r FROM Reserva r WHERE r.cancha.establecimiento.id = :establecimientoId AND r.fecha = :fecha")
    List<Reserva> findByEstablecimientoIdAndFecha(@Param("establecimientoId") Long establecimientoId, @Param("fecha") LocalDate fecha);
    
    @Query("SELECT COUNT(r) FROM Reserva r WHERE r.cancha.establecimiento.id = :establecimientoId AND r.estado = :estado")
    Long countByEstablecimientoIdAndEstado(@Param("establecimientoId") Long establecimientoId, @Param("estado") EstadoReserva estado);
    
    @Query("SELECT r FROM Reserva r WHERE r.fecha < :fecha AND r.estado = :estadoActual")
    List<Reserva> findReservasPasadas(@Param("fecha") LocalDate fecha, @Param("estadoActual") EstadoReserva estadoActual);
}

