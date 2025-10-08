package com.golReserve.gol.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "establecimiento")
public class Establecimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_establecimiento",nullable = false, length = 150)
    private Long id;

    @Column(name="nombre_cancha",nullable = false, length = 150)
    private String nombre;

    @Column(name ="direccion_establecimiento",nullable =  false, length = 250)
    private String direccion;

    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = false)
    @JsonBackReference("usuario-establecimientos")
    private Usuario administrador;

    @OneToMany(mappedBy = "establecimiento", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Cancha> canchas;


}
