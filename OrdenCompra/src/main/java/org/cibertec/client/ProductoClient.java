package org.cibertec.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "Producto", url="http://localhost:8080/Producto")
public interface ProductoClient {
	@PutMapping("/actualizar/stock")
    ResponseEntity<String> actualizarStockProducto(@RequestParam("id") int idProducto,@RequestParam int cantidad);
}
