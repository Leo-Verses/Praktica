package com.praktica.praktica.repository;
import com.praktica.praktica.model.MensajeChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MensajeChatRepository extends JpaRepository<MensajeChat, Long> {
    List<MensajeChat> findByChat_IdChat(Long chatId);

    @Query("""
        SELECT COUNT(DISTINCT m.chat.idChat)
        FROM MensajeChat m
        WHERE m.leido = false
        AND m.idEmisor <> :idUsuario
    """)
    long contarChatsNoLeidos(@Param("idUsuario") Long idUsuario);

}
