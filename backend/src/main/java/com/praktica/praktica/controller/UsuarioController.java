package com.praktica.praktica.controller;

import com.praktica.praktica.model.Usuario;
import com.praktica.praktica.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Collections;
import java.io.IOException;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    private static final String CLIENT_ID = "38423991825-burmdic6706js9cmarbugkj6dr12pcjk.apps.googleusercontent.com";

    @PostMapping("/login")
    public ResponseEntity<?> loginUsuario(@RequestBody Map<String, String> loginData) {
        String email = loginData.get("email");
        String password = loginData.get("password");

        Usuario usuario = usuarioService.loginUsuario(email, password);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Credenciales incorrectas"));
        }

        if (!usuario.getIdUsuario().equals(0L)) {
            usuario.setEstado("activo"); // al iniciar sesión
            usuario.setLastActive(LocalDateTime.now()); // hora de login
            usuarioService.save(usuario);
        }

        String token = usuarioService.generarToken(usuario);

        Map<String, Object> response = Map.of(
            "idUsuario", usuario.getIdUsuario(),
            "nombre", usuario.getNombre(),
            "apellido", usuario.getApellido(),
            "email", usuario.getEmail(),
            "rol", usuario.getRol().getNombreRol(),
            "estado", usuario.getEstado(),
            "token", token
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/google")
    public ResponseEntity<?> loginConGoogle(@RequestBody Map<String, String> body) {
        String idTokenString = body.get("idToken");

        try {
            JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    jsonFactory
            ).setAudience(Collections.singletonList(CLIENT_ID))
            .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Token de Google inválido"));
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String nombreCompleto = (String) payload.get("name");
            String nombre = nombreCompleto != null ? nombreCompleto.split(" ")[0] : "Google";
            String apellido = nombreCompleto != null && nombreCompleto.contains(" ")
                    ? nombreCompleto.split(" ")[1] : "User";

            Usuario usuario = usuarioService.findByEmail(email);
            if (usuario == null) {
                usuario = usuarioService.registrarUsuarioDesdeGoogle(email, nombre, apellido);
            }

            if (!usuario.getIdUsuario().equals(0L)) {
                usuario.setEstado("activo");
                usuario.setLastActive(LocalDateTime.now());
                usuarioService.save(usuario);
            }

            String token = usuarioService.generarToken(usuario);

            Map<String, Object> response = Map.of(
                    "idUsuario", usuario.getIdUsuario(),
                    "nombre", usuario.getNombre(),
                    "apellido", usuario.getApellido(),
                    "email", usuario.getEmail(),
                    "rol", usuario.getRol().getNombreRol(),
                    "estado", usuario.getEstado(),
                    "token", token
            );

            return ResponseEntity.ok(response);

        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Error al verificar el token de Google"));
        }
    }




    @PutMapping("/cambiar-contrasena")
    public ResponseEntity<?> cambiarContrasena(@RequestBody Map<String, String> body) {
        String nuevaContrasena = body.get("nuevaContrasena");

        if (nuevaContrasena == null || nuevaContrasena.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "La nueva contraseña no puede estar vacía"));
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        Usuario usuario = usuarioService.findByEmail(email);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Usuario no encontrado"));
        }

        try {
            usuarioService.cambiarContrasena(email, nuevaContrasena);
            return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada correctamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/recuperar-contrasena")
    public ResponseEntity<?> recuperarContrasena(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String nuevaContrasena = body.get("nuevaContrasena");

        if (email == null || email.trim().isEmpty() ||
            nuevaContrasena == null || nuevaContrasena.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Email y nueva contraseña son obligatorios"));
        }

        Usuario usuario = usuarioService.findByEmail(email);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "El correo no está registrado"));
        }

        try {
            usuarioService.cambiarContrasena(email, nuevaContrasena);
            return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada correctamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@RequestBody Usuario usuario) {
        try {
            Usuario nuevoUsuario = usuarioService.registrarUsuario(usuario);

            String token = usuarioService.generarToken(nuevoUsuario);

            Map<String, Object> response = Map.of(
                "idUsuario", nuevoUsuario.getIdUsuario(),
                "nombre", nuevoUsuario.getNombre(),
                "apellido", nuevoUsuario.getApellido(),
                "email", nuevoUsuario.getEmail(),
                "rol", nuevoUsuario.getRol().getNombreRol(),
                "token", token
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/verificar-contrasena")
    public ResponseEntity<?> verificarContrasena(@RequestBody Map<String, String> body) {
        String passwordIngresada = body.get("password");

        if (passwordIngresada == null || passwordIngresada.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "La contraseña no puede estar vacía"));
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Usuario usuario = usuarioService.findByEmail(email);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Usuario no encontrado"));
        }

        boolean coincide = usuarioService.verificarContrasena(usuario, passwordIngresada);

        if (!coincide) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("valido", false, "error", "Contraseña incorrecta"));
        }

        return ResponseEntity.ok(Map.of("valido", true));
    }

    @GetMapping("/empleados")
    public ResponseEntity<?> listarEmpleados(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size
    ) {
        Page<Usuario> empleados = usuarioService.listarEmpleadosPaginados(page, size);
        return ResponseEntity.ok(empleados);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUsuario(@RequestBody Map<String, Long> body) {
        Long idUsuario = body.get("idUsuario");
        Usuario usuario = usuarioService.findById(idUsuario);
        if (usuario != null && !usuario.getIdUsuario().equals(0L)) {
            usuario.setEstado("desconectado");
            usuario.setLastActive(null);
            usuarioService.save(usuario);
        }
        return ResponseEntity.ok(Map.of("mensaje", "Sesión cerrada correctamente"));
    }

    @GetMapping("/clientes/conectados")
    public Map<String, Long> obtenerClientesConectados() {
        long cantidad = usuarioService.contarClientesConectados();
        return Map.of("clientesConectados", cantidad);
    }

    @PutMapping("/keep-alive")
    public ResponseEntity<?> mantenerActivo(Authentication auth) {
        String email = auth.getName();
        Usuario usuario = usuarioService.findByEmail(email);
        if (usuario != null && !usuario.getIdUsuario().equals(0L)) {
            usuario.setLastActive(LocalDateTime.now());
            usuario.setEstado("activo");
            usuarioService.save(usuario);
        }
        return ResponseEntity.ok(Map.of("mensaje", "Usuario activo"));
    }
}
