package com.golReserve.gol.security.dto;

import com.golReserve.gol.entity.Enums.RolUsuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private Long idUsuario;
    private String nombre;
    private String email;
    private RolUsuario rolUsuario;
    private String mensaje;
    private String token;

    // Constructor adicional sin token para mantener la compatibilidad con el código existente
    public LoginResponse(Long idUsuario, String nombre, String email, RolUsuario rolUsuario, String mensaje) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.email = email;
        this.rolUsuario = rolUsuario;
        this.mensaje = mensaje;
    }
}
