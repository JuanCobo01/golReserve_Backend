package com.golReserve.gol.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservaDTO {
    private Long idReserva;
    private LocalDate fechaReserva;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String estadoReserva;
    private Long idUsuario;
    
    // Información de la cancha
    private Long idCancha;
    private String tipoCancha;
    private Double precioHora;
}
