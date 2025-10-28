package org.cibertec.repository;

import org.cibertec.entity.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IServicioRepository extends JpaRepository<Servicio, Integer> {
    List<Servicio> findByUsuarioIdUsuario(Long idUsuario);
}
