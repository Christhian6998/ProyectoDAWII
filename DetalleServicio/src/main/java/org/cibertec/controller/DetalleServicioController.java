package org.cibertec.controller;

import lombok.RequiredArgsConstructor;
import org.cibertec.dto.DetalleDto;
import org.cibertec.service.DetalleServicioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/detalle-servicio")
@RequiredArgsConstructor
public class DetalleServicioController {

    private final DetalleServicioService detalleServicioService;

    // 🔹 Listar todos los detalles
    @GetMapping
    public ResponseEntity<List<DetalleDto>> listarDetalles() {
        List<DetalleDto> lista = detalleServicioService.listar();
        return ResponseEntity.ok(lista);
    }

    // 🔹 Crear nuevo detalle de servicio
    @PostMapping
    public ResponseEntity<DetalleDto> crearDetalle(@RequestBody DetalleDto dto) {
        DetalleDto creado = detalleServicioService.crear(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }
}
