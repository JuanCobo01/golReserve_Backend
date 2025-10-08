package com.golReserve.gol.entity;

import com.golReserve.gol.entity.Enums.EstadoCancha;
import com.golReserve.gol.entity.Enums.EstadoUsuario;
import com.golReserve.gol.entity.Enums.RolUsuario;
import com.golReserve.gol.entity.Enums.TipoCancha;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.web.WebProperties;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cancha")
public class Cancha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="id_cancha")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name= "tipo_cancha",nullable = false)
    private TipoCancha tipoCancha;

    @Column(name= "precio_hora",nullable = false)
    private Double precioHora;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_cancha", nullable = false)
    private EstadoCancha estadoCancha;

    @ManyToOne
    @JoinColumn(name = "establecimiento_id", nullable = false)
    @JsonBackReference
    private Establecimiento establecimiento;


    @OneToMany(mappedBy = "cancha", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("cancha-reservas")
    private List<Reserva> reservas;




}
