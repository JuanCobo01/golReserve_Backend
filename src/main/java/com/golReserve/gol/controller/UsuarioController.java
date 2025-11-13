package com.golReserve.gol.controller;

import com.golReserve.gol.dto.LoginRequest;
import com.golReserve.gol.dto.LoginResponse;
import com.golReserve.gol.entity.Usuario;
import com.golReserve.gol.security.JwtTokenUtil;
import com.golReserve.gol.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PostMapping("/login")
    public ResponseEntity<?> loginUsuario(@RequestBody LoginRequest loginRequest) {
        try {
            Usuario usuario = usuarioService.autenticarUsuario(loginRequest.getEmail(), loginRequest.getPassword());

            // Generar token JWT
            String token = jwtTokenUtil.generateToken(
                usuario.getIdUsuario(),
                usuario.getEmail(),
                usuario.getRolUsuario().toString()
            );

            // Crear respuesta personalizada con el token
            LoginResponse response = new LoginResponse(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRolUsuario(),
                "Login exitoso",
                token
            );

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(null, null, null, null, e.getMessage()));
        }
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarUsuario(@RequestBody Usuario usuario){
        try {
            // Log de depuración
            System.out.println("=== INICIANDO REGISTRO ===");
            System.out.println("Email recibido: " + usuario.getEmail());
            System.out.println("Cédula recibida: " + usuario.getCedula());
            System.out.println("Nombre recibido: " + usuario.getNombre());
            System.out.println("Password recibido: " + usuario.getPassword());
            System.out.println("Teléfono recibido: " + usuario.getTelefono());
            System.out.println("Estado recibido: " + usuario.getEstadoUsuario());
            System.out.println("Rol recibido: " + usuario.getRolUsuario());

            Usuario nuevoUsuario = usuarioService.registrarUsuario(usuario);

            System.out.println("=== USUARIO REGISTRADO EXITOSAMENTE ===");
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
        } catch (RuntimeException e) {
            System.out.println("=== ERROR AL REGISTRAR ===");
            System.out.println("Mensaje de error: " + e.getMessage());
            e.printStackTrace();
            // Capturar errores de validación y devolverlos como BAD_REQUEST (400)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Usuario>> listarUsuarios(){
        List<Usuario> usuarios = usuarioService.listarUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/buscar/cedula/{cedula}")
    public ResponseEntity<?> buscarUsuarioPorCedula(@PathVariable String cedula){
        Optional<Usuario> usuario = usuarioService.buscarUsuarioPorCC(cedula);
        return usuario.isPresent() ? ResponseEntity.ok(usuario.get())
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario con cedula: " +cedula+ " no encontrado");
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Usuario usuario) {
        try {
            Usuario actualizado = usuarioService.ActualizarUsuario(id, usuario);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/verificar-sesion")
    public ResponseEntity<?> verificarSesion() {
        // Este endpoint simplemente verificará que el token JWT es válido
        // Si el filtro permite pasar la petición, significa que la sesión es válida
        return ResponseEntity.ok().body(new LoginResponse(null, null, null, null, "Sesión válida"));
    }
}
