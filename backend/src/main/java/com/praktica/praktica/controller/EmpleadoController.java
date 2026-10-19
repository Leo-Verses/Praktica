package com.praktica.praktica.controller;

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
    public ResponseEntity<?> crearEmpleado(@RequestBody Usuario usuario) {
        try {
            if (usuario.getEmail() == null || usuario.getEmail().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "El correo es obligatorio"));
            }

            List<Integer> rolesValidos = List.of(2, 3, 4);
            if (!rolesValidos.contains(usuario.getRol().getIdRol())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Rol inválido. Los roles válidos son: 'administrador', 'catalogador' o 'ventas'"));
            }

            Optional<Rol> rolOptional = rolRepository.findByIdRol(usuario.getRol().getIdRol());
            if (!rolOptional.isPresent()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Rol no encontrado"));
            }

            Rol rol = rolOptional.get();
            usuario.setEstado("desconectado");
            usuario.setContrasena("praktica2025");
            usuario.setRol(rol);

            Usuario nuevoEmpleado = usuarioService.registrarEmpleado(usuario);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "mensaje", "Empleado creado exitosamente",
                    "empleado", nuevoEmpleado
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al crear el empleado", "details", e.getMessage()));
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
