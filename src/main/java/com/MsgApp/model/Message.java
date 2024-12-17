package com.MsgApp.model;


import com.MsgApp.enums.MessageStatus;
import com.MsgApp.enums.MessageType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Mesajı gönderen kullanıcı
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    // Şifrelenmiş mesaj içeriği
    @Column(nullable = false, columnDefinition = "TEXT")
    private String encryptedContent;

    // Mesajın SHA256 hash'i
    @Column(nullable = false)
    private String contentHash;

    // Mesaj türü (PRIVATE, GROUP, BROADCAST)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType type;

    // Mesajın gönderildiği grup (eğer grup mesajıysa)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private ChatGroup group;

    // Mesajın alıcıları
    @ManyToMany(mappedBy = "receivedMessages")
    private Set<User> recipients = new HashSet<>();

    // Mesajın gönderilme zamanı
    @Column(nullable = false)
    private LocalDateTime timestamp;

    // Mesajın okunma durumu
    @Column(nullable = false)
    private boolean read = false;

    // Mesajın iletilme durumu
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageStatus status = MessageStatus.SENT;

    // Ön yükleme için constructor
    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
}