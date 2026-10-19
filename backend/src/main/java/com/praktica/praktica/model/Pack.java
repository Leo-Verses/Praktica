package com.praktica.praktica.model;

import jakarta.persistence.*;

@Entity
@Table(name = "packs")
public class Pack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_pack;

    private String nombre_pack;
    private String descripcion;
    private Double precio_pack;
    private String estado;

    public Long getId_pack() { return id_pack; }
    public void setId_pack(Long id_pack) { this.id_pack = id_pack; }

    public String getNombre_pack() { return nombre_pack; }
    public void setNombre_pack(String nombre_pack) { this.nombre_pack = nombre_pack; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Double getPrecio_pack() { return precio_pack; }
    public void setPrecio_pack(Double precio_pack) { this.precio_pack = precio_pack; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}