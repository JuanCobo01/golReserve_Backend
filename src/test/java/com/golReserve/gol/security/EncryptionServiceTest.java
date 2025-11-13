package com.golReserve.gol.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para el servicio de cifrado ChaCha20-Poly1305
 */
@SpringBootTest
class EncryptionServiceTest {

    @Autowired
    private EncryptionService encryptionService;

    @Test
    void testEncryptDecrypt() {
        // Datos de prueba
        String originalCedula = "1234567890";
        String originalTelefono = "3001234567";

        // Cifrar
        String cedulaCifrada = encryptionService.encrypt(originalCedula);
        String telefonoCifrado = encryptionService.encrypt(originalTelefono);

        // Verificar que se cifraron
        assertNotNull(cedulaCifrada);
        assertNotNull(telefonoCifrado);
        assertNotEquals(originalCedula, cedulaCifrada);
        assertNotEquals(originalTelefono, telefonoCifrado);

        // Descifrar
        String cedulaDescifrada = encryptionService.decrypt(cedulaCifrada);
        String telefonoDescifrado = encryptionService.decrypt(telefonoCifrado);

        // Verificar que se descifraron correctamente
        assertEquals(originalCedula, cedulaDescifrada);
        assertEquals(originalTelefono, telefonoDescifrado);
    }

    @Test
    void testEncryptNull() {
        String result = encryptionService.encrypt(null);
        assertNull(result);
    }

    @Test
    void testDecryptNull() {
        String result = encryptionService.decrypt(null);
        assertNull(result);
    }

    @Test
    void testEncryptEmpty() {
        String result = encryptionService.encrypt("");
        assertNull(result);
    }

    @Test
    void testIsEncrypted() {
        String original = "1234567890";
        String encrypted = encryptionService.encrypt(original);

        assertFalse(encryptionService.isEncrypted(original));
        assertTrue(encryptionService.isEncrypted(encrypted));
    }

    @Test
    void testDifferentEncryptions() {
        // El mismo dato debe producir diferentes cifrados (por el nonce aleatorio)
        String original = "1234567890";
        String encrypted1 = encryptionService.encrypt(original);
        String encrypted2 = encryptionService.encrypt(original);

        assertNotEquals(encrypted1, encrypted2);

        // Pero ambos deben descifrar al mismo valor
        assertEquals(original, encryptionService.decrypt(encrypted1));
        assertEquals(original, encryptionService.decrypt(encrypted2));
    }

    @Test
    void testLongData() {
        // Probar con datos largos
        String longData = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. ".repeat(10);
        String encrypted = encryptionService.encrypt(longData);
        String decrypted = encryptionService.decrypt(encrypted);

        assertEquals(longData, decrypted);
    }

    @Test
    void testSpecialCharacters() {
        // Probar con caracteres especiales
        String specialData = "¡Hola! ñ@#$%^&*()_+-={}[]|:;<>?,./";
        String encrypted = encryptionService.encrypt(specialData);
        String decrypted = encryptionService.decrypt(encrypted);

        assertEquals(specialData, decrypted);
    }

    @Test
    void testInvalidDecryption() {
        // Intentar descifrar datos inválidos debe lanzar excepción
        assertThrows(RuntimeException.class, () -> {
            encryptionService.decrypt("invalid-base64-data");
        });
    }
}
