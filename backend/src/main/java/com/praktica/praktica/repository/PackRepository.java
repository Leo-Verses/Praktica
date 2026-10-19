package com.praktica.praktica.repository;
import com.praktica.praktica.model.Pack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
public interface PackRepository extends JpaRepository<Pack, Long> {
    List<Pack> findByEstado(String estado);

    @Query("SELECT p FROM Pack p WHERE p.nombre_pack LIKE %:termino% OR p.descripcion LIKE %:termino%")
    List<Pack> buscarPorNombreDescripcion(String termino);
}
