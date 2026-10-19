package com.praktica.praktica.model;

import jakarta.persistence.*;

@Entity
@Table(name = "servicios")
public class Servicio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_servicio;

    private String nombre;
    private String descripcion;
    private Double precio_base;
    private String estado;

    public Long getId_servicio() { return id_servicio; }
    public void setId_servicio(Long id_servicio) { this.id_servicio = id_servicio; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Double getPrecio_base() { return precio_base; }
    public void setPrecio_base(Double precio_base) { this.precio_base = precio_base; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
