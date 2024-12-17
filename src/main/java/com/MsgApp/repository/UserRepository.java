package com.MsgApp.repository;

import com.MsgApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Kullanıcı adına göre kullanıcı bulma
    Optional<User> findByUsername(String username);

    // Email adresine göre kullanıcı bulma
    Optional<User> findByEmail(String email);

    // Çevrimiçi kullanıcıları bulma
    List<User> findByOnlineTrue();

    // Kullanıcı adı veya email ile var olup olmadığını kontrol etme
    boolean existsByUsernameOrEmail(String username, String email);

    // Kullanıcının çevrimiçi durumunu güncelleme
    @Modifying
    @Query("UPDATE User u SET u.online = :status, u.lastSeen = :lastSeen WHERE u.id = :userId")
    void updateUserStatus(Long userId, boolean status, LocalDateTime lastSeen);

    // Kullanıcı araması (username veya email ile)
    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<User> searchUsers(String searchTerm);
}
