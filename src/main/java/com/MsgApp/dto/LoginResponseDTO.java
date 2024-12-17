package com.MsgApp.dto;

import lombok.Data;

@Data
public class LoginResponseDTO {
    private String token;
    private String username;
    private Long userId;
}
