package com.MsgApp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF korumasını devre dışı bırakma (modern yaklaşım)
                .csrf(csrf -> csrf.disable())

                // HTTP isteklerini yetkilendirme
                .authorizeHttpRequests(authorize -> authorize
                        // /api/auth/** ile başlayan URL'lere herkes erişebilir
                        .requestMatchers("/api/auth/**").permitAll()
                        // Diğer tüm istekler için kimlik doğrulama gerekli
                        .anyRequest().authenticated()
                )

                // Basic authentication'ı etkinleştirme
                .httpBasic(basic -> basic.disable());

        return http.build();
    }
}