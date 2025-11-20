package com.golReserve.gol.dto;

import lombok.Data;

@Data
public class CrearAdministradorRequest {
    // Datos del administrador
    private String nombreCompleto;
    private String cedula;
    private String telefono;
    private String email;
    private String password;
    
    // Datos del establecimiento
    private String nombreEstablecimiento;
    private String direccionEstablecimiento;
}
