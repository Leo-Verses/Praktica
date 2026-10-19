package com.praktica.praktica.service;

import com.praktica.praktica.model.ChatOrden;
import com.praktica.praktica.model.HistorialCambiosOrden;
import com.praktica.praktica.model.Orden;
import com.praktica.praktica.model.ParticipanteChat;
import com.praktica.praktica.model.Usuario;
import com.praktica.praktica.repository.ChatOrdenRepository;
import com.praktica.praktica.repository.HistorialCambiosOrdenRepository;
import com.praktica.praktica.repository.OrdenRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class OrdenService {
    private final OrdenRepository ordenRepository;
    private final HistorialCambiosOrdenRepository historialRepository;
    private final ChatService chatService;
    private final UsuarioService usuarioService;
    @Autowired
    private ChatOrdenRepository chatOrdenRepository;

    public OrdenService(OrdenRepository ordenRepository,
                        HistorialCambiosOrdenRepository historialRepository,
                        ChatService chatService,
                        UsuarioService usuarioService) {
        this.ordenRepository = ordenRepository;
        this.historialRepository = historialRepository;
        this.chatService = chatService;
        this.usuarioService = usuarioService;
    }

    public Orden crearOrden(Orden orden) {
        if (orden.getUsuario() != null && orden.getUsuario().getIdUsuario() != null) {
            Usuario usuarioPersistido = usuarioService.findById(orden.getUsuario().getIdUsuario());
            if (usuarioPersistido == null) {
                throw new RuntimeException("Usuario no encontrado con ID: " + orden.getUsuario().getIdUsuario());
            }
            orden.setUsuario(usuarioPersistido);
            System.out.println("Usuario asociado a la orden: " + usuarioPersistido.getEmail());
        } else {
            throw new RuntimeException("La orden debe tener un usuario asociado");
        }

        Orden nuevaOrden = ordenRepository.save(orden);
        System.out.println("=== crearOrden llamado ===");
        System.out.println("Orden guardada con ID: " + nuevaOrden.getIdOrden());

        ChatOrden chat = chatService.crearChat(nuevaOrden.getIdOrden());
        chatService.agregarParticipante(
            chat.getIdChat(),
            nuevaOrden.getUsuario().getIdUsuario(),
            ParticipanteChat.RolChat.cliente
        );

        return nuevaOrden;
    }





    public List<Orden> listarPorUsuario(Long idUsuario) {
        return ordenRepository.findByUsuario_IdUsuario(idUsuario);
    }

    public Orden buscarPorId(Long idOrden) {
        return ordenRepository.findById(idOrden).orElse(null);
    }

    public Orden actualizarEstado(Long idOrden, Orden.EstadoOrden nuevoEstado) {
        Optional<Orden> opt = ordenRepository.findById(idOrden);
        if (opt.isEmpty()) return null;

        Orden orden = opt.get();
        Orden.EstadoOrden anterior = orden.getEstado();
        orden.setEstado(nuevoEstado);
        Orden actualizada = ordenRepository.save(orden);

        registrarCambio(orden, "estado", anterior.name(), nuevoEstado.name(), "Actualización de estado");
        return actualizada;
    }

    public Orden actualizarDescripcion(Long idOrden, Long idUsuario, String nuevaDescripcion, String motivo) {
        Optional<Orden> opt = ordenRepository.findById(idOrden);
        if (opt.isEmpty()) return null;

        Orden orden = opt.get();
        String anterior = orden.getDescripcion();
        orden.setDescripcion(nuevaDescripcion);
        Orden actualizada = ordenRepository.save(orden);

        registrarCambio(orden, "descripcion", anterior, nuevaDescripcion, motivo);
        return actualizada;
    }

    public void registrarCambio(Orden orden, String campo, String valorAnterior, String valorNuevo, String motivo) {
        HistorialCambiosOrden h = new HistorialCambiosOrden();
        h.setIdOrden(orden.getIdOrden());
        h.setIdUsuario(orden.getUsuario().getIdUsuario());
        h.setCampoModificado(campo);
        h.setValorAnterior(valorAnterior);
        h.setValorNuevo(valorNuevo);
        h.setMotivo(motivo);
        historialRepository.save(h);
    }

    public Orden buscarPorIdChat(Long idChat) {
        ChatOrden chat = chatOrdenRepository.findByIdChat(idChat);
        return (chat != null) ? chat.getOrden() : null;
    }
}
