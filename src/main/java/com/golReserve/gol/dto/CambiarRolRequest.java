package com.golReserve.gol.dto;

import com.golReserve.gol.entity.Enums.RolUsuario;
import lombok.Data;

@Data
public class CambiarRolRequest {
    private RolUsuario nuevoRol;
}
