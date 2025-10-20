package org.cibertec.client;


import org.cibertec.dto.ServicioResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "Servicio", url="http://localhost:8080/Servicio")
public interface ServicioClient {
    @GetMapping("/servicios/{id}")
    public ResponseEntity<ServicioResponseDTO> buscarServicioPorId(@PathVariable Integer id);
}
