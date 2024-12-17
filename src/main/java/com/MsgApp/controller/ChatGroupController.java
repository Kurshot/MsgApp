package com.MsgApp.controller;

import com.MsgApp.dto.CreateGroupDTO;
import com.MsgApp.dto.GroupResponseDTO;
import com.MsgApp.model.ChatGroup;
import com.MsgApp.model.User;
import com.MsgApp.service.ChatGroupService;
import com.MsgApp.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;


import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class ChatGroupController {
    private final ChatGroupService chatGroupService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserService userService;

    // Yeni grup oluşturma
    @PostMapping
    public ResponseEntity<GroupResponseDTO> createGroup(@Valid @RequestBody CreateGroupDTO createGroupDTO) {
        ChatGroup group = chatGroupService.createGroup(
                createGroupDTO.getName(),
                createGroupDTO.getCreatorId(),
                createGroupDTO.getMemberIds()
        );

        // Grup oluşturulduğunda tüm üyelere bildirim gönder
        group.getMembers().forEach(member -> {
            messagingTemplate.convertAndSendToUser(
                    member.getUsername(),
                    "/queue/group-notifications",
                    "Yeni gruba eklendininiz: " + group.getName()
            );
        });

        return ResponseEntity.ok(convertToDTO(group));
    }

    // Kullanıcının gruplarını getir
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<GroupResponseDTO>> getUserGroups(@PathVariable Long userId) {
        List<ChatGroup> groups = chatGroupService.getUserGroups(userId);
        List<GroupResponseDTO> groupDTOs = groups.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(groupDTOs);
    }

    // Gruba üye ekleme
    @PostMapping("/{groupId}/members/{userId}")
    public ResponseEntity<?> addMemberToGroup(
            @PathVariable Long groupId,
            @PathVariable Long userId) {

        // Grup üye sayısı kontrolü servis katmanında yapılıyor
        ChatGroup updatedGroup = chatGroupService.addMember(groupId, userId);

        // Yeni üyeye bildirim gönder
        User newMember = userService.getUserById(userId);
        messagingTemplate.convertAndSendToUser(
                newMember.getUsername(),
                "/queue/group-notifications",
                "Gruba eklendiniz: " + updatedGroup.getName()
        );

        return ResponseEntity.ok(convertToDTO(updatedGroup));
    }

    // Gruptan üye çıkarma
    @DeleteMapping("/{groupId}/members/{userId}")
    public ResponseEntity<?> removeMemberFromGroup(
            @PathVariable Long groupId,
            @PathVariable Long userId) {

        ChatGroup updatedGroup = chatGroupService.removeMember(groupId, userId);

        // Çıkarılan üyeye bildirim gönder
        User removedMember = userService.getUserById(userId);
        messagingTemplate.convertAndSendToUser(
                removedMember.getUsername(),
                "/queue/group-notifications",
                "Gruptan çıkarıldınız: " + updatedGroup.getName()
        );

        return ResponseEntity.ok(convertToDTO(updatedGroup));
    }

    // Grup bilgilerini güncelleme
    @PutMapping("/{groupId}")
    public ResponseEntity<?> updateGroup(
            @PathVariable Long groupId,
            @RequestBody @Valid CreateGroupDTO updateDTO) {

        ChatGroup updatedGroup = chatGroupService.updateGroup(groupId, updateDTO.getName());

        // Tüm grup üyelerine bildirim gönder
        updatedGroup.getMembers().forEach(member -> {
            messagingTemplate.convertAndSendToUser(
                    member.getUsername(),
                    "/queue/group-notifications",
                    "Grup bilgileri güncellendi: " + updatedGroup.getName()
            );
        });

        return ResponseEntity.ok(convertToDTO(updatedGroup));
    }

    // ChatGroup entity'sini DTO'ya çeviren yardımcı metod
    private GroupResponseDTO convertToDTO(ChatGroup group) {
        GroupResponseDTO dto = new GroupResponseDTO();
        dto.setId(group.getId());
        dto.setName(group.getName());
        dto.setCreatorUsername(group.getCreator().getUsername());
        dto.setMemberUsernames(
                group.getMembers().stream()
                        .map(User::getUsername)
                        .collect(Collectors.toSet())
        );
        dto.setMemberCount(group.getMembers().size());
        dto.setCreatedAt(group.getCreatedAt().toString());
        return dto;
    }
}