package com.MsgApp.repository;

import com.MsgApp.model.Message;
import com.MsgApp.model.User;
import com.MsgApp.enums.MessageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    // Belirli bir kullanıcının gönderdiği mesajları bulma
    List<Message> findBySenderOrderByTimestampDesc(User sender);

    // Belirli bir kullanıcının aldığı mesajları bulma
    List<Message> findByRecipientsContainingOrderByTimestampDesc(User recipient);

    // Belirli bir grup için mesajları bulma
    List<Message> findByGroupIdOrderByTimestampDesc(Long groupId);

    // Okunmamış mesajları bulma
    List<Message> findByRecipientsContainingAndReadFalse(User recipient);

    // Belirli tarihten sonraki mesajları bulma
    List<Message> findByTimestampAfterAndRecipientsContaining(LocalDateTime timestamp, User recipient);

    // İki kullanıcı arasındaki özel mesajları bulma
    @Query("SELECT m FROM Message m WHERE m.type = 'PRIVATE' AND " +
            "((m.sender = :user1 AND :user2 MEMBER OF m.recipients) OR " +
            "(m.sender = :user2 AND :user1 MEMBER OF m.recipients)) " +
            "ORDER BY m.timestamp DESC")
    List<Message> findPrivateMessages(User user1, User user2);

    // Toplu duyuruları bulma
    List<Message> findByTypeOrderByTimestampDesc(MessageType type);


}