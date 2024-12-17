package com.MsgApp.controller;

import com.MsgApp.dto.MessageDTO;
import com.MsgApp.model.Message;
import com.MsgApp.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@MessageMapping("/chat")  // WebSocket endpoint base path
@RequestMapping("/api/messages")  // REST endpoint base path
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    // WebSocket endpoint for private messages
    @MessageMapping("/private")
    public void handlePrivateMessage(@Payload MessageDTO messageDto) {
        Message message = messageService.sendPrivateMessage(
                messageDto.getSenderId(),
                messageDto.getRecipientId(),
                messageDto.getContent()
        );

        // Mesajı alıcıya gönder
        messagingTemplate.convertAndSendToUser(
                message.getRecipient().getUsername(),
                "/queue/private",
                message
        );
    }

    // WebSocket endpoint for group messages
    @MessageMapping("/group")
    public void handleGroupMessage(@Payload MessageDTO messageDto) {
        Message message = messageService.sendGroupMessage(
                messageDto.getSenderId(),
                messageDto.getGroupId(),
                messageDto.getContent()
        );

        // Mesajı gruba gönder
        messagingTemplate.convertAndSend(
                "/topic/group." + messageDto.getGroupId(),
                message
        );
    }

    // REST endpoint to get message history
    @GetMapping("/history/{userId}")
    public ResponseEntity<?> getMessageHistory(
            @PathVariable Long userId,
            @RequestParam Long otherUserId) {
        return ResponseEntity.ok(
                messageService.getMessageHistory(userId, otherUserId)
        );
    }

    // REST endpoint to get unread messages
    @GetMapping("/unread/{userId}")
    public ResponseEntity<?> getUnreadMessages(@PathVariable Long userId) {
        return ResponseEntity.ok(
                messageService.getUnreadMessages(userId)
        );
    }
}