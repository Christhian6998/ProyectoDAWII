package org.cibertec.client;

import java.util.List;

import org.cibertec.dto.DetalleDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "carrito", url = "http://localhost:8080/carritocompra")
public interface CarritoClient {
	@GetMapping("/carrito/usuario")
    ResponseEntity<List<DetalleDto>> obtenerCarrito(@RequestHeader(name = "Authorization", required = false) String token,
    		@RequestParam Long idUsuario);

    @DeleteMapping("/carrito/eliminar")
    ResponseEntity<String> eliminarProducto(@RequestHeader(name = "Authorization", required = false) String token,
            @RequestParam Long idUsuario,
            @RequestParam Integer idProducto);

}
