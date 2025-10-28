package org.cibertec.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "Producto", url="http://localhost:8080/producto")
public interface ProductoClient {
	@PutMapping("producto/actualizar/restarStock")
	ResponseEntity<String> restarStockProductoPorID(@RequestParam int id,@RequestParam int stock);
}
