package org.cibertec.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.cibertec.client.ServicioCitaClient;
import org.cibertec.dto.DetalleDto;
import org.cibertec.dto.DetalleVentaDTO;
import org.cibertec.entity.DetalleOrden;
import org.cibertec.entity.DetalleProducto;
import org.cibertec.entity.DetalleServicio;
import org.cibertec.entity.Mascota;
import org.cibertec.entity.Orden;
import org.cibertec.entity.Producto;
import org.cibertec.entity.Servicio;
import org.cibertec.entity.Usuario;
import org.cibertec.service.DetalleOrdenService;
import org.cibertec.service.OrdenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ordenCompra")
public class OrdenCompraController {
	@Autowired
	private OrdenService oSer;
	
	@Autowired
	private DetalleOrdenService detOrdSer;
	
	@Autowired
	private ServicioCitaClient servCitCli;
	
	
	//todas las ordenes para el administrador
	@GetMapping("/ordenes")
	public ResponseEntity<List<Orden>> listarOrdenes(@RequestHeader(name = "Authorization", required = false) String token){
		if (token == null || token.isEmpty() || !token.startsWith("Bearer ")) {
	        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
	    }
		List<Orden> ordenes =oSer.listaOrden();
		return ResponseEntity.ok(ordenes);
	}
	
	//Listar las ordenes de cada cliente
	@GetMapping("/ordenesClientes")
	public ResponseEntity<List<Orden>> ListarOrdenPorUsuario(@RequestHeader(name = "Authorization", required = false) String token,
			@RequestParam Long idUsuario){
		if (token == null || token.isEmpty() || !token.startsWith("Bearer ")) {
	        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
	    }
		if (Objects.isNull(idUsuario)) {
	        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	    }
        
        Usuario usuario=new Usuario();
        usuario.setIdUsuario(idUsuario);
        List<Orden> ordenes = oSer.buscarPorUsuario(usuario);
		return ResponseEntity.ok(ordenes);
	}
	
	//Detalle de Ordenes
	@GetMapping("/detalleOrden/{idOrden}")
	public ResponseEntity<?> listarDetalleOrden(@RequestHeader(name = "Authorization", required = false) String token,
			@PathVariable int idOrden){
		if (token == null || token.isEmpty() || !token.startsWith("Bearer ")) {
	        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
	    }
		Optional<Orden> ordenOpt = oSer.buscarPorId(idOrden);
		
		if (!ordenOpt.isPresent()) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                .body("Orden con ID " + idOrden + " no encontrada");
	    }
		Orden orden = ordenOpt.get();
		
	    List<?> detalle = detOrdSer.buscarPorOrd(orden);
		
		return ResponseEntity.ok(detalle);
	}
	
	//Detalle de Ventas para veterinarios
	@GetMapping("/listaVentas")
	public ResponseEntity<List<DetalleVentaDTO>> ListarVentasVeterinarios(@RequestHeader(name = "Authorization", required = false) String token,
			@RequestParam Long idUsuario){
		if (token == null || token.isEmpty() || !token.startsWith("Bearer ")) {
	        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
	    }
		if (Objects.isNull(idUsuario)) {
	        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	    }
        
        List<DetalleProducto> detalleProd = detOrdSer.buscarProductoPorUsuarioId(idUsuario);
        List<DetalleServicio> detalleServ = detOrdSer.buscarServicioPorUsuarioId(idUsuario);
        
        
        List<DetalleVentaDTO> detallesCombinados= new ArrayList<>();
        
        for(DetalleOrden detOrd:detalleProd) {
        	detallesCombinados.add(mapADto(detOrd));
        }
        for(DetalleOrden detOrd:detalleServ) {
        	detallesCombinados.add(mapADto(detOrd));
        }
        
        return ResponseEntity.ok(detallesCombinados);
        
	}
	
	
	@PostMapping("/procesarCarrito")
	public ResponseEntity<?> procesarOrdenCarrito(@RequestHeader(name = "Authorization", required = false) String token,
	        @RequestParam Long idUsuario) {

	    if (token == null || !token.startsWith("Bearer ")) {
	        return new ResponseEntity<>("Token requerido.", HttpStatus.UNAUTHORIZED);
	    }

	    List<DetalleDto> detalles;
	    try {
	        detalles = detOrdSer.carritoPorUsuario(token, idUsuario);
	    } catch (Exception e) {
	        return new ResponseEntity<>("Error al obtener carrito: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	    }

	    if (detalles.isEmpty()) {
	        return new ResponseEntity<>("El carrito está vacío.", HttpStatus.BAD_REQUEST);
	    }
	    
	    if (detalles == null || detalles.isEmpty()) {
	        return new ResponseEntity<>("El carrito está vacío o no se pudo obtener.", HttpStatus.BAD_REQUEST);
	    }

	    return procesarDetalles(idUsuario,detalles, token);
	}
	
	@PostMapping("/procesarCita")
	public ResponseEntity<?> procesarOrdenCita(@RequestHeader(name = "Authorization", required = false) String token,
	        @RequestParam Long idUsuario, @RequestParam Integer idServicio,
	        @RequestParam Long idMascota, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaCita) {

	    if (token == null || !token.startsWith("Bearer ")) {
	        return new ResponseEntity<>("Token requerido.", HttpStatus.UNAUTHORIZED);
	    }

	    DetalleDto detalle;
	    try {
	        detalle = detOrdSer.citaPorUsuario(token, idUsuario, idServicio, idMascota, fechaCita);
	    } catch (Exception e) {
	        return new ResponseEntity<>("Error al obtener cita: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	    
	    if (detalle == null) {
	        return new ResponseEntity<>("No se encontró la cita o los datos están incompletos.", HttpStatus.BAD_REQUEST);
	    }

	    return procesarDetalles(idUsuario,List.of(detalle), token);
	}
	
	
	private ResponseEntity<?> procesarDetalles(Long idUsuario,List<DetalleDto> detalles, String token) {
		
		List<DetalleDto> detallesValidos = detalles.stream()
	            .filter(d -> d.getIdProducto() != null || d.getIdServicio() != null)
	            .toList();

	    if (detallesValidos.isEmpty()) {
	        return new ResponseEntity<>("No hay detalles válidos para procesar.", HttpStatus.BAD_REQUEST);
	    }
		
		Usuario usuario = new Usuario();
	    usuario.setIdUsuario(idUsuario);

	    Orden orden = new Orden();
	    orden.setFecha(new Date());
	    orden.setUsuario(usuario);

	    double total = detalles.stream().mapToDouble(DetalleDto::getTotal).sum();
	    orden.setTotal(total);
	    orden.setComisionTotal(total * 0.15);

	    Orden ordenGuardada = oSer.guardar(orden);

	    List<Integer> detallesGuardados = new ArrayList<>();
	    List<Map<String, Integer>> productosProcesados = new ArrayList<>();

	    try {
	        for (DetalleDto detalle : detalles) {
	            if (detalle.getIdServicio() != null) {
	            	try {
	            		DetalleServicio ds = new DetalleServicio();
		                ds.setOrden(ordenGuardada);
		                ds.setNombre(detalle.getNombre());
		                ds.setPrecio(detalle.getPrecio());
		                ds.setTotal(detalle.getTotal());
		                ds.setComision(detalle.getTotal() * 0.15);
		                ds.setFechaCita(detalle.getFechaCita());
	
		                Servicio servicio = new Servicio();
		                servicio.setIdServicio(detalle.getIdServicio());
		                ds.setServicio(servicio);
	
		                Mascota mascota = new Mascota();
		                mascota.setIdMascota(detalle.getIdMascota());
		                ds.setMascota(mascota);
	
		                DetalleOrden guardado = detOrdSer.guardar(ds);
		                detallesGuardados.add(guardado.getIdDetalle());
	            	}catch (Exception e) {
	            		System.err.println("Error guardando servicio: " + e.getMessage());
	            	}
	                

	            } else if (detalle.getIdProducto() != null) {
	            	
	            	try {
	                    detOrdSer.eliminarProductoCarrito(token, idUsuario, detalle.getIdProducto());
	                } catch (Exception e) {
	                    throw new RuntimeException("Error al eliminar producto del carrito (ID: " + detalle.getIdProducto()+"-" +idUsuario+ "): " + e.getMessage());
	                }
	            	try {
						Producto producto = new Producto();
		                producto.setIdProducto(detalle.getIdProducto());
	
		                DetalleProducto dp = new DetalleProducto();
		                dp.setOrden(ordenGuardada);
		                dp.setNombre(detalle.getNombre());
		                dp.setPrecio(detalle.getPrecio());
		                dp.setTotal(detalle.getTotal());
		                dp.setCantidad(detalle.getCantidad());
		                dp.setProducto(producto);
		                dp.setComision(detalle.getTotal() * 0.15);
	
		                DetalleOrden guardado = detOrdSer.guardar(dp);
		                detallesGuardados.add(guardado.getIdDetalle());
					} catch (Exception e) {
						System.err.println("Error guardando producto: " + e.getMessage());
					}
	            	
	                try {
						Map<String, Integer> productoInfo = new HashMap<>();
		                productoInfo.put("idProducto", detalle.getIdProducto());
		                productoInfo.put("cantidad", detalle.getCantidad());
		                productosProcesados.add(productoInfo);
					} catch (Exception e) {
						System.err.println("Error consumiendo stock: " + e.getMessage());
					}

	                
	            }
	        }

	        for (Map<String, Integer> producto : productosProcesados) {
	            detOrdSer.consumirStockProductos(
	                producto.get("idProducto"),
	                producto.get("cantidad")
	            );
	        }
	        return new ResponseEntity<>(
	                "Orden No. " + ordenGuardada.getNumeroOrden() + " creada exitosamente.",
	                HttpStatus.CREATED
	        );

	    } catch (Exception e) {
	        for (Integer idDetalle : detallesGuardados) detOrdSer.eliminar(idDetalle);
	        oSer.eliminar(ordenGuardada.getNumeroOrden());
	        return new ResponseEntity<>("Error al procesar la orden: " + e.getMessage(),
	                HttpStatus.INTERNAL_SERVER_ERROR);
	    }

	}

	
	
	
	private DetalleVentaDTO mapADto(DetalleOrden detOrd) {
	    DetalleVentaDTO dto = new DetalleVentaDTO();
	    dto.setDetalleId(detOrd.getIdDetalle());
	    dto.setOrdenId(detOrd.getOrden().getNumeroOrden());
	    dto.setNombreItem(detOrd.getNombre());
	    dto.setPrecio(detOrd.getPrecio());
	    dto.setTotal(detOrd.getTotal());
	    dto.setIdUsuairo(detOrd.getOrden().getUsuario().getIdUsuario());
	    
	    if(detOrd instanceof DetalleProducto) {
	    	DetalleProducto detProd= (DetalleProducto) detOrd;
	    	dto.setTipoItem("Producto");
	    	dto.setCantidad(detProd.getCantidad());
	    	dto.setFecha(null);
	    } 
	    if (detOrd instanceof DetalleServicio) {
	        DetalleServicio detServ = (DetalleServicio) detOrd;
	        dto.setTipoItem("Servicio");
	        dto.setCantidad(1);
	        dto.setFecha(detServ.getFechaCita());
	        dto.setIdMascota(detServ.getMascota().getIdMascota());
	    }
	    return dto;
	}
}
