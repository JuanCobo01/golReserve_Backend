package com.golReserve.gol.service;

import com.golReserve.gol.entity.Usuario;
import com.golReserve.gol.repository.UsuarioRepository;
import com.golReserve.gol.security.EncryptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de migración para cifrar datos existentes
 * Se ejecuta al iniciar la aplicación (solo una vez)
 * Activar con: spring.profiles.active=migrate-encryption
 */
@Component
@Profile("migrate-encryption")
public class DataEncryptionMigrationService implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataEncryptionMigrationService.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EncryptionService encryptionService;

    @Override
    @Transactional
    public void run(String... args) {
        logger.info("=== INICIANDO MIGRACIÓN DE CIFRADO DE DATOS ===");
        
        try {
            migrateUsuarios();
            logger.info("=== MIGRACIÓN COMPLETADA EXITOSAMENTE ===");
        } catch (Exception e) {
            logger.error("=== ERROR EN LA MIGRACIÓN ===", e);
            throw new RuntimeException("Fallo en la migración de cifrado", e);
        }
    }

    private void migrateUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        int total = usuarios.size();
        int migrated = 0;
        int skipped = 0;

        logger.info("Encontrados {} usuarios para revisar", total);

        for (Usuario usuario : usuarios) {
            boolean needsUpdate = false;

            // Verificar y cifrar cédula si no está cifrada
            if (usuario.getCedula() != null && !encryptionService.isEncrypted(usuario.getCedula())) {
                logger.debug("Cifrando cédula del usuario ID: {}", usuario.getIdUsuario());
                String cedulaOriginal = usuario.getCedula();
                usuario.setCedula(encryptionService.encrypt(cedulaOriginal));
                needsUpdate = true;
            }

            // Verificar y cifrar teléfono si no está cifrado
            if (usuario.getTelefono() != null && !encryptionService.isEncrypted(usuario.getTelefono())) {
                logger.debug("Cifrando teléfono del usuario ID: {}", usuario.getIdUsuario());
                String telefonoOriginal = usuario.getTelefono();
                usuario.setTelefono(encryptionService.encrypt(telefonoOriginal));
                needsUpdate = true;
            }

            if (needsUpdate) {
                usuarioRepository.save(usuario);
                migrated++;
                logger.info("Usuario ID {} migrado exitosamente", usuario.getIdUsuario());
            } else {
                skipped++;
                logger.debug("Usuario ID {} ya tiene datos cifrados, omitiendo", usuario.getIdUsuario());
            }
        }

        logger.info("Migración completada: {} usuarios migrados, {} omitidos", migrated, skipped);
    }
}
