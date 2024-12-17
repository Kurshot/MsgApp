package com.MsgApp.repository;

import com.MsgApp.model.ChatGroup;
import com.MsgApp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatGroupRepository extends JpaRepository<ChatGroup, Long> {
    // Bir kullanıcının üye olduğu grupları bulma
    List<ChatGroup> findByMembersContaining(User member);

    // Bir kullanıcının oluşturduğu grupları bulma
    List<ChatGroup> findByCreator(User creator);

    // Grup adına göre arama
    List<ChatGroup> findByNameContainingIgnoreCase(String name);

    // Belirli bir üye sayısından az üyesi olan grupları bulma
    @Query("SELECT g FROM ChatGroup g WHERE SIZE(g.members) < :maxMembers")
    List<ChatGroup> findGroupsWithLessMembers(int maxMembers);

    // Bir kullanıcının hem üyesi hem de yaratıcısı olduğu grupları bulma
    @Query("SELECT g FROM ChatGroup g WHERE g.creator = :user OR :user MEMBER OF g.members")
    List<ChatGroup> findUserInvolvedGroups(User user);

    // Grup adının benzersiz olup olmadığını kontrol etme
    boolean existsByNameIgnoreCase(String name);
}
