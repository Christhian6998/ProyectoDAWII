package org.cibertec.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Servicio {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idServicio;
	private double precio;
	private String nombre;
    private String descripcion;
	
	@Lob
	@Column(name = "img", columnDefinition = "LONGBLOB")
	private byte[] img;
	
	@ManyToOne
	@JsonIgnore
	private Usuario usuario;
	
	@OneToMany(mappedBy = "servicio")
	@JsonIgnore
	private List<DetalleServicio> detServicio;
}
