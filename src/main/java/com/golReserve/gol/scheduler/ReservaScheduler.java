package com.golReserve.gol.scheduler;

import com.golReserve.gol.entity.Enums.EstadoReserva;
import com.golReserve.gol.entity.Reserva;
import com.golReserve.gol.repository.ReservaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Tarea programada para actualizar automáticamente el estado de las reservas
 */
@Component
public class ReservaScheduler {

    private static final Logger logger = LoggerFactory.getLogger(ReservaScheduler.class);

    @Autowired
    private ReservaRepository reservaRepository;

    /**
     * Se ejecuta diariamente a las 00:01
     * Cambia el estado de reservas confirmadas cuya fecha ya pasó
     */
    @Scheduled(cron = "0 1 0 * * *") // Segundo Minuto Hora Día Mes DíaSemana
    public void actualizarReservasPasadas() {
        try {
            logger.info("=== INICIANDO TAREA: Actualizar reservas pasadas ===");
            
            LocalDate hoy = LocalDate.now();
            
            // Buscar reservas confirmadas cuya fecha ya pasó
            List<Reserva> reservasPasadas = reservaRepository.findReservasPasadas(
                    hoy, EstadoReserva.CONFIRMADA);
            
            logger.info("Reservas encontradas para actualizar: {}", reservasPasadas.size());
            
            int actualizadas = 0;
            for (Reserva reserva : reservasPasadas) {
                reserva.setEstado(EstadoReserva.CANCELADA); // O crear un nuevo estado PASADA
                reservaRepository.save(reserva);
                actualizadas++;
                
                logger.debug("Reserva ID {} actualizada a PASADA", reserva.getId());
            }
            
            logger.info("=== TAREA COMPLETADA: {} reservas actualizadas ===", actualizadas);
            
        } catch (Exception e) {
            logger.error("Error al actualizar reservas pasadas: {}", e.getMessage(), e);
        }
    }

    /**
     * Tarea alternativa que se ejecuta cada hora para validaciones adicionales
     */
    @Scheduled(fixedRate = 3600000) // Cada hora (en milisegundos)
    public void verificarReservasProximas() {
        try {
            logger.debug("Verificando estado de reservas próximas...");
            // Aquí se pueden agregar otras validaciones si son necesarias
        } catch (Exception e) {
            logger.error("Error en verificación de reservas: {}", e.getMessage());
        }
    }
}
