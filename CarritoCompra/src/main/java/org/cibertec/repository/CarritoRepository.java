package org.cibertec.repository;

import java.util.List;
import java.util.Optional;

import org.cibertec.entity.CarritoCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarritoRepository extends JpaRepository<CarritoCompra, Integer> {
	List<CarritoCompra> findByIdUsuario(Long idUsuario);
	void deleteByIdUsuarioAndIdProducto(Long idUsuario, Integer idProducto);
	Optional<CarritoCompra> findByIdUsuarioAndIdProducto(Long idUsuario, Integer idProducto);
}
