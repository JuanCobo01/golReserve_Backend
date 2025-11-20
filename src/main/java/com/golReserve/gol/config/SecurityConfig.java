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
            .cors(cors -> cors.configure(http))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Permitir peticiones OPTIONS para CORS preflight
                .requestMatchers(request -> "OPTIONS".equals(request.getMethod())).permitAll()

                // ========== ENDPOINTS PÚBLICOS ==========
                .requestMatchers("/api/usuarios/login",
                                "/api/usuarios/registrar",
                                "/api/health",
                                "/api/test").permitAll()

                // ========== SUPER ADMINISTRADOR ==========
                .requestMatchers("/api/super-admin/**").hasRole("SUPER_ADMINISTRADOR")

                // ========== ADMINISTRADOR ==========
                .requestMatchers("/api/admin/**").hasRole("ADMINISTRADOR")

                // ========== CLIENTE ==========
                .requestMatchers("/api/cliente/**").hasRole("CLIENTE")

                // ========== ENDPOINTS COMPARTIDOS ==========
                // Consultas de establecimientos y canchas (todos los roles autenticados)
                .requestMatchers(HttpMethod.GET, "/api/establecimiento/listar",
                                "/api/establecimiento/buscar/**",
                                "/api/cancha/listar",
                                "/api/cancha/buscar/**")
                        .hasAnyRole("CLIENTE", "ADMINISTRADOR", "SUPER_ADMINISTRADOR")

                // Gestión de establecimientos - ADMINISTRADOR y SUPER_ADMINISTRADOR
                .requestMatchers("/api/establecimiento/registrar",
                                "/api/establecimiento/actualizar/**",
                                "/api/establecimiento/eliminar/**",
                                "/api/establecimiento/mis-establecimientos")
                        .hasAnyRole("ADMINISTRADOR", "SUPER_ADMINISTRADOR")

                // Gestión de canchas - ADMINISTRADOR y SUPER_ADMINISTRADOR
                .requestMatchers("/api/cancha/registrar")
                        .hasAnyRole("ADMINISTRADOR", "SUPER_ADMINISTRADOR")

                // Gestión de reservas - ORDEN IMPORTANTE: más específico primero
                .requestMatchers(HttpMethod.GET, "/api/reservas/usuario/**",
                                "/api/reservas/mis-reservas",
                                "/api/reservas/buscar/**",
                                "/api/reservas/compartir/**",
                                "/api/reservas/verificar-disponibilidad")
                        .hasAnyRole("CLIENTE", "ADMINISTRADOR", "SUPER_ADMINISTRADOR")
                
                .requestMatchers(HttpMethod.GET, "/api/reservas/listar")
                        .hasAnyRole("ADMINISTRADOR", "SUPER_ADMINISTRADOR")
                
                .requestMatchers(HttpMethod.POST, "/api/reservas/registrar",
                                "/api/reservas/crear",
                                "/api/reservas/cancelar/**")
                        .hasAnyRole("CLIENTE", "ADMINISTRADOR", "SUPER_ADMINISTRADOR")
                
                .requestMatchers(HttpMethod.POST, "/api/reservas/confirmar/**")
                        .hasAnyRole("ADMINISTRADOR", "SUPER_ADMINISTRADOR")
                
                .requestMatchers(HttpMethod.PUT, "/api/reservas/**")
                        .hasAnyRole("CLIENTE", "ADMINISTRADOR", "SUPER_ADMINISTRADOR")
                
                .requestMatchers(HttpMethod.DELETE, "/api/reservas/eliminar/**")
                        .hasAnyRole("ADMINISTRADOR", "SUPER_ADMINISTRADOR")

                // Cualquier otra petición requiere autenticación
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
