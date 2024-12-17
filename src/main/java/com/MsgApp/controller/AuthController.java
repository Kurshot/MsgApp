package com.MsgApp.controller;

import com.MsgApp.dto.LoginRequestDTO;
import com.MsgApp.dto.LoginResponseDTO;
import com.MsgApp.dto.RegisterRequestDTO;
import com.MsgApp.model.User;
import com.MsgApp.security.JwtTokenProvider;
import com.MsgApp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequestDTO request) {
        // Yeni kullanıcı oluştur
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        User registeredUser = userService.registerUser(user);

        // JWT token oluştur
        String token = jwtTokenProvider.generateToken(registeredUser.getUsername());

        LoginResponseDTO response = new LoginResponseDTO();
        response.setToken(token);
        response.setUsername(registeredUser.getUsername());
        response.setUserId(registeredUser.getId());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequestDTO request) {
        // Kullanıcı doğrulama
        User user = userService.login(request.getUsername(), request.getPassword());

        // JWT token oluştur
        String token = jwtTokenProvider.generateToken(user.getUsername());

        LoginResponseDTO response = new LoginResponseDTO();
        response.setToken(token);
        response.setUsername(user.getUsername());
        response.setUserId(user.getId());

        return ResponseEntity.ok(response);
    }
}
