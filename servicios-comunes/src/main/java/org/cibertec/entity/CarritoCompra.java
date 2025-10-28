package org.cibertec.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "carrito")
public class CarritoCompra {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCarrito;

    private Long idUsuario;
    private Integer idProducto;
    private String nombreProducto;
    private String descripcion;
    private Double precio;
    private Integer cantidad;
    private Double total;
}
