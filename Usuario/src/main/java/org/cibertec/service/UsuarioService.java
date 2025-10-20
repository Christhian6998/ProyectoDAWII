package org.cibertec.service;



import java.util.List;
import java.util.Optional;
import org.cibertec.entity.Rol;
import org.cibertec.entity.Usuario;
import org.cibertec.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class UsuarioService {
	@Autowired
    private UsuarioRepository usuarioRepository;
	

	// LISTAR TODOS LOS USUARIOS
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    // BUSCAR USUARIO POR ID
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }


    // CREAR NUEVO USUARIO
    public Usuario crearUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }


    // ELIMINAR USUARIO
    public void eliminarUsuario(Long id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
        } else {
            throw new RuntimeException("Usuario no encontrado con id: " + id);
        }
    }

    // BUSCAR USUARIOS POR ROL
    public List<Usuario> buscarPorRol(Rol rol) {
        return usuarioRepository.findByRol(rol);
    }

}