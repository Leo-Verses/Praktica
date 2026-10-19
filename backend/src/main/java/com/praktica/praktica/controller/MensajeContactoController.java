package com.praktica.praktica.controller;

import com.praktica.praktica.model.MensajeContacto;
import com.praktica.praktica.service.MensajeContactoService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/contacto")
public class MensajeContactoController {

    private final MensajeContactoService service;

    public MensajeContactoController(MensajeContactoService service) {
        this.service = service;
    }

    @PostMapping
    public MensajeContacto crearMensaje(@RequestBody MensajeContacto mensaje) {
        return service.guardarMensaje(mensaje);
    }

    @GetMapping
    public List<MensajeContacto> listarMensajes() {
        return service.listarMensajes();
    }

    @GetMapping("/{id}")
    public MensajeContacto obtenerMensaje(@PathVariable Integer id) {
        return service.obtenerPorId(id)
                .orElseThrow(() -> new RuntimeException("Mensaje no encontrado"));
    }

    @PutMapping("/{id}/estado")
    public MensajeContacto actualizarEstado(
            @PathVariable Integer id,
            @RequestParam("estado") MensajeContacto.EstadoMensaje estado
    ) {
        return service.actualizarEstado(id, estado);
    }
    
    @DeleteMapping("/{id}")
    public void eliminarMensaje(@PathVariable Integer id) {
        service.eliminarMensaje(id);
    }
}