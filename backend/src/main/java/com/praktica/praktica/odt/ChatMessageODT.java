package com.praktica.praktica.odt;

import java.time.LocalDateTime;

public class ChatMessageODT {
    private Long idChat;
    private Long idEmisor;
    private String nombreEmisor;
    private String mensaje;
    private LocalDateTime fechaEnvio;

    public ChatMessageODT() {}

    public ChatMessageODT(Long idChat, Long idEmisor, String nombreEmisor, String mensaje, LocalDateTime fechaEnvio) {
        this.idChat = idChat;
        this.idEmisor = idEmisor;
        this.nombreEmisor = nombreEmisor;
        this.mensaje = mensaje;
        this.fechaEnvio = fechaEnvio;
    }

    public Long getIdChat() { return idChat; }
    public void setIdChat(Long idChat) { this.idChat = idChat; }

    public Long getIdEmisor() { return idEmisor; }
    public void setIdEmisor(Long idEmisor) { this.idEmisor = idEmisor; }

    public String getNombreEmisor() { return nombreEmisor; }
    public void setNombreEmisor(String nombreEmisor) { this.nombreEmisor = nombreEmisor; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public LocalDateTime getFechaEnvio() { return fechaEnvio; }
    public void setFechaEnvio(LocalDateTime fechaEnvio) { this.fechaEnvio = fechaEnvio; }
}