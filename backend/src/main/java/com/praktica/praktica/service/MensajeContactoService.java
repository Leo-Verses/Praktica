package com.praktica.praktica.service;

import com.praktica.praktica.model.MensajeContacto;
import com.praktica.praktica.repository.MensajeContactoRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class MensajeContactoService {

    private final MensajeContactoRepository repository;

    public MensajeContactoService(MensajeContactoRepository repository) {
        this.repository = repository;
    }

    public MensajeContacto guardarMensaje(MensajeContacto mensaje) {
        return repository.save(mensaje);
    }

    public List<MensajeContacto> listarMensajes() {
        return repository.findAll();
    }

    public Optional<MensajeContacto> obtenerPorId(Integer id) {
        return repository.findById(id);
    }

    public MensajeContacto actualizarEstado(Integer id, MensajeContacto.EstadoMensaje estado) {
        MensajeContacto mensaje = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mensaje no encontrado"));
        mensaje.setEstado(estado);
        return repository.save(mensaje);
    }

    public void eliminarMensaje(Integer id) {
        repository.deleteById(id);
    }
}