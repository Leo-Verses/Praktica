package com.praktica.praktica.repository;
import com.praktica.praktica.model.ParticipanteChat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ParticipanteChatRepository extends JpaRepository<ParticipanteChat, Long> {
    List<ParticipanteChat> findByIdUsuario(Long idUsuario);
}