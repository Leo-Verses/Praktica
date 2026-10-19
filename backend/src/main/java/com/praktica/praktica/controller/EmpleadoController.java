package com.praktica.praktica.controller;

import com.praktica.praktica.dto.CrearEmpleadoRequest;
import com.praktica.praktica.model.Rol;
import com.praktica.praktica.model.Usuario;
import com.praktica.praktica.repository.RolRepository;
import com.praktica.praktica.service.UsuarioService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/empleados")
public class EmpleadoController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private RolRepository rolRepository;

    @PostMapping("/crear")
    public ResponseEntity<?> crearEmpleado(@RequestBody CrearEmpleadoRequest request) {
        try {
            if (request == null || request.nombre() == null || request.nombre().isBlank()
                    || request.apellido() == null || request.apellido().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Nombre y apellido son obligatorios"));
            }
            if (request.email() == null || request.email().isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("error", "El correo es obligatorio"));
            }
            if (request.rol() == null || request.rol().idRol() == null
                    || !List.of(2, 3, 4).contains(request.rol().idRol())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Rol de empleado inválido"));
            }

            Optional<Rol> rolOptional = rolRepository.findByIdRol(request.rol().idRol());
            if (rolOptional.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Rol no encontrado"));
            }

            // Nunca utilizar una contraseña predeterminada compartida.
            Usuario usuario = new Usuario();
            usuario.setNombre(request.nombre().trim());
            usuario.setApellido(request.apellido().trim());
            usuario.setEmail(request.email().trim());
            usuario.setContrasena(request.contrasena());
            usuario.setEstado("desconectado");
            usuario.setRol(rolOptional.get());

            Usuario creado = usuarioService.registrarEmpleado(usuario);
            // Devolver solo datos públicos; no exponer el hash BCrypt.
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "mensaje", "Empleado creado exitosamente",
                    "idUsuario", creado.getIdUsuario(),
                    "email", creado.getEmail()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "No se pudo registrar el empleado"));
        }
    }

    @PutMapping("/actualizar/{idUsuario}")
    public ResponseEntity<?> actualizarEmpleado(@PathVariable Long idUsuario, @RequestBody Map<String, String> body) {
        try {
            Usuario usuario = usuarioService.findById(idUsuario);

            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Empleado no encontrado"));
            }

            if (body.containsKey("nombre")) {
                usuario.setNombre(body.get("nombre"));
            }

            if (body.containsKey("apellido")) {
                usuario.setApellido(body.get("apellido"));
            }

            if (body.containsKey("gmail")) {
                String nuevoEmail = body.get("gmail");
                if (usuarioService.findByEmail(nuevoEmail) != null && !usuario.getEmail().equals(nuevoEmail)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "El correo ya está registrado"));
                }
                usuario.setEmail(nuevoEmail);
            }

            if (body.containsKey("rol")) {
                String nuevoRol = body.get("rol");
                if (!nuevoRol.equals("administrador") && !nuevoRol.equals("Catalogador") && !nuevoRol.equals("Ventas")) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Rol inválido"));
                }
                usuarioService.actualizarRol(usuario, nuevoRol);
            }

            usuarioService.save(usuario);

            return ResponseEntity.ok(Map.of("mensaje", "Empleado actualizado exitosamente", "empleado", usuario));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al actualizar los datos del empleado", "details", e.getMessage()));
        }
    }

    @GetMapping("/listar-por-roles")
    public ResponseEntity<?> listarEmpleadosPorRoles(HttpServletRequest request) {
        try {
            List<Usuario> empleados = usuarioService.obtenerUsuariosPorRoles();

            List<Map<String, Object>> empleadosResponse = empleados.stream()
                    .map(usuario -> {
                        Map<String, Object> empleadoMap = Map.of(
                            "idUsuario", usuario.getIdUsuario(),
                            "nombre", usuario.getNombre(),
                            "apellido", usuario.getApellido(),
                            "email", usuario.getEmail(),
                            "rol", usuario.getRol().getNombreRol(),
                            "estado", usuario.getEstado()
                        );
                        return empleadoMap;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(Map.of("empleados", empleadosResponse));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al listar los empleados", "details", e.getMessage()));
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<Page<Usuario>> buscar(
            @RequestParam String filtro,
            @RequestParam int page,
            @RequestParam int size
    ) {
        return ResponseEntity.ok(usuarioService.buscarEmpleados(filtro, page, size));
    }

    @GetMapping("/conectados")
    public Map<String, Long> obtenerUsuariosRolesConectados() {
        long cantidad = usuarioService.contarUsuariosConectadosPorRoles(List.of(2L, 3L, 4L));
        return Map.of("usuariosConectados", cantidad);
    }
}
