package org.cibertec.service;



import java.util.List;
import java.util.Optional;

import org.cibertec.entity.Rol;
import org.cibertec.entity.Usuario;
import org.cibertec.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class UsuarioService {
	
	@Autowired
    private UsuarioRepository usuarioRepository;
	
	
	BCryptPasswordEncoder pasEncode = new BCryptPasswordEncoder();
	

	// LISTAR TODOS LOS USUARIOS
	@CircuitBreaker(name = "usuarioService", fallbackMethod = "fallbackListarTodos")
	@Retry(name="usuarioService")
    public List<Usuario> listarTodos() {
					
        return usuarioRepository.findAll();
    }

    // BUSCAR USUARIO POR ID
	@CircuitBreaker(name = "usuarioService", fallbackMethod = "fallbackBuscarPorId")
	@Retry(name="usuarioService")
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }
    
    
    // BUSCAR USUARIO POR ROL
	@CircuitBreaker(name = "usuarioService", fallbackMethod = "fallbackBuscarPorRol")
	@Retry(name="usuarioService")
    public List<Usuario> buscarPorRol(Long idRol) {
        Rol rol=new Rol();
        rol.setIdRol(idRol);
        
        
        return usuarioRepository.findByRol(rol);
    }


    // CREAR NUEVO USUARIO
	@CircuitBreaker(name = "usuarioService", fallbackMethod = "fallbackCrearUsuario")
	@Retry(name="usuarioService")
    public Usuario crearUsuario(Usuario usuario) {
    	
    	
    	usuario.setPassword(pasEncode.encode(usuario.getPassword()));
        
        Rol rol = new Rol();
        Long idRol=3L;
        
        
        if (usuarioRepository.findByUsername(usuario.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario ya existe");
        }
        
        if (usuarioRepository.findByNumeroDocumento(usuario.getNumeroDocumento()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Este documento ya existe");
        }
        
        if (usuario.getRuc() != null && !usuario.getRuc().trim().isEmpty()) {
            if (usuarioRepository.findByRuc(usuario.getRuc()).isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El RUC ya existe");
            }
            idRol=2L;
        }

        if (usuarioRepository.findByCorreo(usuario.getCorreo()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo ya existe");
        }
        
        rol.setIdRol(idRol);
        usuario.setRol(rol);
    	
        return usuarioRepository.save(usuario);
    }
    
	@CircuitBreaker(name = "usuarioService", fallbackMethod = "fallbackActualizarUsuario")
	@Retry(name="usuarioService")
    public Usuario actualizarUsuario(Usuario nuevoUsuario) {
    	
    	
    	Usuario usuario = buscarPorId(nuevoUsuario.getIdUsuario())
    	        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado con ID: " + nuevoUsuario.getIdUsuario()));
    	
    	Optional<Usuario> username = usuarioRepository.findByUsername(nuevoUsuario.getUsername());
    	if (username.isPresent() && !username.get().getIdUsuario().equals(nuevoUsuario.getIdUsuario())) {
    	    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario ya existe.");
    	}
    	Optional<Usuario> documento = usuarioRepository.findByNumeroDocumento(nuevoUsuario.getNumeroDocumento());
    	if (documento.isPresent() && !documento.get().getIdUsuario().equals(nuevoUsuario.getIdUsuario())) {
    	    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Este documento ya existe.");
    	}
    	Optional<Usuario> ruc = usuarioRepository.findByRuc(nuevoUsuario.getRuc());
    	if (ruc.isPresent() && !ruc.get().getIdUsuario().equals(nuevoUsuario.getIdUsuario())) {
    	    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El RUC ya existe.");
    	}
    	Optional<Usuario> correo = usuarioRepository.findByCorreo(nuevoUsuario.getCorreo());
    	if (correo.isPresent() && !correo.get().getIdUsuario().equals(nuevoUsuario.getIdUsuario())) {
    	    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo ya existe.");
    	}
    	usuario.setUsername(nuevoUsuario.getUsername());
        usuario.setNumeroDocumento(nuevoUsuario.getNumeroDocumento());
        usuario.setRuc(nuevoUsuario.getRuc());
        usuario.setCorreo(nuevoUsuario.getCorreo());
        usuario.setApellido(nuevoUsuario.getApellido());
        usuario.setDireccion(nuevoUsuario.getDireccion());
        usuario.setNombre(nuevoUsuario.getNombre());
        usuario.setTelefono(nuevoUsuario.getTelefono());
        if (nuevoUsuario.getPassword() != null && !nuevoUsuario.getPassword().isEmpty()) {
            usuario.setPassword(pasEncode.encode(nuevoUsuario.getPassword()));
        }
        
        try {
        	return usuarioRepository.save(usuario);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Algo salió mal al actualizar.");
        }
        
    }


    // ELIMINAR USUARIO
	
	@CircuitBreaker(name = "usuarioService", fallbackMethod = "fallbackEliminarUsuario")
	@Retry(name="usuarioService")
    public String eliminarUsuario(Long id) {
        if (usuarioRepository.existsById(id)) {
        	
        	 try { 
        		usuarioRepository.deleteById(id);
                return "Eliminacion exitosa";
        		 
        	 } catch (Exception e) {
                 throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
             }
            
        } else {
        	throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
	

    
	/*/*****************************/
	/********** FallBacks **********/ 
	/*/*****************************/
    

	public List<Usuario> fallbackListarTodos(Throwable ex) {
        System.err.println("Fallback en listar todos los usuarios : " + ex.getMessage());
        
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(0L);
        usuario.setNombre("No se encontro usuarios");
        usuario.setApellido("");
        usuario.setNumeroDocumento("");
        usuario.setTelefono("");
        usuario.setDireccion("");
        usuario.setCorreo("");
        usuario.setRuc("");
        
        return List.of(usuario);
    }
    
	public Usuario fallbackBuscarPorId(Long id, Throwable ex) {
        System.err.println("Fallback en buscar usuario por id="+id + ": "+ ex.getMessage());
        
        
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(0L);
        usuario.setNombre("No se encontro usuario con el id");
        usuario.setApellido("");
        usuario.setNumeroDocumento("");
        usuario.setTelefono("");
        usuario.setDireccion("");
        usuario.setCorreo("");
        usuario.setRuc("");
        
        
        return usuario;
    }
    
	public List<Usuario> fallbackBuscarPorRol(Long idRol, Throwable ex) {
        System.err.println("Fallback en buscar usuarios por rol id="+ idRol + ": "  + ex.getMessage());
        
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(0L);
        usuario.setNombre("No se encontro usuarios con el rol");
        usuario.setApellido("");
        usuario.setNumeroDocumento("");
        usuario.setTelefono("");
        usuario.setDireccion("");
        usuario.setCorreo("");
        usuario.setRuc("");
        
        return List.of(usuario);
    }
    
	public Usuario fallbackCrearUsuario(Usuario usuario, Throwable ex) throws Exception {
        System.err.println("Fallback en crear usuario: " + ex.getMessage());
        throw new Exception();
    }
    
	public Usuario fallbackActualizarUsuario(Usuario usuario, Throwable ex) throws Exception {
        System.err.println("Fallback en actualizar usuario: " + ex.getMessage());
        throw new Exception();
    }
    
	public String fallbackEliminarUsuario(Long id, Throwable ex) {
		
		 System.err.println("Fallback en eliminar el usuario con id=" + id + ": " + ex.getMessage());
		 return "Error";
    }
		
       

}