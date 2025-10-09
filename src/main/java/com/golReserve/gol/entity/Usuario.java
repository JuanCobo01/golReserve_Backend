package com.golReserve.gol.entity;

import com.golReserve.gol.entity.Enums.EstadoUsuario;
import com.golReserve.gol.entity.Enums.RolUsuario;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "usuarios")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="usuario_id")
    private long idUsuario;


    @Column(name = ("cc_usuario"),unique = true,nullable = false,length = 25)
    private long cedula;

    @Column(name = "nombre_usuario",nullable = false,length = 150)
    private String nombre;

    @Column(name = "email_usuario",unique = true,nullable = false,length = 150)
    private String email;

    @Column(name = "password_usuario", nullable = false, length = 250)
    private String password;

    @Column(name = "cel_usuario",nullable = false,length = 15)
    private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(name= "estado",nullable = false)
    private EstadoUsuario estadoUsuario;

    @Column(name = "token_verificacion", length = 100)
    private String tokenVerificacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol_usuario", nullable = false)
    private RolUsuario rolUsuario;

    @OneToMany(mappedBy = "usuario")
    @JsonManagedReference("usuario-reservas")
    private List<Reserva> reservas;

    @OneToMany(mappedBy = "administrador")
    @JsonManagedReference("usuario-establecimientos")
    private List<Establecimiento> establecimientos;

}
