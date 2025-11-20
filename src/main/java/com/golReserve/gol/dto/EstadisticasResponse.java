package com.golReserve.gol.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EstadisticasResponse {
    private Long totalUsuarios;
    private Long totalClientes;
    private Long totalAdministradores;
    private Long totalEstablecimientos;
    private Long totalCanchas;
    private Long totalReservasActivas;
    private Long totalReservasPasadas;
    private Long totalReservasCanceladas;
}
