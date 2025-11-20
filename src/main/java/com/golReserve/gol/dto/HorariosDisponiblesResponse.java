package com.golReserve.gol.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HorariosDisponiblesResponse {
    private Long canchaId;
    private String nombreCancha;
    private LocalDate fecha;
    private List<HorarioDisponible> horariosDisponibles;
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HorarioDisponible {
        private LocalTime horaInicio;
        private LocalTime horaFin;
        private boolean disponible;
    }
}
