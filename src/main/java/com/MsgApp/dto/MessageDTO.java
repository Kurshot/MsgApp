package com.MsgApp.dto;

import com.MsgApp.enums.MessageType;
import lombok.Data;


@Data
public class MessageDTO {
    // Mesajı gönderen kullanıcının ID'si
    private Long senderId;

    // Mesajın içeriği
    private String content;

    // Alıcı kullanıcının ID'si (özel mesajlar için)
    private Long recipientId;

    // Mesajın gönderileceği grubun ID'si (grup mesajları için)
    private Long groupId;

    // Mesajın tipi (PRIVATE, GROUP, veya BROADCAST)
    private MessageType type;

    // İsteğe bağlı olarak eklenebilecek ek alanlar
    private String timestamp;  // Mesajın gönderilme zamanı
    private boolean encrypted; // Mesajın şifrelenip şifrelenmediği
}