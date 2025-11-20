package com.golReserve.gol.config;

import com.golReserve.gol.security.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Permitir peticiones OPTIONS para CORS preflight
                .requestMatchers(request -> "OPTIONS".equals(request.getMethod())).permitAll()

                // Endpoints públicos - sin autenticación
                .requestMatchers("/api/usuarios/login",
                                "/api/usuarios/registrar",
                                "/api/health",
                                "/api/test").permitAll()

                // Endpoints de consulta para CLIENTES y superiores
                .requestMatchers("/api/establecimiento/listar",
                                "/api/establecimiento/buscar/**",
                                "/api/cancha/listar",
                                "/api/cancha/buscar/**").hasAnyRole("CLIENTE", "ADMINISTRADOR", "SUPER_ADMINISTRADOR")

                // Gestión de usuarios - solo SUPER_ADMINISTRADOR
                .requestMatchers("/api/usuarios/listar",
                                "/api/usuarios/buscar/**",
                                "/api/usuarios/actualizar/**").hasRole("SUPER_ADMINISTRADOR")

                // Gestión de establecimientos - ADMINISTRADOR y SUPER_ADMINISTRADOR
                .requestMatchers("/api/establecimiento/registrar",
                                "/api/establecimiento/actualizar/**",
                                "/api/establecimiento/eliminar/**",
                                "/api/establecimiento/mis-establecimientos").hasAnyRole("ADMINISTRADOR", "SUPER_ADMINISTRADOR")

                // Gestión de canchas - ADMINISTRADOR y SUPER_ADMINISTRADOR
                .requestMatchers("/api/cancha/registrar").hasAnyRole("ADMINISTRADOR", "SUPER_ADMINISTRADOR")

                // Gestión de reservas - ORDEN IMPORTANTE: más específico primero
                // Permitir GET para consultas
                .requestMatchers(HttpMethod.GET, "/api/reservas/usuario/**").hasAnyRole("CLIENTE", "ADMINISTRADOR", "SUPER_ADMINISTRADOR")
                .requestMatchers(HttpMethod.GET, "/api/reservas/mis-reservas").hasAnyRole("CLIENTE", "ADMINISTRADOR", "SUPER_ADMINISTRADOR")
                .requestMatchers(HttpMethod.GET, "/api/reservas/buscar/**").hasAnyRole("CLIENTE", "ADMINISTRADOR", "SUPER_ADMINISTRADOR")
                .requestMatchers(HttpMethod.GET, "/api/reservas/compartir/**").hasAnyRole("CLIENTE", "ADMINISTRADOR", "SUPER_ADMINISTRADOR")
                .requestMatchers(HttpMethod.GET, "/api/reservas/verificar-disponibilidad").hasAnyRole("CLIENTE", "ADMINISTRADOR", "SUPER_ADMINISTRADOR")
                .requestMatchers(HttpMethod.GET, "/api/reservas/listar").hasAnyRole("ADMINISTRADOR", "SUPER_ADMINISTRADOR")
                
                // Permitir POST para crear y cancelar
                .requestMatchers(HttpMethod.POST, "/api/reservas/registrar").hasAnyRole("CLIENTE", "ADMINISTRADOR", "SUPER_ADMINISTRADOR")
                .requestMatchers(HttpMethod.POST, "/api/reservas/crear").hasAnyRole("CLIENTE", "ADMINISTRADOR", "SUPER_ADMINISTRADOR")
                .requestMatchers(HttpMethod.POST, "/api/reservas/cancelar/**").hasAnyRole("CLIENTE", "ADMINISTRADOR", "SUPER_ADMINISTRADOR")
                .requestMatchers(HttpMethod.POST, "/api/reservas/confirmar/**").hasAnyRole("ADMINISTRADOR", "SUPER_ADMINISTRADOR")
                
                // Permitir PUT para actualizar y modificar - CLIENTE puede modificar sus propias reservas
                .requestMatchers(HttpMethod.PUT, "/api/reservas/**").hasAnyRole("CLIENTE", "ADMINISTRADOR", "SUPER_ADMINISTRADOR")
                
                // Permitir DELETE solo para admins
                .requestMatchers(HttpMethod.DELETE, "/api/reservas/eliminar/**").hasAnyRole("ADMINISTRADOR", "SUPER_ADMINISTRADOR")

                // Cualquier otra petición requiere autenticación
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
