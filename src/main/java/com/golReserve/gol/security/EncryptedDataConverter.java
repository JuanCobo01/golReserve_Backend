package com.golReserve.gol.security;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Convertidor JPA para cifrar/descifrar automáticamente datos sensibles
 * Utiliza ChaCha20-Poly1305 para cifrado autenticado
 */
@Converter
@Component
public class EncryptedDataConverter implements AttributeConverter<String, String> {

    private static EncryptionService encryptionService;

    @Autowired
    public void setEncryptionService(EncryptionService service) {
        EncryptedDataConverter.encryptionService = service;
    }

    /**
     * Cifra el dato antes de guardarlo en la base de datos
     */
    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }
        // Si ya está cifrado, no volver a cifrar
        if (encryptionService != null && encryptionService.isEncrypted(attribute)) {
            return attribute;
        }
        return encryptionService != null ? encryptionService.encrypt(attribute) : attribute;
    }

    /**
     * Descifra el dato al leerlo de la base de datos
     */
    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        // Solo descifrar si parece estar cifrado
        if (encryptionService != null && encryptionService.isEncrypted(dbData)) {
            return encryptionService.decrypt(dbData);
        }
        return dbData;
    }
}
