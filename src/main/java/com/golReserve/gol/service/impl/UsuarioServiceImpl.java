package com.golReserve.gol.service.impl;


import com.golReserve.gol.entity.Enums.EstadoUsuario;
import com.golReserve.gol.entity.Enums.RolUsuario;
import com.golReserve.gol.entity.Usuario;
import com.golReserve.gol.repository.UsuarioRepository;
import com.golReserve.gol.service.UsuarioService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Patrón para validar email
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    // Patrón para validar contraseña (mínimo 8 caracteres, al menos una letra y un número)
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{8,}$"
    );

    @Override
    public Usuario registrarUsuario(Usuario usuario) {
        // VALIDACIÓN IMPORTANTE: Solo permitir registro público con rol CLIENTE
        // Los roles ADMINISTRADOR y SUPER_ADMINISTRADOR deben ser creados por SUPER_ADMIN
        if (usuario.getRolUsuario() != null && usuario.getRolUsuario() != RolUsuario.CLIENTE) {
            throw new RuntimeException("No se puede auto-registrar con este rol. Contacte al administrador del sistema.");
        }
        
        // Forzar rol CLIENTE para registro público
        usuario.setRolUsuario(RolUsuario.CLIENTE);
        
        // Establecer estado ACTIVO por defecto
        usuario.setEstadoUsuario(EstadoUsuario.ACTIVO);
        
        // Validar formato de email
        if (usuario.getEmail() == null || !EMAIL_PATTERN.matcher(usuario.getEmail()).matches()) {
            throw new RuntimeException("El formato del correo electrónico no es válido");
        }

        // Validar que el email no esté duplicado
        Optional<Usuario> emailExistente = usuarioRepository.findByEmail(usuario.getEmail());
        if (emailExistente.isPresent()) {
            throw new RuntimeException("El correo electrónico ya está registrado");
        }

        // Validar que la cédula no esté duplicada
        Optional<Usuario> cedulaExistente = usuarioRepository.findByCedula(usuario.getCedula());
        if (cedulaExistente.isPresent()) {
            throw new RuntimeException("La cédula ya está registrada");
        }

        // Validar fortaleza de contraseña
        if (usuario.getPassword() == null || !PASSWORD_PATTERN.matcher(usuario.getPassword()).matches()) {
            throw new RuntimeException("La contraseña debe tener al menos 8 caracteres, incluyendo letras y números");
        }

        // Encriptar la contraseña antes de guardar
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));

        // Validar que el nombre no esté vacío
        if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty()) {
            throw new RuntimeException("El nombre es obligatorio");
        }

        // Validar teléfono (básico)
        if (usuario.getTelefono() == null || usuario.getTelefono().length() < 10) {
            throw new RuntimeException("El teléfono debe tener al menos 10 dígitos");
        }

        return usuarioRepository.save(usuario);
    }

    @Override
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    @Override
    public Optional<Usuario> buscarUsuarioPorId(Long idUsuario) {
        return usuarioRepository.findById(idUsuario);
    }

    public Optional<Usuario> buscarUsuarioPorCC(String cedula) {
        return usuarioRepository.findByCedula(cedula);
    }

    @Override
    @SneakyThrows
    public Usuario ActualizarUsuario(Long idUsuario, Usuario usuario) {
        Usuario usuarioExistente = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario con ID" + idUsuario + "No encontrado"));
        usuarioExistente.setNombre(usuario.getNombre());

        // Solo encriptar la contraseña si se está actualizando
        if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
            usuarioExistente.setPassword(passwordEncoder.encode(usuario.getPassword()));
        }

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

    @Override
    @SneakyThrows
    public Usuario autenticarUsuario(String email, String password) throws RuntimeException {
        // Buscar usuario por email
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (!usuarioOpt.isPresent()) {
            throw new RuntimeException("Credenciales incorrectas");
        }

        Usuario usuario = usuarioOpt.get();

        // Verificar si el usuario está activo
        if (usuario.getEstadoUsuario() != EstadoUsuario.ACTIVO) {
            throw new RuntimeException("La cuenta de usuario no está activa. Por favor contacte al administrador");
        }

        // Verificar contraseña usando BCrypt
        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new RuntimeException("Credenciales incorrectas");
        }

        return usuario;
    }
}
