package com.golReserve.gol.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservaDTO {
    private Long idReserva;
    private Long idUsuario;
    private Long idCancha;
    private String fechaReserva; // YYYY-MM-DD
    private String horaInicio; // HH:mm
    private String horaFin; // HH:mm
    private String estadoReserva;
    private Double valorTotal;
}
