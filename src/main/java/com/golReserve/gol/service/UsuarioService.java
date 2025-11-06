package com.golReserve.gol.service;



import com.golReserve.gol.entity.Enums.EstadoUsuario;
import com.golReserve.gol.entity.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {

    //Registra un nuevo usuario
    Usuario registrarUsuario(Usuario usuario);

    //Lista los usuarios
    List<Usuario> listarUsuarios();

    //Busca un usuario por id
    Optional<Usuario> buscarUsuarioPorId(Long idUsuario);

    //Busca un usuario por cedula
    Optional<Usuario> buscarUsuarioPorCC(Long cedula);

    //Actualiza información de un usuario
    Usuario ActualizarUsuario(Long idUsuario,Usuario usuario);

    //Elimina cuenta de un usuario cambiando su estado a inactivo
    void eliminarUsuario(Long idUsuario, EstadoUsuario nuevoEstadoUsuario);

    //Obtiene usuarios que estan registrados y activos
    Optional<Usuario> ObtenerUsuarioPorEstado(EstadoUsuario estadoUsuario);

    //Autenticar usuario para login
    Usuario autenticarUsuario(String email, String password) throws RuntimeException;
}
