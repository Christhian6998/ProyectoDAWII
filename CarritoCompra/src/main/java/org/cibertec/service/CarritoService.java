package org.cibertec.service;

import java.util.List;
import java.util.Optional;

import org.cibertec.client.ProductoClient;
import org.cibertec.dto.ProductoResponseDTO;
import org.cibertec.entity.CarritoCompra;
import org.cibertec.repository.CarritoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;


@Service
public class CarritoService {
	@Autowired
    private CarritoRepository cRep;
	
    @Autowired
    private ProductoClient pCli;
    
    @CircuitBreaker(name = "carritoService", fallbackMethod = "fallbackObtenerProductos")
    @Retry(name = "carritoService")
    public List<CarritoCompra> obtenerCarritoPorUsuario(Long idUsuario) {
        return cRep.findByIdUsuario(idUsuario);
    }
    
    @CircuitBreaker(name = "carritoService", fallbackMethod = "fallbackAgregarProducto")
    @Retry(name = "carritoService")
    public CarritoCompra agregarProducto(Long idUsuario, Integer idProducto, Integer cantidad) {
    	Optional<CarritoCompra> existente = cRep.findByIdUsuarioAndIdProducto(idUsuario, idProducto);
        if (existente.isPresent()) {
            throw new RuntimeException("El producto ya existe en el carrito");
        }
    	
        ResponseEntity<ProductoResponseDTO> response = pCli.obtenerProductoPorID(idProducto);
        ProductoResponseDTO producto = response.getBody();

        if (producto == null || producto.getNombre().contains("no disponible")) {
            throw new RuntimeException("Producto no disponible o servicio de productos caído");
        }

        CarritoCompra carrito = new CarritoCompra();
        carrito.setIdUsuario(idUsuario);
        carrito.setIdProducto(idProducto);
        carrito.setNombreProducto(producto.getNombre());
        carrito.setDescripcion(producto.getDescripcion());
        carrito.setPrecio(producto.getPrecio());
        carrito.setCantidad(cantidad);
        carrito.setTotal(producto.getPrecio() * cantidad);

        return cRep.save(carrito);
    }
    
    @Transactional
    public void eliminarProducto(Long idUsuario, Integer idProducto) {
        cRep.deleteByIdUsuarioAndIdProducto(idUsuario, idProducto);
    }
    
    
    public List<CarritoCompra> fallbackObtenerProductos(Long idUsuario, Throwable ex) {
        CarritoCompra car=new CarritoCompra();
        car.setDescripcion("Fallback obtener carrito: " + ex.getMessage());
        return List.of(car);
    }

    public CarritoCompra fallbackAgregarProducto(Long idUsuario, Integer idProducto, Integer cantidad, Throwable ex) {
        CarritoCompra carrito = new CarritoCompra();
        carrito.setNombreProducto("Error al agregar producto: " + ex.getMessage());
        carrito.setCantidad(0);
        carrito.setTotal(0.0);
        return carrito;
    }

}
