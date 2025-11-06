package com.golReserve.gol.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configure(http))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos - sin autenticación
                .requestMatchers("/api/usuarios/login",
                                "/api/usuarios/registrar",
                                "/api/health",
                                "/api/test").permitAll()

                // Endpoints de consulta para CLIENTES y superiores
                .requestMatchers("/api/establecimiento/listar",
                                "/api/establecimiento/buscar/**",
                                "/api/cancha/listar",
                                "/api/cancha/buscar/**").hasAnyAuthority("CLIENTE", "ADMINISTRADOR", "SUPER_ADMINISTRADOR")

                // Gestión de usuarios - solo SUPER_ADMINISTRADOR
                .requestMatchers("/api/usuarios/listar",
                                "/api/usuarios/buscar/**",
                                "/api/usuarios/actualizar/**").hasAuthority("SUPER_ADMINISTRADOR")

                // Gestión de establecimientos - ADMINISTRADOR y SUPER_ADMINISTRADOR
                .requestMatchers("/api/establecimiento/registrar",
                                "/api/establecimiento/actualizar/**",
                                "/api/establecimiento/eliminar/**",
                                "/api/establecimiento/mis-establecimientos").hasAnyAuthority("ADMINISTRADOR", "SUPER_ADMINISTRADOR")

                // Gestión de canchas - ADMINISTRADOR y SUPER_ADMINISTRADOR
                .requestMatchers("/api/cancha/registrar").hasAnyAuthority("ADMINISTRADOR", "SUPER_ADMINISTRADOR")

                // Gestión de reservas
                .requestMatchers("/api/reserva/registrar").hasAnyAuthority("CLIENTE", "ADMINISTRADOR", "SUPER_ADMINISTRADOR")
                .requestMatchers("/api/reserva/listar").hasAnyAuthority("ADMINISTRADOR", "SUPER_ADMINISTRADOR")
                .requestMatchers("/api/reserva/mis-reservas").hasAnyAuthority("CLIENTE", "ADMINISTRADOR", "SUPER_ADMINISTRADOR")
                .requestMatchers("/api/reserva/buscar/usuario/**").hasAnyAuthority("CLIENTE", "ADMINISTRADOR", "SUPER_ADMINISTRADOR")
                .requestMatchers("/api/reserva/buscar/cancha/**").hasAnyAuthority("ADMINISTRADOR", "SUPER_ADMINISTRADOR")
                .requestMatchers("/api/reserva/confirmar/**").hasAnyAuthority("ADMINISTRADOR", "SUPER_ADMINISTRADOR")
                .requestMatchers("/api/reserva/cancelar/**",
                                "/api/reserva/modificar/**").hasAnyAuthority("CLIENTE", "ADMINISTRADOR", "SUPER_ADMINISTRADOR")
                .requestMatchers("/api/reserva/compartir/**").hasAnyAuthority("CLIENTE", "ADMINISTRADOR", "SUPER_ADMINISTRADOR")
                .requestMatchers("/api/reserva/actualizar/**",
                                "/api/reserva/eliminar/**").hasAnyAuthority("ADMINISTRADOR", "SUPER_ADMINISTRADOR")

                // Cualquier otra petición requiere autenticación
                .anyRequest().authenticated()
            );

        return http.build();
    }
}
