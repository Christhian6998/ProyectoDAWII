package org.cibertec.controller;

import lombok.RequiredArgsConstructor;
import org.cibertec.dto.ServicioRequestDTO;
import org.cibertec.dto.ServicioResponseDTO;
import org.cibertec.entity.Producto;
import org.cibertec.service.ServicioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/servicios")
@RequiredArgsConstructor
public class ServicioController {
    private final ServicioService servicioService;

    @GetMapping
    public ResponseEntity<List<ServicioResponseDTO>> listarServicios() {
        List<ServicioResponseDTO> servicios = servicioService.listarServicio();
        return ResponseEntity.ok(servicios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServicioResponseDTO> buscarServicioPorId(@PathVariable Integer id){
        ServicioResponseDTO servicio = servicioService.buscarServicioPorId(id);
        return ResponseEntity.ok(servicio);
    }

    // Modif repas
    @PostMapping
    public ResponseEntity<ServicioResponseDTO> crearServicio(
            @RequestParam String nombre,
            @RequestParam String descripcion,
            @RequestParam Double precio,
            @RequestParam Long usuarioId,
            @RequestParam(required = false) MultipartFile img) throws IOException {

        ServicioResponseDTO servicio = servicioService.crearServicio(nombre, descripcion, precio,usuarioId,img);
        return ResponseEntity.status(HttpStatus.CREATED).body(servicio);
    }
    // Modif repas
    @PutMapping("/{id}")
    public ResponseEntity<ServicioResponseDTO> actualizarServicio(
            @PathVariable Integer id,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) Double precio,
            @RequestParam(required = false) MultipartFile img) throws IOException {

        ServicioResponseDTO servicio = servicioService.actualizarServicio(id, nombre, descripcion, precio, img);
        return ResponseEntity.ok(servicio);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarServicio(@PathVariable Integer id){
        servicioService.eliminarServicio(id);
        return ResponseEntity.noContent().build();
    }
    // Modif repas
    @GetMapping("/imagen/{id}")
    public ResponseEntity<byte[]> obtenerImagen(@PathVariable Integer id) {
        byte[] img = servicioService.obtenerImagen(id);
        if (img == null || img.length == 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG) // Ajusta si usas PNG, etc.
                .body(img);
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<ServicioResponseDTO>> listarServiciosPorVeterinario(@PathVariable Long idUsuario) {
        List<ServicioResponseDTO> servicios = servicioService.listarServiciosPorVeterinario(idUsuario);
        return ResponseEntity.ok(servicios);}
}
