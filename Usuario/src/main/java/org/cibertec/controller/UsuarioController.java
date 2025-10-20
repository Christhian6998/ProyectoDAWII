package org.cibertec.controller;

import java.util.List;
import java.util.Optional;

import org.cibertec.entity.Rol;
import org.cibertec.entity.Usuario;
import org.cibertec.repository.UsuarioRepository;
import org.cibertec.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/usuario")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UsuarioController {
	@Autowired
    private UsuarioService usuarioService;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	BCryptPasswordEncoder pasEncode = new BCryptPasswordEncoder();

	// LISTAR TODOS LOS USUARIOS
    @GetMapping
    public ResponseEntity<List<Usuario>> listaUsuarios() {
        List<Usuario> usuarios = usuarioService.listarTodos();
        return ResponseEntity.ok(usuarios);
    }

    // BUSCAR USUARIO POR ID
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarUsuarioPorId(@PathVariable Long id) {
        return usuarioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    // CREAR NUEVO USUARIO
    @PostMapping
    public ResponseEntity<Usuario> crearUsuario(@RequestBody Usuario usuario) {
    	try {
	        usuario.setPassword(pasEncode.encode(usuario.getPassword()));
	        Rol rol=new Rol();
	        Long nuevoRol=3L;
	        
	        if (usuarioRepository.findByUsername(usuario.getUsername()) != null) {
	        	throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "El usuario ya existe");
	        }else if (usuarioRepository.findByNumeroDocumento(usuario.getNumeroDocumento()) != null) {
	        	throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Este documento ya existe");
	        }else if (usuarioRepository.findByRuc(usuario.getRuc()) != null) {
	        	throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "El RUC ya existe");
	        }else if (usuarioRepository.findByCorreo(usuario.getCorreo()) != null) {
	        	throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "El correo ya existe");
	        }
	        if (usuario.getUsername() != null) {
	        	nuevoRol=2L;
	        }
	        rol.setIdRol(nuevoRol);
	        usuario.setRol(rol);

	        Usuario nuevo = usuarioService.crearUsuario(usuario);

	        return ResponseEntity.ok(nuevo);
	    } catch (Exception e) {
	    	throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Algo salio mal al registrar usuario");
	    }
    }

    // ACTUALIZAR USUARIO
    @PutMapping
    public ResponseEntity<Usuario> actualizarUsuario( @RequestBody Usuario u) {
    	Usuario usuario = usuarioService.buscarPorId(u.getIdUsuario())
    	        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado con ID: " + u.getIdUsuario()));
    	
    	Optional<Usuario> username = usuarioRepository.findByUsername(u.getUsername());
    	if (username.isPresent() && !username.get().getIdUsuario().equals(u.getIdUsuario())) {
    	    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "El usuario ya existe.");
    	}
    	Optional<Usuario> documento = usuarioRepository.findByNumeroDocumento(u.getNumeroDocumento());
    	if (documento.isPresent() && !documento.get().getIdUsuario().equals(u.getIdUsuario())) {
    	    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Este documento ya existe.");
    	}
    	Optional<Usuario> ruc = usuarioRepository.findByRuc(u.getRuc());
    	if (ruc.isPresent() && !ruc.get().getIdUsuario().equals(u.getIdUsuario())) {
    	    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "El RUC ya existe.");
    	}
    	Optional<Usuario> correo = usuarioRepository.findByCorreo(u.getCorreo());
    	if (correo.isPresent() && !correo.get().getIdUsuario().equals(u.getIdUsuario())) {
    	    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "El correo ya existe.");
    	}
    	usuario.setUsername(u.getUsername());
        usuario.setNumeroDocumento(u.getNumeroDocumento());
        usuario.setRuc(u.getRuc());
        usuario.setCorreo(u.getCorreo());
        usuario.setApellido(u.getApellido());
        usuario.setDireccion(u.getDireccion());
        usuario.setNombre(u.getNombre());
        usuario.setTelefono(u.getTelefono());
        if (u.getPassword() != null && !u.getPassword().isEmpty()) {
            usuario.setPassword(pasEncode.encode(u.getPassword()));
        }
        
        try {
            Usuario usuarioActualizado = usuarioService.crearUsuario(usuario); 
            return ResponseEntity.ok(usuarioActualizado);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Algo salió mal al actualizar.");
        }
    }

    // ELIMINAR USUARIO
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    
    // BUSCAR USUARIOS POR ROL
    @GetMapping("/rol/{idRol}")
    public ResponseEntity<List<Usuario>> buscarUsuariosPorRol(@PathVariable Long idRol) {
        Rol rol=new Rol();
        rol.setIdRol(idRol);
    	List<Usuario> usuarios = usuarioService.buscarPorRol(rol);
        return ResponseEntity.ok(usuarios);
    }

}