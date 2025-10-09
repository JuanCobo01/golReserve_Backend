package com.golReserve.gol.service.impl;

import com.golReserve.gol.entity.Reserva;
import com.golReserve.gol.repository.ReservaRepository;
import com.golReserve.gol.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReservaServiceImpl implements ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

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
}

