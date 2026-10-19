package com.praktica.praktica.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "chats_orden")
public class ChatOrden {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_chat")
    private Long idChat;

    @Column(name = "id_orden", nullable = false)
    private Long idOrden;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_chat")
    private EstadoChat estadoChat = EstadoChat.activo;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<MensajeChat> mensajes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_orden", insertable = false, updatable = false)
    private Orden orden;

    public enum EstadoChat {
        activo, cerrado, archivado
    }

    public ChatOrden() {}

    public ChatOrden(Long idChat) {
        this.idChat = idChat;
    }

    public Long getIdChat() { return idChat; }
    public void setIdChat(Long idChat) { this.idChat = idChat; }

    public Long getIdOrden() { return idOrden; }
    public void setIdOrden(Long idOrden) { this.idOrden = idOrden; }

    public EstadoChat getEstadoChat() { return estadoChat; }
    public void setEstadoChat(EstadoChat estadoChat) { this.estadoChat = estadoChat; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaCierre() { return fechaCierre; }
    public void setFechaCierre(LocalDateTime fechaCierre) { this.fechaCierre = fechaCierre; }

    public List<MensajeChat> getMensajes() { return mensajes; }
    public void setMensajes(List<MensajeChat> mensajes) { this.mensajes = mensajes; }

    public Orden getOrden() { return orden; }
    public void setOrden(Orden orden) { this.orden = orden; }
}
