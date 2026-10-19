package com.praktica.praktica.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "participantes_chat")
public class ParticipanteChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_participante")
    private Long idParticipante;

    @ManyToOne
    @JoinColumn(name = "id_chat", nullable = false)
    private ChatOrden chat;

    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol_en_chat", nullable = false)
    private RolChat rolEnChat;

    @Column(name = "fecha_union")
    private LocalDateTime fechaUnion = LocalDateTime.now();

    public enum RolChat {
        cliente, empleado
    }

    public Long getIdParticipante() {
        return idParticipante;
    }

    public void setIdParticipante(Long idParticipante) {
        this.idParticipante = idParticipante;
    }

    public ChatOrden getChat() {
        return chat;
    }

    public void setChat(ChatOrden chat) {
        this.chat = chat;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public RolChat getRolEnChat() {
        return rolEnChat;
    }

    public void setRolEnChat(RolChat rolEnChat) {
        this.rolEnChat = rolEnChat;
    }

    public LocalDateTime getFechaUnion() {
        return fechaUnion;
    }

    public void setFechaUnion(LocalDateTime fechaUnion) {
        this.fechaUnion = fechaUnion;
    }
}
