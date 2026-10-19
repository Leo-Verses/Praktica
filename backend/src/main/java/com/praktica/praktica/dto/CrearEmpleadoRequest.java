package com.praktica.praktica.dto;

/** Datos recibidos al registrar un empleado. Nunca se devuelve la contrasena. */
public record CrearEmpleadoRequest(
        String nombre,
        String apellido,
        String email,
        String contrasena,
        RolRequest rol
) {
    public record RolRequest(Integer idRol) { }
}
