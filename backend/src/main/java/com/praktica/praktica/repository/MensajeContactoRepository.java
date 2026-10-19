package com.praktica.praktica.repository;
import com.praktica.praktica.model.MensajeContacto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MensajeContactoRepository extends JpaRepository<MensajeContacto, Integer> {
}
