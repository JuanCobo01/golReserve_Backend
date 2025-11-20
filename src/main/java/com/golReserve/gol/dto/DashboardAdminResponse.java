package com.golReserve.gol.dto;

import com.golReserve.gol.entity.Establecimiento;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardAdminResponse {
    private Establecimiento establecimiento;
    private Long totalCanchas;
    private Long canchasActivas;
    private Long canchasMantenimiento;
    private Long reservasActivas;
    private Long reservasHoy;
    private Long reservasProximaSemana;
}
