package org.cibertec.client;

import org.cibertec.dto.ServicioResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "servicio-client", url = "http://localhost:8888/servicios")
public interface ServicioClient {

    @GetMapping("/{id}")
    ServicioResponseDTO obtenerServicioPorId(@PathVariable("id") Integer id);

}
