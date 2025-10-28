package org.cibertec.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoResponseDTO {
    private Integer IdProducto;
    private double precio;
    private int stock;
    private String nombre;
    private String descripcion;
    //Usuario
    private Long idUsuario;
    private String ruc;
    private String username;
    private String correo;
    private String telefono;
    private String direccion;
}
