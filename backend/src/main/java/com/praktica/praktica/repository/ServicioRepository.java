package com.praktica.praktica.repository;
import com.praktica.praktica.model.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ServicioRepository extends JpaRepository<Servicio, Long> {
    List<Servicio> findByEstado(String estado);

    @Query("SELECT s FROM Servicio s WHERE s.nombre LIKE %:termino% OR s.descripcion LIKE %:termino%")
    List<Servicio> buscarPorNombreDescripcion(String termino);
}