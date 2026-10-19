package com.praktica.praktica.repository;

import com.praktica.praktica.model.Pago;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {
        @Query("SELECT MONTH(p.fechaPago), SUM(p.monto) " +
                "FROM Pago p " +
                "GROUP BY MONTH(p.fechaPago) " +
                "ORDER BY MONTH(p.fechaPago)")
        List<Object[]> ventasPorMes();

        boolean existsByIdOrdenAndEstadoPago(Long idOrden, Pago.EstadoPago estado);

        @Query("SELECT p FROM Pago p WHERE p.fechaPago BETWEEN :inicio AND :fin")
        List<Pago> findPagosEntreFechas(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

        @Query("SELECT p FROM Pago p " +
                "JOIN FETCH p.orden o " +
                "WHERE p.estadoPago = 'completado' " +
                "AND (:fechaInicio IS NULL OR p.fechaPago >= :fechaInicio) " +
                "AND (:fechaFin IS NULL OR p.fechaPago <= :fechaFin) " +
                "ORDER BY p.fechaPago DESC")
        List<Pago> findPagosCompletadosConOrden(
                @Param("fechaInicio") LocalDateTime fechaInicio,
                @Param("fechaFin") LocalDateTime fechaFin
        );
}