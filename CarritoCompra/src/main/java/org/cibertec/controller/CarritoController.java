package org.cibertec.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.cibertec.dto.DetalleDto;
import org.cibertec.entity.CarritoCompra;
import org.cibertec.service.CarritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/carrito")
public class CarritoController {
	@Autowired
	private CarritoService cSer;
	
	@GetMapping("/usuario")
	public ResponseEntity<List<DetalleDto>> obtenerCarrito(@RequestHeader(name = "Authorization", required = false) String token,
			@RequestParam Long idUsuario) {
		
		if (token == null || token.isEmpty() || !token.startsWith("Bearer ")) {
	        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
	    }
		
	    List<CarritoCompra> carrito = cSer.obtenerCarritoPorUsuario(idUsuario);
	    List<DetalleDto> detalles = carrito.stream().map(item -> {
	        DetalleDto dto = new DetalleDto();
	        dto.setIdProducto(item.getIdProducto());
	        dto.setNombre(item.getNombreProducto());
	        dto.setCantidad(item.getCantidad());
	        dto.setPrecio(item.getPrecio());
	        dto.setTotal(item.getTotal());
	        return dto;
	    }).collect(Collectors.toList());

	    return ResponseEntity.ok(detalles);
	}
	
	@PostMapping("/agregar")
	public ResponseEntity<CarritoCompra> agregarProducto(@RequestHeader(name = "Authorization", required = false) String token,
	        @RequestParam Long idUsuario,
	        @RequestParam Integer idProducto,
	        @RequestParam Integer cantidad) {
	    
		
		if (token == null || token.isEmpty() || !token.startsWith("Bearer ")) {
	        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
	    }
		if (cantidad<0) {
	        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	    }
		
	    CarritoCompra carrito = cSer.agregarProducto(idUsuario, idProducto, cantidad);
	    return ResponseEntity.ok(carrito);
	}
	
	@DeleteMapping("/eliminar")
	public ResponseEntity<String> eliminarProducto(@RequestHeader(name = "Authorization", required = false) String token,
	        @RequestParam Long idUsuario,
	        @RequestParam Integer idProducto) {
		
		if (token == null || token.isEmpty() || !token.startsWith("Bearer ")) {
	        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
	    }
		
	    try {
	        cSer.eliminarProducto(idUsuario, idProducto);
	        return ResponseEntity.ok("Producto eliminado del carrito correctamente");
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Error al eliminar el producto: " + e.getMessage());
	    }
	}



}
