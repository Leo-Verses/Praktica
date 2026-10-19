package com.praktica.praktica.controller;

import com.praktica.praktica.model.*;
import com.praktica.praktica.service.ChatService;
import com.praktica.praktica.service.OrdenService;
import com.praktica.praktica.service.ProductoService;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chats")
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final OrdenService ordenService;
    private final ProductoService productoService;
    

    public ChatController(ChatService chatService, SimpMessagingTemplate simpMessagingTemplate, OrdenService ordenService,
    ProductoService productoService) {
        this.chatService = chatService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.ordenService = ordenService;
        this.productoService = productoService;
    }

    @PostMapping("/crear/{idOrden}")
    public ChatOrden crearChat(@PathVariable Long idOrden) {
        return chatService.crearChat(idOrden);
    }

    @PostMapping("/{idChat}/participantes")
    public ParticipanteChat agregarParticipante(@PathVariable Long idChat, @RequestBody Map<String, Object> payload) {
        Long idUsuario = Long.parseLong(payload.get("idUsuario").toString());
        String rol = payload.get("rol").toString();
        return chatService.agregarParticipante(idChat, idUsuario, ParticipanteChat.RolChat.valueOf(rol));
    }

    @PostMapping("/{idChat}/enviar")
    public MensajeChat enviarMensaje(@PathVariable Long idChat, @RequestBody Map<String, Object> payload) {
        Long idEmisor = Long.parseLong(payload.get("idEmisor").toString());
        String mensaje = payload.get("mensaje").toString();

        if (idEmisor == 0) {
            boolean mensajeExistente = chatService.existeMensaje(idChat, idEmisor, mensaje);
            if (mensajeExistente) return null;
        }

        MensajeChat mensajeChat = chatService.enviarMensaje(idChat, idEmisor, mensaje);

        simpMessagingTemplate.convertAndSend(
            "/topic/chat/" + idChat,
            mensajeChat
        );

        System.out.println("Mensaje enviado y broadcast: " + mensajeChat);
        return mensajeChat;
    }


    @GetMapping("/{idChat}/mensajes")
    public List<MensajeChat> obtenerMensajes(@PathVariable Long idChat) {
        return chatService.obtenerMensajes(idChat);
    }

    @PutMapping("/mensajes/{idMensaje}/leido")
    public void marcarComoLeido(@PathVariable Long idMensaje) {
        chatService.marcarComoLeido(idMensaje);
    }

    @PutMapping("/{idChat}/cerrar")
    public void cerrarChat(@PathVariable Long idChat) {
        chatService.cerrarChat(idChat);
    }

    @GetMapping("/usuario/{idUsuario}")
    public List<Map<String, Object>> obtenerChatsPorUsuario(@PathVariable Long idUsuario) {
        List<ChatOrden> chats = chatService.obtenerChatsPorUsuario(idUsuario);
        List<Map<String, Object>> resultado = new ArrayList<>();

        for (ChatOrden chat : chats) {
            Map<String, Object> chatMap = new HashMap<>();
            chatMap.put("idChat", chat.getIdChat());
            chatMap.put("idOrden", chat.getIdOrden());
            chatMap.put("estado", chat.getEstadoChat());
            chatMap.put("fechaCreacion", chat.getFechaCreacion());

            List<MensajeChat> mensajes = chat.getMensajes();
            if (mensajes != null && !mensajes.isEmpty()) {
                MensajeChat ultimo = mensajes.get(mensajes.size() - 1);
                chatMap.put("ultimoMensaje", ultimo.getMensaje());
                chatMap.put("fechaUltimoMensaje", ultimo.getFechaEnvio());
            }

            resultado.add(chatMap);
        }

        return resultado;
    }

    @PostMapping("/sincronizar")
    public ResponseEntity<String> sincronizarChats() {
        chatService.crearChatsFaltantes();
        return ResponseEntity.ok("Chats creados para órdenes sin chat");
    }

    @GetMapping("/todos")
    public List<Map<String, Object>> obtenerTodosLosChats() {
        List<ChatOrden> chats = chatService.obtenerTodosLosChats();
        List<Map<String, Object>> resultado = new ArrayList<>();

        for (ChatOrden chat : chats) {
            Map<String, Object> chatMap = new HashMap<>();
            chatMap.put("idChat", chat.getIdChat());
            chatMap.put("idOrden", chat.getIdOrden());
            chatMap.put("estado", chat.getEstadoChat());
            chatMap.put("fechaCreacion", chat.getFechaCreacion());

            String nombreCliente = chat.getOrden().getUsuario().getNombre();
            chatMap.put("nombreCliente", nombreCliente);

            List<MensajeChat> mensajes = chat.getMensajes();
            if (mensajes != null && !mensajes.isEmpty()) {
                MensajeChat ultimo = mensajes.get(mensajes.size() - 1);
                chatMap.put("ultimoMensaje", ultimo.getMensaje());
                chatMap.put("fechaUltimoMensaje", ultimo.getFechaEnvio());
            }

            resultado.add(chatMap);
        }

        return resultado;
    }

    @GetMapping("/{idChat}/inspeccion")
    public ResponseEntity<?> obtenerOrdenPorChat(@PathVariable Long idChat) {
        Orden orden = ordenService.buscarPorIdChat(idChat);

        if (orden == null) return ResponseEntity.notFound().build();

        Map<String, Object> ordenDTO = new HashMap<>();
        ordenDTO.put("idOrden", orden.getIdOrden());
        ordenDTO.put("nombreCliente", orden.getNombreCliente());
        ordenDTO.put("telefono", orden.getTelefono());
        ordenDTO.put("tipoPedido", orden.getTipoPedido());
        ordenDTO.put("idServicio", orden.getIdServicio());
        ordenDTO.put("idPack", orden.getIdPack());
        ordenDTO.put("cantidad", orden.getCantidad());
        ordenDTO.put("fechaEntregaEstimada", orden.getFechaEntregaEstimada());
        ordenDTO.put("descripcion", orden.getDescripcion());
        ordenDTO.put("estado", orden.getEstado());

        String nombreProducto = productoService.obtenerNombreProducto(
                orden.getIdServicio(), orden.getIdPack()
        );
        ordenDTO.put("nombreProducto", nombreProducto);

        return ResponseEntity.ok(ordenDTO);
    }

    @PostMapping("/enviar-link/{idOrden}")
    public ResponseEntity<?> enviarLinkPago(@PathVariable Long idOrden, @RequestBody Map<String, Object> payload) {
        String mensaje = payload.get("mensaje").toString();
        Long idEmisor = 0L; // usuario: Sistema
        ChatOrden chat = chatService.crearChat(idOrden);
        MensajeChat mensajeChat = chatService.enviarMensaje(chat.getIdChat(), idEmisor, mensaje);
        simpMessagingTemplate.convertAndSend("/topic/chat/" + chat.getIdChat(), mensajeChat);
        return ResponseEntity.ok(mensajeChat);
    }

    @PutMapping("/{idChat}/marcar-leido/{idUsuario}")
    public ResponseEntity<String> marcarChatComoLeido(@PathVariable Long idChat, @PathVariable Long idUsuario) {
        chatService.marcarMensajesComoLeidos(idChat, idUsuario);
        return ResponseEntity.ok("Mensajes marcados como leídos");
    }

    @GetMapping("/usuario/{idUsuario}/no-leidos")
    public Map<String, Long> obtenerChatsNoLeidos(@PathVariable Long idUsuario) {
        long cantidad = chatService.obtenerCantidadChatsNoLeidos(idUsuario);
        Map<String, Long> respuesta = new HashMap<>();
        respuesta.put("chatsNoLeidos", cantidad);
        return respuesta;
    }
}
