package org.cibertec.service;

import lombok.RequiredArgsConstructor;
import org.cibertec.dto.DetalleDto;
import org.cibertec.dto.ServicioResponseDTO;
import org.cibertec.entity.DetalleServicio;
import org.cibertec.client.ServicioClient;
import org.cibertec.repository.DetalleServicioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DetalleServicioService {

    private final DetalleServicioRepository detalleServicioRepository;
    private final ServicioClient servicioClient;

    public List<DetalleDto> listar() {
        return detalleServicioRepository.findAll()
                .stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    public DetalleDto crear(DetalleDto dto) {

        if (dto.getIdServicio() == null) {
            throw new IllegalArgumentException("El idServicio no puede ser nulo para registrar un detalle de servicio.");
        }

        ServicioResponseDTO servicio = servicioClient.obtenerServicioPorId(dto.getIdServicio());

        DetalleServicio detalle = new DetalleServicio();
        detalle.setFechaCita(dto.getFechaCita());
        detalle.setPrecio(servicio.getPrecio());
        detalle.setTotal(servicio.getPrecio() * (dto.getCantidad() == null ? 1 : dto.getCantidad()));
        detalle.setNombre(servicio.getNombre());
        detalle.setComision(0.0);

        detalleServicioRepository.save(detalle);

        return convertirADto(detalle);
    }

    private DetalleDto convertirADto(DetalleServicio detalle) {
        DetalleDto dto = new DetalleDto();
        dto.setFechaCita(detalle.getFechaCita());
        dto.setPrecio(detalle.getPrecio());
        dto.setTotal(detalle.getTotal());
        dto.setNombre(detalle.getNombre());
        dto.setIdServicio(
                detalle.getServicio() != null ? detalle.getServicio().getIdServicio() : null
        );
        return dto;
    }
}
