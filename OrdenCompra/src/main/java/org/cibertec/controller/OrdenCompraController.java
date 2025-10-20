package org.cibertec.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import org.cibertec.entity.Servicio;
import org.cibertec.entity.Usuario;
import org.cibertec.service.DetalleOrdenService;
import org.cibertec.service.OrdenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/ordenCompra")
@CrossOrigin(origins = "*")
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
		List<Orden> ordenes =oSer.listaOrden();
		return ResponseEntity.ok(ordenes);
	}
	
	//Listar las ordenes de cada cliente
	@GetMapping("/ordenesClientes")
	public ResponseEntity<List<Orden>> ListarOrdenPorUsuario(@RequestHeader(name = "Authorization", required = false) String token,
			@RequestParam Long idUsuario){
		//si el gateway garantiza el envio se elimina la validacion if
        if (idUsuario == null) {
        	throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Acceso denegado: El ID de Cliente no fue proporcionado en el encabezado.");
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
		//si el gateway garantiza el envio se elimina la validacion if
        if (idUsuario == null) {
        	throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Acceso denegado: El ID de Cliente no fue proporcionado en el encabezado.");
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
	
	
	@PostMapping("/procesar")
	public ResponseEntity<?> procesarOrden(@RequestHeader(name = "Authorization", required = false) String token,
    		@RequestBody List<DetalleDto> detalles) {

		if (token == null || !token.startsWith("Bearer ")) {
		    return new ResponseEntity<>("Token de autorización (Authorization token) requerido.", HttpStatus.UNAUTHORIZED);
		}
		if (detalles.isEmpty() || Objects.isNull(detalles.get(0).getIdUsuario())) {
            return new ResponseEntity<>("La lista de detalles o el ID de usuario está vacío (Details list or User ID is empty).", HttpStatus.BAD_REQUEST);
        }
		
		Usuario usuario=new Usuario();
		usuario.setIdUsuario(detalles.get(0).getIdUsuario());
		
		Orden orden = new Orden();
		orden.setFecha(new Date());
		orden.setUsuario(usuario);
		
		Orden ordenGuardada = oSer.guardar(orden);
		
		double total= 0.0;
		
		for (DetalleDto detalle : detalles) {
		    
			if (Objects.nonNull(detalle.getIdServicio())) {
			    
				if (Objects.isNull(detalle.getIdUsuario()) || Objects.isNull(detalle.getIdMascota()) || Objects.isNull(detalle.getFechaCita())) {
					return new ResponseEntity<>("Datos incompletos para la Cita (Incomplete data for the Appointment).", HttpStatus.BAD_REQUEST);
				}
				
				DetalleServicio ds = new DetalleServicio();
				
				Servicio servicio = new Servicio();
                servicio.setIdServicio(detalle.getIdServicio());
                
                Mascota mascota = new Mascota();
                mascota.setIdMascota(detalle.getIdMascota());
				
				ds.setOrden(ordenGuardada);
				ds.setNombre(detalle.getNombre());
				ds.setPrecio(detalle.getPrecio());
				ds.setTotal(detalle.getTotal());
				ds.setComision(detalle.getTotal() * 0.15);
				ds.setFechaCita(detalle.getFechaCita());
				ds.setServicio(servicio);
				ds.setMascota(mascota);
				
				DetalleOrden detalleGuardado=detOrdSer.guardar(ds);
				
				try {
					ResponseEntity<DetalleDto> citaResponse = servCitCli.nuevaCita(token, detalle.getIdUsuario(), 
						detalle.getIdServicio(), detalle.getIdMascota(), detalle.getFechaCita());
					
					if (!citaResponse.getStatusCode().is2xxSuccessful()) {
						detOrdSer.eliminar(detalleGuardado.getIdDetalle());
                        oSer.eliminar(ordenGuardada.getNumeroOrden());
					    return new ResponseEntity<>("Error al registrar la cita (Error registering the appointment): " + citaResponse.getStatusCode(), citaResponse.getStatusCode());
					}
					total += detalle.getTotal();
				
				} catch (Exception e) {
					detOrdSer.eliminar(detalleGuardado.getIdDetalle());
                    oSer.eliminar(ordenGuardada.getNumeroOrden());
					return new ResponseEntity<>("Fallo en la comunicación (Communication failed) con ServicioCita: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
				}
			
			} /*else if (Objects.nonNull(detalle.getIdProducto())) {
				
				// --- ES UN PRODUCTO ---
				// Consumir Stock usando el servicio local (Consume Stock using the local service)
				try {
					detalleOrdenService.consumirStockProductos(List.of(detalle)); // Llama al Feign Client ProductoClient internamente
				     // Aquí también iría el guardado del DetalleOrden
				} catch (Exception e) {
					return new ResponseEntity<>("Fallo al consumir stock (Failed to consume stock): " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}*/
		}
		
		ordenGuardada.setTotal(total);
        ordenGuardada.setComisionTotal(total * 0.15);
        oSer.guardar(ordenGuardada);
		return new ResponseEntity<>("Orden No. " + ordenGuardada.getNumeroOrden() + " creada y procesada exitosamente.", HttpStatus.CREATED);
	}
	
	
	
	private DetalleVentaDTO mapADto(DetalleOrden detOrd) {
	    DetalleVentaDTO dto = new DetalleVentaDTO();
	    dto.setDetalleId(detOrd.getIdDetalle());
	    dto.setOrdenId(detOrd.getOrden().getNumeroOrden());
	    dto.setNombreItem(detOrd.getNombre());
	    dto.setPrecio(detOrd.getPrecio());
	    dto.setTotal(detOrd.getTotal());
	    
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
	    }
	    return dto;
	}
}
