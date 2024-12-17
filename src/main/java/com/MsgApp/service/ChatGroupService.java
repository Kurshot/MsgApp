package com.MsgApp.service;

import com.MsgApp.model.ChatGroup;
import com.MsgApp.model.User;
import com.MsgApp.repository.ChatGroupRepository;
import com.MsgApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ChatGroupService {
    private final ChatGroupRepository chatGroupRepository;
    private final UserRepository userRepository;

    // Yeni grup oluşturma
    @Transactional
    public ChatGroup createGroup(String groupName, Long creatorId, Set<Long> memberIds) {
        // Maksimum 5 üye kontrolü
        if (memberIds.size() > 4) { // Creator + 4 member = 5 total
            throw new IllegalArgumentException("Group cannot have more than 5 members");
        }

        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("Creator not found"));

        // Grup adı kontrolü
        if (chatGroupRepository.existsByNameIgnoreCase(groupName)) {
            throw new RuntimeException("Group name already exists");
        }

        ChatGroup group = new ChatGroup();
        group.setName(groupName);
        group.setCreator(creator);

        // Üyeleri ekle
        group.getMembers().add(creator); // Creator'ı otomatik ekle
        memberIds.stream()
                .map(id -> userRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Member not found: " + id)))
                .forEach(member -> group.getMembers().add(member));

        return chatGroupRepository.save(group);
    }

    // Gruba üye ekleme
    @Transactional
    public ChatGroup addMember(Long groupId, Long userId) {
        ChatGroup group = chatGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        if (!group.canAddMember()) {
            throw new IllegalStateException("Group has reached maximum member limit");
        }

        User newMember = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        group.getMembers().add(newMember);
        return chatGroupRepository.save(group);
    }

    // Gruptan üye çıkarma
    @Transactional
    public ChatGroup removeMember(Long groupId, Long userId) {
        ChatGroup group = chatGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        User member = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Creator kontrolü
        if (group.getCreator().equals(member)) {
            throw new IllegalStateException("Cannot remove group creator");
        }

        group.getMembers().remove(member);
        return chatGroupRepository.save(group);
    }

    // Kullanıcının gruplarını getir
    public List<ChatGroup> getUserGroups(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return chatGroupRepository.findUserInvolvedGroups(user);
    }

    // Grup bilgilerini güncelle
    @Transactional
    public ChatGroup updateGroup(Long groupId, String newName) {
        ChatGroup group = chatGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        if (!group.getName().equals(newName) &&
                chatGroupRepository.existsByNameIgnoreCase(newName)) {
            throw new RuntimeException("Group name already exists");
        }

        group.setName(newName);
        return chatGroupRepository.save(group);
    }
}