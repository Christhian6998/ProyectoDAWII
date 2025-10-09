package org.cibertec.service;

import lombok.RequiredArgsConstructor;
import org.cibertec.dto.ServicioRequestDTO;
import org.cibertec.dto.ServicioResponseDTO;
import org.cibertec.entity.Mascota;
import org.cibertec.entity.Servicio;
import org.cibertec.mapper.ServicioMapper;
import org.cibertec.repository.IServicioRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicioService {
    private final IServicioRepository servicioRepository;
    private final ServicioMapper servicioMapper;


    public List<ServicioResponseDTO> listarServicio() {
        return servicioRepository.findAll().stream()
                .map(servicioMapper::toDto)
                .toList();
    }

        public ServicioResponseDTO buscarServicioPorId(Integer id) {
            return servicioRepository.findById(id)
                    .map(servicioMapper::toDto)
                    .orElseThrow(() -> new RuntimeException("No se encontro el servicio con id: " + id));
        }

// Modif repas
    public ServicioResponseDTO crearServicio(
            String nombre,
            String descripcion,
            Double precio,
           // Long usuarioId,
            MultipartFile img) throws IOException {

        ServicioRequestDTO request = ServicioRequestDTO.builder()
                .nombre(nombre)
                .descripcion(descripcion)
                .precio(precio)
                .build();

        byte[] imgBytes = img != null ? img.getBytes() : null;
        Servicio servicio = servicioMapper.toEntity(request, imgBytes);
        Servicio guardado = servicioRepository.save(servicio);
        return servicioMapper.toDto(guardado);
    }

    // Modif repas
    public ServicioResponseDTO actualizarServicio(
            Integer id,
            String nombre,
            String descripcion,
            Double precio,
           // Long usuarioId,
            MultipartFile img) throws IOException {

        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado"));

        // Actualizar solo los campos que no son null
        if (nombre != null) servicio.setNombre(nombre);
        if (descripcion != null) servicio.setDescripcion(descripcion);
        if (precio != null) servicio.setPrecio(precio);
        //if (usuarioId != null) servicio.setUsuarioId(usuarioId);
        if (img != null && !img.isEmpty()) {
            servicio.setImg(img.getBytes());
        }

        Servicio actualizado = servicioRepository.save(servicio);
        return servicioMapper.toDto(actualizado);
    }


        public void eliminarServicio(Integer id) {
            if(!servicioRepository.existsById(id)) {
                throw new RuntimeException("No se encontro el servicio con id: " + id);
            }
            servicioRepository.deleteById(id);
        }

    // Modif repas
        public byte[] obtenerImagen(Integer id) {
        return servicioRepository.findById(id)
                .map(Servicio::getImg)
                .orElse(null);
        }
    }

