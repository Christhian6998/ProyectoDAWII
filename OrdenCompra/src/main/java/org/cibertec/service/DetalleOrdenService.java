package org.cibertec.service;

import java.util.Date;
import java.util.List;

import org.cibertec.client.CarritoClient;
import org.cibertec.client.ProductoClient;
import org.cibertec.client.ServicioCitaClient;
import org.cibertec.dto.DetalleDto;
import org.cibertec.entity.DetalleOrden;
import org.cibertec.entity.DetalleProducto;
import org.cibertec.entity.DetalleServicio;
import org.cibertec.entity.Orden;
import org.cibertec.entity.Servicio;
import org.cibertec.repository.DetalleOrdenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@Service
public class DetalleOrdenService {
	@Autowired
	private DetalleOrdenRepository detOrdRep;
	
	@Autowired
	private ProductoClient prodCli;
	
	@Autowired
	private CarritoClient cCli;
	
	@Autowired
	private ServicioCitaClient serCitCli;
	
	@CircuitBreaker(name = "detalleOrdenService", fallbackMethod = "fallbackGuardar")
    @Retry(name = "detalleOrdenService")
	public DetalleOrden guardar(DetalleOrden det) {
		return detOrdRep.save(det);
	}
	
	@CircuitBreaker(name = "detalleOrdenService", fallbackMethod = "fallbackBuscarPorOrd")
    @Retry(name = "detalleOrdenService")
	public List<DetalleOrden> buscarPorOrd(Orden ord){
		return detOrdRep.findByOrden(ord);
	}
	
	@CircuitBreaker(name = "detalleOrdenService", fallbackMethod = "fallbackBuscarProdPorUsuario")
    @Retry(name = "detalleOrdenService")
	public List<DetalleProducto> buscarProductoPorUsuarioId(Long idUsuario) {
        return detOrdRep.findDetallesProductosByUsuarioId(idUsuario);
    }
	
	
	@CircuitBreaker(name = "detalleOrdenService", fallbackMethod = "fallbackBuscarServPorUsuario")
    @Retry(name = "detalleOrdenService")
	public List<DetalleServicio> buscarServicioPorUsuarioId(Long idUsuario) {
        return detOrdRep.findDetallesServicioByUsuarioId(idUsuario);
    }
	
	
	@CircuitBreaker(name = "detalleOrdenService", fallbackMethod = "fallbackEliminar")
    @Retry(name = "detalleOrdenService")
	public void eliminar(Integer id) {
		detOrdRep.deleteById(id);
	}
	
    @CircuitBreaker(name = "detalleOrdenService", fallbackMethod = "fallbackConsumirStockProductos")
    @Retry(name = "detalleOrdenService")
    public void consumirStockProductos(Integer id, int cantidad) {
    	prodCli.restarStockProductoPorID(id, cantidad);
    }
    
    @CircuitBreaker(name = "detalleOrdenService", fallbackMethod = "fallbackProcesarCarrito")
    @Retry(name = "detalleOrdenService")
    public List<DetalleDto> carritoPorUsuario(String token,Long idUsuario) {
        ResponseEntity<List<DetalleDto>> response = cCli.obtenerCarrito(token,idUsuario);
        List<DetalleDto> detalles = response.getBody();
        
        if (detalles == null || detalles.isEmpty()) {
            throw new RuntimeException("El carrito está vacío o no se pudo obtener.");
        }
        return detalles;
    }
    
    @CircuitBreaker(name = "detalleOrdenService", fallbackMethod = "fallbackVaciarCarrito")
    @Retry(name = "detalleOrdenService")
    public void eliminarProductoCarrito(String token, Long idUsuario, Integer idProducto) {
    	cCli.eliminarProducto(token, idUsuario, idProducto);
    }
    
    @CircuitBreaker(name = "detalleOrdenService", fallbackMethod = "fallbackCitaPorUsuario")
    @Retry(name = "detalleOrdenService")
    public DetalleDto citaPorUsuario(String token, Long idUsuario, Integer idServicio, Long idMascota, Date fechaCita) {
        ResponseEntity<DetalleDto> response = serCitCli.nuevaCita(token, idUsuario, idServicio, idMascota, fechaCita);
        DetalleDto detalle = response.getBody();
     
        if (detalle == null) {
            throw new RuntimeException("No se pudo obtener la cita del usuario.");
        }
        return detalle;
    }
    
    
    /* ************************************ */
    /* ************ FallBacks ************* */
    /* ************************************ */

    public DetalleOrden fallbackGuardar(DetalleOrden det, Throwable ex) {
        DetalleOrden detalle =new DetalleOrden();
        detalle.setNombre("Error en guardar DetalleOrden: " + ex.getMessage());
        return detalle;
    }

    public List<DetalleOrden> fallbackBuscarPorOrd(Orden ord, Throwable ex) {
        DetalleOrden detalle = new DetalleOrden();
        detalle.setNombre("Error al buscar detalles por orden: " + ex.getMessage());
        return List.of(detalle);
    }

    public List<DetalleProducto> fallbackBuscarProdPorUsuario(Long idUsuario, Throwable ex) {
        DetalleProducto dp = new DetalleProducto();
        dp.setNombre("Error al obtener productos del usuario: " + ex.getMessage());
        return List.of(dp);
    }

    public List<DetalleServicio> fallbackBuscarServPorUsuario(Long idUsuario, Throwable ex) {
        DetalleServicio ds = new DetalleServicio();
        ds.setNombre("Error al obtener servicios del usuario: " + ex.getMessage());
        return List.of(ds);
    }

    public void fallbackEliminar(Integer id, Throwable ex) {
        System.err.println("Error al eliminar DetalleOrden: " + ex.getMessage());
    }

    public void fallbackConsumirStockProductos(List<DetalleDto> detalles, Throwable ex) {
        System.err.println("Error al consumir stock de productos: " + ex.getMessage());
    }
    
    public List<DetalleDto> fallbackProcesarCarrito(String token,Long idUsuario, Throwable ex) {
        DetalleDto detalle = new DetalleDto();
        detalle.setNombre("Error al procesar carrito: " + ex.getMessage());
        return List.of(detalle);
    }

    
    public DetalleDto fallbackCitaPorUsuario(String token, Long idUsuario, Integer idServicio, Long idMascota, Date fechaCita, Throwable ex) {
        DetalleDto detalle = new DetalleDto();
        detalle.setNombre("Error al obtener la cita: " + ex.getMessage());
        return detalle;
    }
    
    public void fallbackVaciarCarrito(String token, Long idUsuario, Integer idProducto, Throwable t) {
        System.err.println("Fallback: no se pudo eliminar producto del carrito. " + t.getMessage());
    }
    public void fallbackConsumirStockProductos(Integer idProducto, int cantidad, Throwable t) {
        System.err.println("Fallback: no se pudo consumir stock del producto " + idProducto);
    }

}
