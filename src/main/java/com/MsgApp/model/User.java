package com.MsgApp.model;

import com.MsgApp.service.EncryptionService;
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
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    // Şifreleme için gerekli alanlar
    @Column(name = "public_key", columnDefinition = "TEXT")
    private String publicKey;

    @Column(name = "private_key", columnDefinition = "TEXT")
    private String privateKey;

    @Column(nullable = false)
    private boolean online = false;

    @Column(name = "last_seen")
    private LocalDateTime lastSeen;

    @ManyToMany(mappedBy = "members")
    private Set<ChatGroup> groups = new HashSet<>();

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
    private Set<Message> sentMessages = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "message_recipients",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "message_id")
    )
    private Set<Message> receivedMessages = new HashSet<>();

    // Kullanıcı oluşturulduğunda anahtar çifti oluşturulmalı
    @PrePersist
    protected void onCreate() {
        if (publicKey == null || privateKey == null) {
            generateKeyPair();
        }
        lastSeen = LocalDateTime.now();
    }

    // Anahtar çifti oluşturma metodu
    private void generateKeyPair() {
        // Bu metod, UserService üzerinden EncryptionService'i kullanarak
        // anahtar çiftini oluşturacak şekilde güncellenecek
        EncryptionService.KeyPair keyPair = new EncryptionService().generateKeyPair();
        this.publicKey = keyPair.getPublicKey();
        this.privateKey = keyPair.getPrivateKey();
    }

    // Public key getter metodu
    public String getPublicKey() {
        return this.publicKey;
    }

    // Private key getter metodu - dikkatli kullanılmalı!
    public String getPrivateKey() {
        return this.privateKey;
    }
}