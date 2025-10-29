package com.cibertec.email.client;

import java.util.List;

import org.cibertec.entity.Usuario;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "Usuario", url = "${usuario.url}/usuario/usuario")
public interface IUsuarioClient {

	@GetMapping("/rol/{idRol}")
    public ResponseEntity<List<Usuario>> buscarUsuariosPorRol(@PathVariable Long idRol);
}
