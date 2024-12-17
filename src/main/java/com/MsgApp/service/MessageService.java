package com.MsgApp.service;

import com.MsgApp.enums.MessageType;
import com.MsgApp.exception.MessageException;
import com.MsgApp.exception.UserNotFoundException;
import com.MsgApp.model.*;
import com.MsgApp.repository.MessageRepository;
import com.MsgApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final EncryptionService encryptionService;
    private final HashingService hashingService;  // Yeni eklenen servis

    /**
     * Özel mesaj gönderme işlemi.
     * 1. Önce mesajın SHA-256 hash'i hesaplanır (orijinal mesaj üzerinden)
     * 2. Sonra mesaj alıcının public key'i ile şifrelenir
     * 3. Hem şifrelenmiş mesaj hem de hash veritabanına kaydedilir
     */
    @Transactional
    public Message sendPrivateMessage(Long senderId, Long recipientId, String content) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new UserNotFoundException("Gönderen bulunamadı"));
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new UserNotFoundException("Alıcı bulunamadı"));

        // 1. Mesajın hash'ini hesapla (şifrelemeden önce)
        String contentHash = hashingService.calculateSHA256Hash(content);

        // 2. Mesajı şifrele
        String encryptedContent = encryptionService.encrypt(content, recipient.getPublicKey());

        // 3. Mesaj nesnesini oluştur
        Message message = new Message();
        message.setSender(sender);
        message.setEncryptedContent(encryptedContent);
        message.setContentHash(contentHash);  // Hash'i kaydet
        message.setType(MessageType.PRIVATE);
        message.getRecipients().add(recipient);
        message.setTimestamp(LocalDateTime.now());

        return messageRepository.save(message);
    }

    /**
     * Mesaj bütünlüğünü doğrula.
     * Bu metod, bir mesajın iletim sırasında değiştirilip değiştirilmediğini kontrol eder.
     */
    public boolean verifyMessageIntegrity(Message message, String decryptedContent) {
        // Şifresi çözülmüş mesajın hash'ini hesapla ve kaydedilen hash ile karşılaştır
        return hashingService.verifyHash(decryptedContent, message.getContentHash());
    }

    /**
     * Mesajı al ve doğrula.
     * Bu metod, bir mesajı alır, şifresini çözer ve bütünlüğünü kontrol eder.
     */
    public String receiveAndVerifyMessage(Message message, String privateKey) {
        // 1. Mesajın şifresini çöz
        String decryptedContent = encryptionService.decrypt(
                message.getEncryptedContent(),
                privateKey
        );

        // 2. Mesajın bütünlüğünü kontrol et
        if (!verifyMessageIntegrity(message, decryptedContent)) {
            throw new MessageException("Mesaj bütünlüğü bozulmuş olabilir!");
        }

        return decryptedContent;
    }

}