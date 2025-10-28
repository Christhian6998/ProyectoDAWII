package org.cibertec.controller;

import org.cibertec.dto.ProductoRequestDTO;
import org.cibertec.dto.ProductoResponseDTO;
import org.cibertec.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/producto")
public class ProductoController {
    @Autowired
    private ProductoService productoService;

    @PostMapping(value = "/guardar", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    private ResponseEntity<ProductoResponseDTO> guardarProducto(@RequestPart ProductoRequestDTO productoRequestDTO,
                                                                @RequestPart(required = false) MultipartFile imagen) {
        return productoService.guardar(productoRequestDTO, imagen);
    }

    @GetMapping("/listar")
    private ResponseEntity<List<ProductoResponseDTO>> obtenerProductos() {
        return productoService.obtenerProductos();
    }

    @GetMapping(value = "/buscar", params = "id")
    private ResponseEntity<ProductoResponseDTO> obtenerProductoPorID(@RequestParam int id) {
        return productoService.buscarProductoPorID(id);
    }

    @GetMapping(value = "/buscar", params = "nombre")
    private ResponseEntity<List<ProductoResponseDTO>> obtenerProductoPorNombre(@RequestParam String nombre) {
        return productoService.buscarProductoPorNombre(nombre);
    }

    @GetMapping(value = "/buscar", params = "veterinario")
    private ResponseEntity<List<ProductoResponseDTO>> obtenerProductosPorVeterinario(@RequestParam Long veterinario) {
        return productoService.obtenerProductosPorVeterinario(veterinario);
    }

    @PutMapping(value = "/actualizar", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    private ResponseEntity<String> actualizarProductoPorId(@RequestParam int id,
                                                           @RequestPart ProductoRequestDTO producto,
                                                           @RequestPart(required = false) MultipartFile imagen) {
        return productoService.actualizarProductoPorId(id, producto, imagen);
    }

    @PutMapping(value = "/actualizar/restarStock")
    private ResponseEntity<String> restarStockProductoPorID(@RequestParam int id,
                                                            @RequestParam int stock) {
        return productoService.restarStockProducto(id, stock);
    }

    @DeleteMapping("/eliminar")
    private ResponseEntity<String> eliminarProductoPorId(@RequestParam int id) {
        return productoService.eliminarProductoPorId(id);
    }

    @GetMapping("/obtenerImagen/{id}")
    public ResponseEntity<byte[]> obtenerImagen(@PathVariable int id) {
        return productoService.obtenerImagen(id);
    }
}
