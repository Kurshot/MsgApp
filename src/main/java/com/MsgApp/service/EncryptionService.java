package com.MsgApp.service;



import com.MsgApp.exception.EncryptionException;
import org.springframework.stereotype.Service;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

@Service
public class EncryptionService {
    private static final String ALGORITHM = "AES";
    private static final int KEY_SIZE = 256;

    // Mesajı şifrele
    public String encrypt(String content, String publicKey) {
        try {
            SecretKey secretKey = generateSecretKeyFromPublicKey(publicKey);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] encryptedBytes = cipher.doFinal(content.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new EncryptionException("Error encrypting message", e);
        }
    }

    // Mesajı çöz
    public String decrypt(String encryptedContent, String privateKey) {
        try {
            SecretKey secretKey = generateSecretKeyFromPrivateKey(privateKey);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedContent));
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new EncryptionException("Error decrypting message", e);
        }
    }

    // Yeni anahtar çifti oluştur (kullanıcı kaydı sırasında kullanılacak)
    public KeyPair generateKeyPair() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
            keyGen.init(KEY_SIZE);
            SecretKey secretKey = keyGen.generateKey();

            // Public ve private key'leri Base64 formatında döndür
            String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
            return new KeyPair(encodedKey, encodedKey);
        } catch (NoSuchAlgorithmException e) {
            throw new EncryptionException("Error generating key pair", e);
        }
    }

    // Public key'den SecretKey oluştur
    private SecretKey generateSecretKeyFromPublicKey(String publicKey) {
        byte[] decodedKey = Base64.getDecoder().decode(publicKey);
        return new SecretKeySpec(decodedKey, ALGORITHM);
    }

    // Private key'den SecretKey oluştur
    private SecretKey generateSecretKeyFromPrivateKey(String privateKey) {
        byte[] decodedKey = Base64.getDecoder().decode(privateKey);
        return new SecretKeySpec(decodedKey, ALGORITHM);
    }

    // Anahtar çifti için inner class
    public static class KeyPair {
        private final String publicKey;
        private final String privateKey;

        public KeyPair(String publicKey, String privateKey) {
            this.publicKey = publicKey;
            this.privateKey = privateKey;
        }

        public String getPublicKey() {
            return publicKey;
        }

        public String getPrivateKey() {
            return privateKey;
        }
    }
}
