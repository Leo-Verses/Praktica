package com.praktica.praktica.repository;
import com.praktica.praktica.model.ChatOrden;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ChatOrdenRepository extends JpaRepository<ChatOrden, Long> {
    Optional<ChatOrden> findByIdOrden(Long idOrden);
    ChatOrden findByIdChat(Long idChat);
}