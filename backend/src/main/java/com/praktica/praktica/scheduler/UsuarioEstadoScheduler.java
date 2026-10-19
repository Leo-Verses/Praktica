package com.praktica.praktica.scheduler;

import com.praktica.praktica.model.Usuario;
import com.praktica.praktica.service.UsuarioService;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class UsuarioEstadoScheduler {

    private final UsuarioService usuarioService;

    public UsuarioEstadoScheduler(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Scheduled(fixedRate = 60000)
    public void actualizarEstados() {
        LocalDateTime ahora = LocalDateTime.now();
        List<Usuario> usuarios = usuarioService.listarTodos();

        for (Usuario usuario : usuarios) {
            if (usuario.getIdUsuario() == 0) continue; //al usuario: Sistema, no le afecta

            if ("desconectado".equalsIgnoreCase(usuario.getEstado())) {
                continue;
            }

            if (usuario.getLastActive() != null) {
                long minutosInactivos = java.time.Duration.between(usuario.getLastActive(), ahora).toMinutes();
                if (minutosInactivos >= 5) {
                    usuario.setEstado("inactivo");
                } else {
                    usuario.setEstado("activo");
                }
            } else {
                // last_active null, no pasa nada, sigue "activo"
            }

            usuarioService.save(usuario);
        }
    }
}
