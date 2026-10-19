package com.praktica.praktica.controller;

import com.praktica.praktica.model.MensajeChat;
import com.praktica.praktica.odt.ChatMessageODT;
import com.praktica.praktica.service.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Controller
public class ChatStompController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatStompController(ChatService chatService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat/{idChat}/enviar")
    public void enviarMensaje(@DestinationVariable Long idChat, ChatMessageODT mensaje) {

        MensajeChat m = chatService.enviarMensaje(idChat, mensaje.getIdEmisor(), mensaje.getMensaje());

        ChatMessageODT dto = new ChatMessageODT(
                m.getChat().getIdChat(),
                m.getIdEmisor(),
                mensaje.getNombreEmisor(),
                m.getMensaje(),
                m.getFechaEnvio()
        );

        messagingTemplate.convertAndSend("/topic/chat/" + idChat, dto);
    }
}
