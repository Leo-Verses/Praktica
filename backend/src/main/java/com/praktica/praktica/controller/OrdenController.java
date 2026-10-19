package com.praktica.praktica.controller;

import com.praktica.praktica.model.Orden;
import com.praktica.praktica.model.Usuario;
import com.praktica.praktica.service.OrdenService;
import com.praktica.praktica.service.UsuarioService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/ordenes")
public class OrdenController {
    private final OrdenService ordenService;
    private final UsuarioService usuarioService;

    public OrdenController(OrdenService ordenService, UsuarioService usuarioService) {
        this.ordenService = ordenService;
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<?> crearOrden(@RequestBody Orden orden, HttpServletRequest request) {
        String email = (String) request.getAttribute("usuario");
        if (email == null) {
            throw new RuntimeException("Usuario no autenticado");
        }

        Usuario usuario = usuarioService.findByEmail(email);
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }

        orden.setUsuario(usuario);
        Orden creada = ordenService.crearOrden(orden);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("id_orden", creada.getIdOrden(), "estado", creada.getEstado()));
    }


    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Orden>> listarPorUsuario(@PathVariable Long idUsuario) {
        List<Orden> lista = ordenService.listarPorUsuario(idUsuario);
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{idOrden}")
    public ResponseEntity<Orden> obtenerPorId(@PathVariable Long idOrden) {
        Orden orden = ordenService.buscarPorId(idOrden);
        return (orden != null) ? ResponseEntity.ok(orden) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{idOrden}/estado")
    public ResponseEntity<?> actualizarEstado(@PathVariable Long idOrden, @RequestParam String estado) {
        try {
            Orden.EstadoOrden nuevo = Orden.EstadoOrden.valueOf(estado.toLowerCase());
            Orden actualizada = ordenService.actualizarEstado(idOrden, nuevo);
            if (actualizada == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(Map.of("estado", actualizada.getEstado()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Estado inválido. Valores válidos: pendiente, produciendo, listo, entregado, cerrado, cancelado"));
        }
    }

    @PutMapping("/{idOrden}/descripcion")
    public ResponseEntity<?> actualizarDescripcion(
            @PathVariable Long idOrden,
            @RequestParam Long idUsuario,
            @RequestParam String descripcion,
            @RequestParam(required = false, defaultValue = "Actualización por usuario") String motivo) {

        Orden actualizada = ordenService.actualizarDescripcion(idOrden, idUsuario, descripcion, motivo);
        if (actualizada == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(Map.of(
                "id_orden", actualizada.getIdOrden(),
                "descripcion", actualizada.getDescripcion()
        ));
    }

    @PutMapping("/{idOrden}/detalles")
    public ResponseEntity<?> actualizarDetalles(
            @PathVariable Long idOrden,
            @RequestParam Long idUsuario,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) Integer cantidad,
            @RequestParam(required = false, name = "fechaEntregaEstimada") String fechaEntregaEstimada,
            @RequestParam(required = false, defaultValue = "Actualización manual de orden") String motivo) {

        Orden orden = ordenService.buscarPorId(idOrden);
        if (orden == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "No se encontró la orden con ID " + idOrden));
        }

        boolean actualizado = false;

        if (descripcion != null && !descripcion.equals(orden.getDescripcion())) {
            ordenService.registrarCambio(orden, "descripcion", orden.getDescripcion(), descripcion, motivo);
            orden.setDescripcion(descripcion);
            actualizado = true;
        }

        if (cantidad != null && !cantidad.equals(orden.getCantidad())) {
            ordenService.registrarCambio(orden, "cantidad",
                    String.valueOf(orden.getCantidad()),
                    String.valueOf(cantidad),
                    motivo);
            orden.setCantidad(cantidad);
            actualizado = true;
        }

        if (fechaEntregaEstimada != null) {
            try {
                java.time.LocalDate nuevaFecha = java.time.LocalDate.parse(fechaEntregaEstimada);
                if (!nuevaFecha.equals(orden.getFechaEntregaEstimada())) {
                    ordenService.registrarCambio(orden, "fechaEntregaEstimada",
                            orden.getFechaEntregaEstimada() != null ? orden.getFechaEntregaEstimada().toString() : "null",
                            nuevaFecha.toString(),
                            motivo);
                    orden.setFechaEntregaEstimada(nuevaFecha);
                    actualizado = true;
                }
            } catch (Exception e) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Formato de fecha inválido. Usa YYYY-MM-DD."));
            }
        }

        if (actualizado) {
            Orden guardada = ordenService.crearOrden(orden);
            return ResponseEntity.ok(Map.of(
                    "id_orden", guardada.getIdOrden(),
                    "mensaje", "Orden actualizada correctamente"
            ));
        } else {
            return ResponseEntity.ok(Map.of("mensaje", "No hubo cambios para actualizar."));
        }
    }
}
