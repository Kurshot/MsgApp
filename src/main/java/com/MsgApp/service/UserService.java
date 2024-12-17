package com.MsgApp.service;

import com.MsgApp.model.User;
import com.MsgApp.repository.UserRepository;
import com.MsgApp.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    // Dependency injection için final alanlar
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EncryptionService encryptionService;

    /**
     * Yeni kullanıcı kaydı yapar.
     * - Kullanıcı adı ve email benzersizliğini kontrol eder
     * - Şifreyi güvenli bir şekilde hashler
     * - Şifreleme için anahtar çifti oluşturur
     */
    @Transactional
    public User registerUser(User user) {
        // Kullanıcı adı veya email zaten varsa hata fırlat
        if (userRepository.existsByUsernameOrEmail(user.getUsername(), user.getEmail())) {
            throw new UsernameAlreadyExistsException("Kullanıcı adı veya email zaten kullanımda");
        }

        // Şifreyi hashle
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Uçtan uca şifreleme için anahtar çifti oluştur
        EncryptionService.KeyPair keyPair = encryptionService.generateKeyPair();
        user.setPublicKey(keyPair.getPublicKey());
        user.setPrivateKey(keyPair.getPrivateKey());

        // Başlangıç değerlerini ayarla
        user.setOnline(false);
        user.setLastSeen(LocalDateTime.now());

        return userRepository.save(user);
    }

    /**
     * Kullanıcı girişi yapar ve çevrimiçi durumunu günceller.
     * Başarısız giriş denemelerinde uygun hataları fırlatır.
     */
    @Transactional
    public User login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Kullanıcı bulunamadı"));

        // Şifre kontrolü
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Geçersiz şifre");
        }

        // Kullanıcıyı çevrimiçi yap
        user.setOnline(true);
        user.setLastSeen(LocalDateTime.now());
        return userRepository.save(user);
    }

    /**
     * Kullanıcı çıkışı yapar ve son görülme zamanını günceller
     */
    @Transactional
    public void logout(Long userId) {
        userRepository.updateUserStatus(userId, false, LocalDateTime.now());
    }

    /**
     * Kullanıcı profili güncelleme
     * Sadece belirli alanların güncellenebilmesini sağlar
     */
    @Transactional
    public User updateUser(Long userId, User updatedUser) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Kullanıcı bulunamadı"));

        // Sadece güvenli alanları güncelle
        if (updatedUser.getEmail() != null) {
            existingUser.setEmail(updatedUser.getEmail());
        }

        // Şifre güncellemesi varsa, yeni şifreyi hashle
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        return userRepository.save(existingUser);
    }

    /**
     * Çevrimiçi kullanıcıları listeler
     */
    public List<User> getOnlineUsers() {
        return userRepository.findByOnlineTrue();
    }

    /**
     * Kullanıcı arama fonksiyonu
     * Username veya email üzerinden arama yapar
     */
    public List<User> searchUsers(String searchTerm) {
        return userRepository.searchUsers(searchTerm);
    }

    /**
     * ID ile kullanıcı bulma
     */
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Kullanıcı bulunamadı: " + userId));
    }

    /**
     * Kullanıcı adı ile kullanıcı bulma
     */
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Kullanıcı bulunamadı: " + username));
    }

    /**
     * Kullanıcının çevrimiçi durumunu kontrol eder
     */
    public boolean isUserOnline(Long userId) {
        return userRepository.findById(userId)
                .map(User::isOnline)
                .orElse(false);
    }
}