package com.golReserve.gol.security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Base64;

/**
 * Servicio de cifrado utilizando ChaCha20-Poly1305
 * Proporciona cifrado autenticado (AEAD) para datos sensibles
 */
@Service
public class EncryptionService {

    // Clave maestra para cifrado (debe estar en variables de entorno en producción)
    @Value("${encryption.master.key:gR3$tK3y!2024#S3cur3K3yF0rCh4Ch420P0ly1305Encrypt10n}")
    private String masterKeyString;

    private static final String ALGORITHM = "ChaCha20-Poly1305";
    private static final int NONCE_LENGTH = 12; // 96 bits recomendado para ChaCha20-Poly1305
    private static final int TAG_LENGTH = 128; // 128 bits de autenticación

    // Inicializar Bouncy Castle Provider
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * Cifra un texto plano usando ChaCha20-Poly1305
     * 
     * @param plainText Texto a cifrar
     * @return Texto cifrado en Base64 (nonce + ciphertext)
     */
    public String encrypt(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            return null;
        }

        try {
            // Generar clave desde la clave maestra
            SecretKey secretKey = generateKey();

            // Generar nonce aleatorio
            byte[] nonce = generateNonce();

            // Inicializar cifrador
            Cipher cipher = Cipher.getInstance(ALGORITHM, "BC");
            GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_LENGTH, nonce);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

            // Cifrar
            byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            // Combinar nonce + ciphertext
            ByteBuffer byteBuffer = ByteBuffer.allocate(nonce.length + cipherText.length);
            byteBuffer.put(nonce);
            byteBuffer.put(cipherText);

            // Retornar en Base64
            return Base64.getEncoder().encodeToString(byteBuffer.array());

        } catch (Exception e) {
            throw new RuntimeException("Error al cifrar datos: " + e.getMessage(), e);
        }
    }

    /**
     * Descifra un texto cifrado con ChaCha20-Poly1305
     * 
     * @param encryptedText Texto cifrado en Base64
     * @return Texto plano descifrado
     */
    public String decrypt(String encryptedText) {
        if (encryptedText == null || encryptedText.isEmpty()) {
            return null;
        }

        try {
            // Decodificar de Base64
            byte[] decodedData = Base64.getDecoder().decode(encryptedText);

            // Extraer nonce y ciphertext
            ByteBuffer byteBuffer = ByteBuffer.wrap(decodedData);
            byte[] nonce = new byte[NONCE_LENGTH];
            byteBuffer.get(nonce);

            byte[] cipherText = new byte[byteBuffer.remaining()];
            byteBuffer.get(cipherText);

            // Generar clave desde la clave maestra
            SecretKey secretKey = generateKey();

            // Inicializar cifrador
            Cipher cipher = Cipher.getInstance(ALGORITHM, "BC");
            GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_LENGTH, nonce);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);

            // Descifrar y verificar autenticación
            byte[] plainText = cipher.doFinal(cipherText);

            return new String(plainText, StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new RuntimeException("Error al descifrar datos: " + e.getMessage(), e);
        }
    }

    /**
     * Genera una clave SecretKey desde la clave maestra
     */
    private SecretKey generateKey() {
        try {
            // Usar los primeros 32 bytes de la clave maestra (256 bits)
            byte[] keyBytes = masterKeyString.getBytes(StandardCharsets.UTF_8);
            byte[] key = new byte[32]; // ChaCha20 usa claves de 256 bits
            System.arraycopy(keyBytes, 0, key, 0, Math.min(keyBytes.length, 32));
            
            return new SecretKeySpec(key, "ChaCha20");
        } catch (Exception e) {
            throw new RuntimeException("Error al generar clave: " + e.getMessage(), e);
        }
    }

    /**
     * Genera un nonce aleatorio de 96 bits
     */
    private byte[] generateNonce() {
        byte[] nonce = new byte[NONCE_LENGTH];
        new SecureRandom().nextBytes(nonce);
        return nonce;
    }

    /**
     * Verifica si un texto está cifrado (formato Base64 válido)
     */
    public boolean isEncrypted(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        try {
            byte[] decoded = Base64.getDecoder().decode(text);
            return decoded.length > NONCE_LENGTH;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
