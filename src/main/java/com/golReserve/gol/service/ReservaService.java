package com.golReserve.gol.service;

import com.golReserve.gol.dto.ReservaDTO;
import com.golReserve.gol.entity.Reserva;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface ReservaService {
    Reserva registrarReserva(Reserva reserva);
    List<Reserva> listarReservas();
    Optional<Reserva> buscarReservaPorId(Long id);
    List<Reserva> buscarReservasPorUsuario(Long idUsuario);
    List<Reserva> buscarReservasPorCancha(Long idCancha);
    Reserva actualizarReserva(Long id, Reserva reserva);
    void eliminarReserva(Long id);
    List<Reserva> buscarReservasPorEmailUsuario(String emailUsuario);

    //métodos para gestión de reservas
    Reserva confirmarReserva(Long idReserva);
    Reserva cancelarReserva(Long idReserva, String motivo);
    Reserva modificarReserva(Long idReserva, Reserva reservaModificada);
    String compartirReserva(Long idReserva);
    
    // Nuevos métodos para gestión con DTO y disponibilidad
    List<Reserva> obtenerReservasPorFecha(LocalDate fecha);
    Reserva crearReservaDesdeDTO(ReservaDTO reservaDTO);
    Reserva actualizarReservaDesdeDTO(Long id, ReservaDTO reservaDTO);
    boolean verificarDisponibilidad(Long idCancha, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin);
}

