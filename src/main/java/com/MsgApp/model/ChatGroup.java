package com.MsgApp.model;

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
@Table(name = "chat_groups")
public class ChatGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Grup adı
    @Column(nullable = false)
    private String name;

    // Grubu oluşturan kullanıcı
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    // Grup üyeleri (maksimum 5 kişi)
    @ManyToMany
    @JoinTable(
            name = "group_members",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> members = new HashSet<>();

    // Gruptaki mesajlar
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private Set<Message> messages = new HashSet<>();

    // Grubun oluşturulma zamanı
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // Ön yükleme için constructor
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Grup üye sayısı kontrolü için yardımcı metod
    public boolean canAddMember() {
        return members.size() < 5;
    }
}
