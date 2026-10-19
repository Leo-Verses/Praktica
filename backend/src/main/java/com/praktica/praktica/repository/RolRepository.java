package com.praktica.praktica.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.praktica.praktica.model.Rol;
import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Integer> {
    Optional<Rol> findByNombreRol(String nombreRol);

    Optional<Rol> findByIdRol(Integer idRol);
}