package com.example.sistemauniversalacesso.utils;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
public class PasswordUtils {
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    // Gerar salt aleatório
    private static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    // Gerar hash da senha usando PBKDF2
    public static String hashPassword(String password, String salt) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(),
                    ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] hash = factory.generateSecret(spec).getEncoded();

            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Erro ao gerar hash da senha", e);
        }
    }
    // Criar um hash de senha seguro com salt
    public static String generateSecurePassword(String password) {
        String salt = generateSalt();
        String hashedPassword = hashPassword(password, salt);
        return salt + ":" + hashedPassword; // Salvar salt e hash juntos
    }
    // Verificar senha
    public static boolean verifyPassword(String inputPassword, String storedPassword) {
        String[] parts = storedPassword.split(":");
        if (parts.length != 2) return false;
        String salt = parts[0];
        String hashOfInput = hashPassword(inputPassword, salt);
        return hashOfInput.equals(parts[1]);
    }
}