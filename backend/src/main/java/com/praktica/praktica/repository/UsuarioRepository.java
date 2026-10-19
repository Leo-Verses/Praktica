package com.praktica.praktica.repository;

import com.praktica.praktica.model.Usuario;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Usuario findByEmail(String email);

    List<Usuario> findByRol_NombreRolIn(List<String> roles);

    Page<Usuario> findByRol_NombreRolIn(List<String> roles, Pageable pageable);

    Page<Usuario> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCaseOrEmailContainingIgnoreCase(
        String nombre, String apellido, String email, Pageable pageable);

    Page<Usuario> findByRol_NombreRolInAndNombreContainingIgnoreCaseOrRol_NombreRolInAndApellidoContainingIgnoreCaseOrRol_NombreRolInAndEmailContainingIgnoreCase(
        List<String> roles1, String nombre,
        List<String> roles2, String apellido,
        List<String> roles3, String email,
        Pageable pageable);

    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.rol.idRol = 1 AND u.estado = 'activo'")
        long contarClientesConectados();

    long countByRol_IdRolInAndEstadoIgnoreCase(List<Long> roles, String estado);
}
