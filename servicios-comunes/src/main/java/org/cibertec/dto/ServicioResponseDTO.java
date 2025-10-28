package org.cibertec.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServicioResponseDTO {

    private Integer idServicio;
    private double precio;
    private String nombre, descripcion;
    // Usuario
    private Long idUsuario;
    private String nombreUsuario;
    private String apellido;
    private String telefono;

}
