package com.golReserve.gol.service;

import com.golReserve.gol.entity.Reserva;
import java.util.List;
import java.util.Optional;

public interface ReservaService {
    Reserva registrarReserva(Reserva reserva);
    List<Reserva> listarReservas();
    Optional<Reserva> buscarReservaPorId(Long id);
    List<Reserva> buscarReservasPorUsuario(Long idUsuario);
    List<Reserva> buscarReservasPorCancha(Long idCancha);
    List<Reserva> buscarReservasPorCanchayFecha(Long idCancha, String fecha);
    Reserva actualizarReserva(Long id, Reserva reserva);
    void eliminarReserva(Long id);
    List<Reserva> buscarReservasPorEmailUsuario(String emailUsuario);

    //métodos para gestión de reservas
    Reserva confirmarReserva(Long idReserva);
    Reserva cancelarReserva(Long idReserva, String motivo);
    Reserva modificarReserva(Long idReserva, Reserva reservaModificada);
    String compartirReserva(Long idReserva);
}
