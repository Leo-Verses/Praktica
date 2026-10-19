package com.praktica.praktica.service;

import com.praktica.praktica.model.*;
import com.praktica.praktica.repository.*;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final ChatOrdenRepository chatRepo;
    private final ParticipanteChatRepository participanteRepo;
    private final MensajeChatService mensajeChatService;
    private final OrdenRepository ordenRepo;
    private final UsuarioRepository usuarioRepo;

    public ChatService(ChatOrdenRepository chatRepo,
                        ParticipanteChatRepository participanteRepo,
                        MensajeChatService mensajeChatService,
                        OrdenRepository ordenRepo,
                        UsuarioRepository usuarioRepo) {
        this.chatRepo = chatRepo;
        this.participanteRepo = participanteRepo;
        this.mensajeChatService = mensajeChatService;
        this.ordenRepo = ordenRepo;
        this.usuarioRepo = usuarioRepo;
    }

    public ChatOrden crearChat(Long idOrden) {
        Optional<ChatOrden> existente = chatRepo.findByIdOrden(idOrden);
        if (existente.isPresent()) {
            return existente.get();
        }
        ChatOrden nuevo = new ChatOrden();
        nuevo.setIdOrden(idOrden);
        nuevo.setEstadoChat(ChatOrden.EstadoChat.activo);
        return chatRepo.save(nuevo);
    }

    public ParticipanteChat agregarParticipante(Long idChat, Long idUsuario, ParticipanteChat.RolChat rol) {
        ChatOrden chat = chatRepo.findById(idChat)
                .orElseThrow(() -> new RuntimeException("Chat no encontrado"));

        ParticipanteChat participante = new ParticipanteChat();
        participante.setChat(chat);
        participante.setIdUsuario(idUsuario);
        participante.setRolEnChat(rol);
        participante.setFechaUnion(LocalDateTime.now());
        return participanteRepo.save(participante);
    }

    public List<MensajeChat> obtenerMensajes(Long idChat) {
        List<MensajeChat> mensajes = mensajeChatService.obtenerMensajesPorChat(idChat);
        for (MensajeChat mensaje : mensajes) {
            if (mensaje.getIdEmisor() == 0) {
                mensaje.setNombreEmisor("Sistema");
            } else {
                Optional<Usuario> emisor = usuarioRepo.findById(mensaje.getIdEmisor());
                if (emisor.isPresent()) {
                    mensaje.setNombreEmisor(emisor.get().getNombre());
                } else {
                    mensaje.setNombreEmisor("Usuario no encontrado");
                }
            }
        }
        return mensajes;
    }


    public MensajeChat enviarMensaje(Long idChat, Long idEmisor, String mensaje) {
        ChatOrden chat = chatRepo.findById(idChat)
                .orElseThrow(() -> new RuntimeException("Chat no encontrado"));

        if (idEmisor == 0 && existeMensaje(idChat, idEmisor, mensaje)) {
            return null;
        }

        MensajeChat nuevo = new MensajeChat();
        nuevo.setChat(chat);
        nuevo.setIdEmisor(idEmisor);
        nuevo.setMensaje(mensaje);
        nuevo.setFechaEnvio(LocalDateTime.now());
        nuevo.setLeido(false);

        return mensajeChatService.guardarMensaje(nuevo);
    }

    public boolean existeMensaje(Long idChat, Long idEmisor, String mensaje) {
        List<MensajeChat> mensajesExistentes = mensajeChatService.obtenerMensajesPorChat(idChat);
        return mensajesExistentes.stream()
                .anyMatch(msg -> msg.getIdEmisor().equals(idEmisor) && msg.getMensaje().equals(mensaje));
    }

    public void marcarComoLeido(Long idMensaje) {
        MensajeChat msg = mensajeChatService.obtenerMensajePorId(idMensaje)
                .orElseThrow(() -> new RuntimeException("Mensaje no encontrado"));
        msg.setLeido(true);
        mensajeChatService.guardarMensaje(msg);
    }

    public void cerrarChat(Long idChat) {
        ChatOrden chat = chatRepo.findById(idChat)
                .orElseThrow(() -> new RuntimeException("Chat no encontrado"));
        chat.setEstadoChat(ChatOrden.EstadoChat.cerrado);
        chat.setFechaCierre(LocalDateTime.now());
        chatRepo.save(chat);
    }

    public List<ChatOrden> obtenerChatsPorUsuario(Long idUsuario) {
        List<Orden> ordenes = ordenRepo.findByUsuario_IdUsuario(idUsuario);
        for (Orden orden : ordenes) {
            Optional<ChatOrden> chatExistente = chatRepo.findByIdOrden(orden.getIdOrden());
            if (chatExistente.isEmpty()) {
                ChatOrden nuevoChat = crearChat(orden.getIdOrden());
                agregarParticipante(nuevoChat.getIdChat(), idUsuario, ParticipanteChat.RolChat.cliente);
            }
        }

        List<ParticipanteChat> participaciones = participanteRepo.findByIdUsuario(idUsuario);
        return participaciones.stream()
                .map(ParticipanteChat::getChat)
                .distinct()
                .collect(Collectors.toList());
    }

    public void crearChatsFaltantes() {
        List<Orden> todas = ordenRepo.findAll();
        for (Orden orden : todas) {
            Optional<ChatOrden> chatExistente = chatRepo.findByIdOrden(orden.getIdOrden());
            if (chatExistente.isEmpty()) {
                ChatOrden nuevoChat = crearChat(orden.getIdOrden());
                agregarParticipante(nuevoChat.getIdChat(), orden.getUsuario().getIdUsuario(), ParticipanteChat.RolChat.cliente);
            }
        }
    }

    public List<ChatOrden> obtenerTodosLosChats() {
        return chatRepo.findAll();
    }

    public ChatOrden obtenerChatPorOrden(Long idOrden) {
        return chatRepo.findByIdOrden(idOrden).orElseGet(() -> crearChat(idOrden));
    }

    public void marcarMensajesComoLeidos(Long idChat, Long idUsuarioQueLee) {
        List<MensajeChat> mensajes = mensajeChatService.obtenerMensajesPorChat(idChat);

        for (MensajeChat mensaje : mensajes) {
            if (!mensaje.getIdEmisor().equals(idUsuarioQueLee) && !mensaje.isLeido()) {
                mensaje.setLeido(true);
                mensajeChatService.guardarMensaje(mensaje);
            }
        }
    }

    public long obtenerCantidadChatsNoLeidos(Long idUsuario) {
        return mensajeChatService.contarChatsNoLeidos(idUsuario);
    }
}
