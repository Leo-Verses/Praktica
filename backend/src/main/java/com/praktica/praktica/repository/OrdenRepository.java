package com.praktica.praktica.repository;
import com.praktica.praktica.model.Orden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrdenRepository extends JpaRepository<Orden, Long> {
    List<Orden> findByUsuario_IdUsuario(Long idUsuario);

    @Query("SELECT o.idServicio, SUM(o.cantidad) FROM Orden o WHERE o.tipoPedido = 'servicio' GROUP BY o.idServicio ORDER BY SUM(o.cantidad) DESC")
    List<Object[]> countServicios();

    @Query("SELECT o.idPack, SUM(o.cantidad) FROM Orden o WHERE o.tipoPedido = 'pack' GROUP BY o.idPack ORDER BY SUM(o.cantidad) DESC")
    List<Object[]> countPacks();

    List<Orden> findByTipoPedido(Orden.TipoPedido tipo);
    
    List<Orden> findByFechaCreacionBetween(LocalDateTime inicio, LocalDateTime fin);
}