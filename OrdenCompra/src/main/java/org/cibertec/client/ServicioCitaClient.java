package org.cibertec.client;

import java.util.Date;

import org.cibertec.dto.DetalleDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "ServicioCita", url="http://localhost:8080/ServicioCita")
public interface ServicioCitaClient {
	
	@GetMapping("/cita/nuevo")
	public ResponseEntity<DetalleDto> nuevaCita(@RequestHeader(name = "Authorization", required = false) String token,
			@RequestParam Long idUsuario, @RequestParam Integer idServicio,@RequestParam Long idMascota,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaCita);
}
