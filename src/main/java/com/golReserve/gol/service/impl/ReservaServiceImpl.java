package com.golReserve.gol.service.impl;

import com.golReserve.gol.dto.ReservaDTO;
import com.golReserve.gol.entity.Cancha;
import com.golReserve.gol.entity.Reserva;
import com.golReserve.gol.entity.Usuario;
import com.golReserve.gol.entity.Enums.EstadoReserva;
import com.golReserve.gol.repository.CanchaRepository;
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
    
    @Autowired
    private CanchaRepository canchaRepository;

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
    public List<Reserva> buscarReservasPorCanchayFecha(Long idCancha, String fecha) {
        try {
            LocalDate fechaLocal = LocalDate.parse(fecha);
            return reservaRepository.findByCancha_IdAndFechaAndEstado(idCancha, fechaLocal, EstadoReserva.CONFIRMADA);
        } catch (Exception e) {
            throw new RuntimeException("Formato de fecha inválido. Usa yyyy-MM-dd");
        }
    }

    @Override
    public Reserva actualizarReserva(Long id, Reserva reserva) {
        System.out.println("=== ACTUALIZANDO RESERVA ===");
        System.out.println("ID: " + id);
        System.out.println("Estado recibido: " + reserva.getEstado());
        
        Reserva existente = reservaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Reserva con ID " + id + " no encontrada"));
        
        System.out.println("Estado actual: " + existente.getEstado());
        
        // Solo actualizar campos que no sean null
        if (reserva.getFecha() != null) {
            existente.setFecha(reserva.getFecha());
        }
        if (reserva.getHoraInicio() != null) {
            existente.setHoraInicio(reserva.getHoraInicio());
        }
        if (reserva.getHoraFin() != null) {
            existente.setHoraFin(reserva.getHoraFin());
        }
        if (reserva.getEstado() != null) {
            System.out.println("Actualizando estado a: " + reserva.getEstado());
            existente.setEstado(reserva.getEstado());
        }
        if (reserva.getCancha() != null) {
            existente.setCancha(reserva.getCancha());
        }
        if (reserva.getUsuario() != null) {
            existente.setUsuario(reserva.getUsuario());
        }
        
        Reserva guardada = reservaRepository.save(existente);
        System.out.println("Estado guardado: " + guardada.getEstado());
        System.out.println("=== FIN ACTUALIZACIÓN ===");
        
        return guardada;
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
    
    @Override
    public List<Reserva> obtenerReservasPorFecha(LocalDate fecha) {
        return reservaRepository.findByFecha(fecha);
    }
    
    @Override
    public Reserva crearReservaDesdeDTO(ReservaDTO reservaDTO) {
        // Validar que el usuario existe
        Usuario usuario = usuarioRepository.findById(reservaDTO.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + reservaDTO.getIdUsuario()));
        
        // Validar que la cancha existe
        Cancha cancha = canchaRepository.findById(reservaDTO.getIdCancha())
                .orElseThrow(() -> new RuntimeException("Cancha no encontrada con id: " + reservaDTO.getIdCancha()));
        
        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setCancha(cancha);
        reserva.setFecha(reservaDTO.getFechaReserva());
        reserva.setHoraInicio(reservaDTO.getHoraInicio());
        reserva.setHoraFin(reservaDTO.getHoraFin());
        
        // Convertir String a Enum
        try {
            reserva.setEstado(EstadoReserva.valueOf(reservaDTO.getEstadoReserva()));
        } catch (IllegalArgumentException e) {
            reserva.setEstado(EstadoReserva.PENDIENTE);
        }
        
        return reservaRepository.save(reserva);
    }
    
    @Override
    public Reserva actualizarReservaDesdeDTO(Long id, ReservaDTO reservaDTO) {
        Reserva reservaExistente = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada con id: " + id));
        
        reservaExistente.setFecha(reservaDTO.getFechaReserva());
        reservaExistente.setHoraInicio(reservaDTO.getHoraInicio());
        reservaExistente.setHoraFin(reservaDTO.getHoraFin());
        
        // Convertir String a Enum
        try {
            reservaExistente.setEstado(EstadoReserva.valueOf(reservaDTO.getEstadoReserva()));
        } catch (IllegalArgumentException e) {
            // Mantener el estado actual si el valor no es válido
        }
        
        return reservaRepository.save(reservaExistente);
    }
    
    @Override
    public boolean verificarDisponibilidad(Long idCancha, LocalDate fecha, LocalTime horaInicio, LocalTime horaFin) {
        List<Reserva> reservasExistentes = reservaRepository.findByFechaAndCancha_Id(fecha, idCancha);
        
        for (Reserva reserva : reservasExistentes) {
            if (reserva.getEstado() == EstadoReserva.CONFIRMADA || reserva.getEstado() == EstadoReserva.PENDIENTE) {
                // Verificar si hay solapamiento de horarios
                // No hay conflicto si: horaFin <= reserva.horaInicio O horaInicio >= reserva.horaFin
                boolean noHayConflicto = horaFin.isBefore(reserva.getHoraInicio()) || 
                                        horaFin.equals(reserva.getHoraInicio()) ||
                                        horaInicio.isAfter(reserva.getHoraFin()) ||
                                        horaInicio.equals(reserva.getHoraFin());
                
                if (!noHayConflicto) {
                    return false; // Hay conflicto
                }
            }
        }
        
        return true; // Está disponible
    }
    
    @Override
    public List<ReservaDTO> getReservasByUsuarioId(Long idUsuario) {
        List<Reserva> reservas = reservaRepository.findByUsuario_IdUsuario(idUsuario);
        return reservas.stream()
            .map(this::convertToDTO)
            .collect(java.util.stream.Collectors.toList());
    }

    private ReservaDTO convertToDTO(Reserva reserva) {
        ReservaDTO dto = new ReservaDTO();
        dto.setIdReserva(reserva.getId());
        dto.setFechaReserva(reserva.getFecha());
        dto.setHoraInicio(reserva.getHoraInicio());
        dto.setHoraFin(reserva.getHoraFin());
        dto.setEstadoReserva(reserva.getEstado().name());
        dto.setIdUsuario(reserva.getUsuario().getIdUsuario());
        
        // Información de la cancha
        dto.setIdCancha(reserva.getCancha().getId());
        dto.setTipoCancha(reserva.getCancha().getTipoCancha().name());
        dto.setPrecioHora(reserva.getCancha().getPrecioHora());
        
        return dto;
    }
}

