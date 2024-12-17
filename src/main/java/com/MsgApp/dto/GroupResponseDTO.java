package com.MsgApp.dto;

import lombok.Data;

import java.util.Set;

@Data
public class GroupResponseDTO {
    private Long id;
    private String name;
    private String creatorUsername;
    private Set<String> memberUsernames;
    private int memberCount;
    private String createdAt;
}
