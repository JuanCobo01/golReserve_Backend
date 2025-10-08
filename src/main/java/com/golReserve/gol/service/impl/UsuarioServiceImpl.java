package com.golReserve.gol.service.impl;


import com.golReserve.gol.entity.Enums.EstadoUsuario;
import com.golReserve.gol.entity.Usuario;
import com.golReserve.gol.repository.UsuarioRepository;
import com.golReserve.gol.service.UsuarioService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {


    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public Usuario registrarUsuario(Usuario usuario) { return usuarioRepository.save(usuario); }

    @Override
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    @Override
    public Optional<Usuario> buscarUsuarioPorId(Long idUsuario) {
        return usuarioRepository.findById(idUsuario);
    }

    public Optional<Usuario> buscarUsuarioPorCC(Long cedula) {
        return usuarioRepository.findByCedula(cedula);
    }

    @Override
    @SneakyThrows
    public Usuario ActualizarUsuario(Long idUsuario, Usuario usuario) {
        Usuario usuarioExistente = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario con ID" + idUsuario + "No encontrado"));
        usuarioExistente.setNombre(usuario.getNombre());
        usuarioExistente.setPassword(usuario.getPassword());
        usuarioExistente.setTelefono(usuario.getTelefono());
        usuarioExistente.setEmail(usuario.getEmail());

        return usuarioRepository.save(usuarioExistente);

    }

    @Override
    @SneakyThrows
    public void eliminarUsuario(Long idUsuario, EstadoUsuario nuevoEstadoUsuario) {
        Usuario usuarioExistenteEliminar = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario con ID" + idUsuario + "No encontrado"));
        usuarioExistenteEliminar.setEstadoUsuario(nuevoEstadoUsuario);
        usuarioRepository.save(usuarioExistenteEliminar);
    }

    @Override
    public Optional<Usuario> ObtenerUsuarioPorEstado(EstadoUsuario estadoUsuario) {
        return usuarioRepository.findByEstadoUsuario(estadoUsuario);
    }
}
