package com.golReserve.gol.repository;

import com.golReserve.gol.entity.Enums.EstadoUsuario;
import com.golReserve.gol.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findById(Long id);
    Optional<Usuario> findByEstadoUsuario(EstadoUsuario estadoUsuario);
    Optional<Usuario> findByCedula(Long cedula);

}
