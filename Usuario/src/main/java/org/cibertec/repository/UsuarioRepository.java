package org.cibertec.repository;

import java.util.List;
import java.util.Optional;

import org.cibertec.entity.Rol;
import org.cibertec.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
	// BUSCAR POR USERNAME
    Optional<Usuario> findByUsername(String username);
    
    // BUSCAR POR CORREO
    Optional<Usuario> findByCorreo(String correo);
    
    // BUSCAR POR NÚMERO DE DOCUMENTO
    Optional<Usuario> findByNumeroDocumento(String numeroDocumento);
    
    // BUSCAR POR RUC
    Optional<Usuario> findByRuc(String ruc);
    
    /*
    // VERIFICAR EXISTENCIAS
    Boolean existsByUsername(String username);
    Boolean existsByCorreo(String correo);
    Boolean existsByNumeroDocumento(String numeroDocumento);
    Boolean existsByRuc(String ruc);
    */
	
 // Buscar por ID de rol
    List<Usuario> findByRol(Rol rol);
    
  
}
