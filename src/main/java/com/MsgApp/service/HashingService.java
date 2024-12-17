package com.MsgApp.service;

import com.MsgApp.exception.HashingException;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class HashingService {
    // SHA-256 algoritmasının adını sabit olarak tanımlıyoruz
    private static final String HASH_ALGORITHM = "SHA-256";

    /**
     * Verilen mesajın SHA-256 hash'ini hesaplar.
     * Hash işlemi şu adımlardan oluşur:
     * 1. Mesaj byte dizisine çevrilir
     * 2. SHA-256 algoritması ile hash hesaplanır
     * 3. Oluşan byte dizisi hexadecimal string'e çevrilir
     *
     * @param content Hash'i hesaplanacak içerik
     * @return Hexadecimal formatta SHA-256 hash string'i
     * @throws HashingException Hash hesaplama sırasında bir hata oluşursa
     */
    public String calculateSHA256Hash(String content) {
        try {
            // SHA-256 için MessageDigest örneği oluştur
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);

            // Mesajı UTF-8 byte dizisine çevir ve hash'i hesapla
            byte[] hashBytes = digest.digest(content.getBytes(StandardCharsets.UTF_8));

            // Byte dizisini hexadecimal string'e çevir
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                // Her byte'ı 2 haneli hexadecimal'e çevir
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new HashingException("SHA-256 hash hesaplama hatası", e);
        }
    }

    /**
     * Bir mesajın hash'inin doğruluğunu kontrol eder.
     * Bu metod, bir mesajın değiştirilip değiştirilmediğini anlamak için kullanılır.
     *
     * @param content Kontrol edilecek mesaj
     * @param storedHash Veritabanında saklanan hash
     * @return Hash'ler eşleşiyorsa true, eşleşmiyorsa false
     */
    public boolean verifyHash(String content, String storedHash) {
        String calculatedHash = calculateSHA256Hash(content);
        return calculatedHash.equals(storedHash);
    }
}