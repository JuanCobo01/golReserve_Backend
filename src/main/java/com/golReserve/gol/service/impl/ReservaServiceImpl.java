package com.golReserve.gol.service.impl;

import com.golReserve.gol.entity.Reserva;
import com.golReserve.gol.entity.Usuario;
import com.golReserve.gol.entity.Enums.EstadoReserva;
import com.golReserve.gol.repository.ReservaRepository;
import com.golReserve.gol.repository.UsuarioRepository;
import com.golReserve.gol.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservaServiceImpl implements ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public Reserva registrarReserva(Reserva reserva) {
        return reservaRepository.save(reserva);
    }

    @Override
    public List<Reserva> listarReservas() {
        return reservaRepository.findAll();
    }

    @Override
    public Optional<Reserva> buscarReservaPorId(Long id) {
        return reservaRepository.findById(id);
    }

    @Override
    public List<Reserva> buscarReservasPorUsuario(Long idUsuario) {
        return reservaRepository.findByUsuario_IdUsuario(idUsuario);
    }

    @Override
    public List<Reserva> buscarReservasPorCancha(Long idCancha) {
        return reservaRepository.findByCancha_Id(idCancha);
    }

    @Override
    public Reserva actualizarReserva(Long id, Reserva reserva) {
        Reserva existente = reservaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Reserva con ID " + id + " no encontrada"));
        existente.setFecha(reserva.getFecha());
        existente.setHoraInicio(reserva.getHoraInicio());
        existente.setHoraFin(reserva.getHoraFin());
        existente.setEstado(reserva.getEstado());
        return reservaRepository.save(existente);
    }

    @Override
    public void eliminarReserva(Long id) {
        reservaRepository.deleteById(id);
    }

    @Override
    public List<Reserva> buscarReservasPorEmailUsuario(String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return reservaRepository.findByUsuario_IdUsuario(usuario.getIdUsuario());
    }

    @Override
    public Reserva confirmarReserva(Long idReserva) {
        Reserva reserva = reservaRepository.findById(idReserva)
            .orElseThrow(() -> new RuntimeException("Reserva con ID " + idReserva + " no encontrada"));

        // Validar que la reserva esté en estado PENDIENTE
        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new RuntimeException("Solo se pueden confirmar reservas en estado PENDIENTE");
        }

        // Validar que la fecha no sea pasada
        if (reserva.getFecha().isBefore(LocalDate.now())) {
            throw new RuntimeException("No se puede confirmar una reserva con fecha pasada");
        }

        reserva.setEstado(EstadoReserva.CONFIRMADA);
        return reservaRepository.save(reserva);
    }

    @Override
    public Reserva cancelarReserva(Long idReserva, String motivo) {
        Reserva reserva = reservaRepository.findById(idReserva)
            .orElseThrow(() -> new RuntimeException("Reserva con ID " + idReserva + " no encontrada"));

        // Validar que la reserva no esté ya cancelada o completada
        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new RuntimeException("La reserva ya está cancelada");
        }

        if (reserva.getEstado() == EstadoReserva.COMPLETADA) {
            throw new RuntimeException("No se puede cancelar una reserva completada");
        }

        reserva.setEstado(EstadoReserva.CANCELADA);
        return reservaRepository.save(reserva);
    }

    @Override
    public Reserva modificarReserva(Long idReserva, Reserva reservaModificada) {
        Reserva reservaExistente = reservaRepository.findById(idReserva)
            .orElseThrow(() -> new RuntimeException("Reserva con ID " + idReserva + " no encontrada"));

        // Validar que la reserva esté en estado PENDIENTE o CONFIRMADA
        if (reservaExistente.getEstado() == EstadoReserva.CANCELADA ||
            reservaExistente.getEstado() == EstadoReserva.COMPLETADA) {
            throw new RuntimeException("No se puede modificar una reserva cancelada o completada");
        }

        // Validar que la nueva fecha no sea pasada
        if (reservaModificada.getFecha() != null && reservaModificada.getFecha().isBefore(LocalDate.now())) {
            throw new RuntimeException("No se puede modificar a una fecha pasada");
        }

        // Validar que hora fin sea después de hora inicio
        if (reservaModificada.getHoraInicio() != null && reservaModificada.getHoraFin() != null) {
            if (reservaModificada.getHoraFin().isBefore(reservaModificada.getHoraInicio()) ||
                reservaModificada.getHoraFin().equals(reservaModificada.getHoraInicio())) {
                throw new RuntimeException("La hora de fin debe ser posterior a la hora de inicio");
            }
        }

        // Actualizar campos permitidos
        if (reservaModificada.getFecha() != null) {
            reservaExistente.setFecha(reservaModificada.getFecha());
        }
        if (reservaModificada.getHoraInicio() != null) {
            reservaExistente.setHoraInicio(reservaModificada.getHoraInicio());
        }
        if (reservaModificada.getHoraFin() != null) {
            reservaExistente.setHoraFin(reservaModificada.getHoraFin());
        }

        // Cambiar estado a PENDIENTE si se modificó
        reservaExistente.setEstado(EstadoReserva.PENDIENTE);

        return reservaRepository.save(reservaExistente);
    }

    @Override
    public String compartirReserva(Long idReserva) {
        Reserva reserva = reservaRepository.findById(idReserva)
            .orElseThrow(() -> new RuntimeException("Reserva con ID " + idReserva + " no encontrada"));

        // Generar un enlace compartible (simulado)
        StringBuilder mensaje = new StringBuilder();
        mensaje.append("¡Reserva de cancha confirmada!\n\n");
        mensaje.append("Fecha: ").append(reserva.getFecha()).append("\n");
        mensaje.append("Horario: ").append(reserva.getHoraInicio())
               .append(" - ").append(reserva.getHoraFin()).append("\n");
        mensaje.append("Cancha: ").append(reserva.getCancha().getTipoCancha()).append("\n");
        mensaje.append("Establecimiento: ").append(reserva.getCancha().getEstablecimiento().getNombre()).append("\n");
        mensaje.append("Precio: $").append(reserva.getCancha().getPrecioHora()).append(" por hora\n");
        mensaje.append("Estado: ").append(reserva.getEstado()).append("\n\n");
        mensaje.append("Código de reserva: #").append(reserva.getId());

        return mensaje.toString();
    }
}
