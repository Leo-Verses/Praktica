package com.praktica.praktica;
import com.praktica.praktica.service.UsuarioService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class EncriptadorInicial implements CommandLineRunner {

    private final UsuarioService usuarioService;

    public EncriptadorInicial(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Override
    public void run(String... args) {
        usuarioService.encriptarContrasenasExistentes();
        System.out.println("Contraseñas antiguas encriptadas correctamente."); 
    }
}