package com.golReserve.gol.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.golReserve.gol.dto.LoginResponse;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    // Lista de rutas públicas que no requieren autenticación
    private static final List<String> PUBLIC_ROUTES = Arrays.asList(
            "/api/usuarios/login",
            "/api/usuarios/registrar",
            "/error"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // Comprobar si la ruta es pública
        String path = request.getServletPath();
        if (isPublicRoute(path)) {
            chain.doFilter(request, response);
            return;
        }

        // Obtener el header Authorization
        final String requestTokenHeader = request.getHeader("Authorization");

        // Si no hay token en la petición, enviar error
        if (requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer ")) {
            sendError(response, "No se proporcionó token de autenticación");
            return;
        }

        // Extraer el token sin el prefijo "Bearer "
        String jwtToken = requestTokenHeader.substring(7);

        try {
            // Validar el token
            if (!jwtTokenUtil.validateToken(jwtToken)) {
                sendError(response, "Token expirado o inválido");
                return;
            }

            // Extraer información del token
            Long userId = jwtTokenUtil.getUserIdFromToken(jwtToken);
            String email = jwtTokenUtil.getEmailFromToken(jwtToken);
            String role = jwtTokenUtil.getRoleFromToken(jwtToken);

            // Crear autenticación con el rol del usuario
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    email,
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority(role))
            );

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Establecer la autenticación en el contexto de seguridad
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // La petición es válida, continuar
            chain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            sendError(response, "Token expirado");
        } catch (Exception e) {
            sendError(response, "Error en la autenticación: " + e.getMessage());
        }
    }

    private boolean isPublicRoute(String path) {
        return PUBLIC_ROUTES.stream().anyMatch(path::startsWith);
    }

    private void sendError(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");

        ObjectMapper mapper = new ObjectMapper();
        LoginResponse errorResponse = new LoginResponse(null, null, null, null, message);

        response.getWriter().write(mapper.writeValueAsString(errorResponse));
    }
}
