package com.praktica.praktica.repository;
import com.praktica.praktica.model.HistorialCambiosOrden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistorialCambiosOrdenRepository extends JpaRepository<HistorialCambiosOrden, Long> {
}
