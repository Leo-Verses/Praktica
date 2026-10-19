package com.praktica.praktica.service;

import com.praktica.praktica.model.MensajeChat;
import com.praktica.praktica.repository.MensajeChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MensajeChatService {

    @Autowired
    private MensajeChatRepository mensajeChatRepository;
    private final MensajeChatRepository mensajeChatRepo;

    public MensajeChatService(MensajeChatRepository mensajeChatRepo) {
        this.mensajeChatRepo = mensajeChatRepo;
    }

    public MensajeChat guardarMensaje(MensajeChat mensajeChat) {
        return mensajeChatRepository.save(mensajeChat);
        
    }

    public List<MensajeChat> obtenerMensajesPorChat(Long idChat) {
        return mensajeChatRepository.findByChat_IdChat(idChat);
    }

    public List<MensajeChat> obtenerTodosLosMensajes() {
        return mensajeChatRepository.findAll();
    }

    public Optional<MensajeChat> obtenerMensajePorId(Long id) {
        return mensajeChatRepository.findById(id);
    }

    public void eliminarMensaje(Long id) {
        mensajeChatRepository.deleteById(id);
    }

    public long contarChatsNoLeidos(Long idUsuario) {
        return mensajeChatRepo.contarChatsNoLeidos(idUsuario);
    }
}
