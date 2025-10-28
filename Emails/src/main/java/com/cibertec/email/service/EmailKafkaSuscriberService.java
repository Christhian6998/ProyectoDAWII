package com.cibertec.email.service;

import com.cibertec.email.client.IUsuarioClient;
import com.cibertec.email.dto.ProductoRequestDTO;
import com.cibertec.email.dto.ServicioRequestDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cibertec.entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class EmailKafkaSuscriberService {
    @Autowired
    IUsuarioClient usuarioClient;

    @KafkaListener(topics = "${app.topic.name}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(String msg) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            ProductoRequestDTO producto = mapper.readValue(msg, ProductoRequestDTO.class);
            Usuario usuario = usuarioClient.getById(producto.getIdUsuario());

            System.out.println(">>>>>>>>>>> Nuevo Mensaje >>>>>>>>>>");
            System.out.println("Asunto: Nuevo producto de " + usuario.getUsername());
            System.out.println("Destinatario: " + usuario.getCorreo());
            System.out.println("Cuerpo:");
            System.out.println(">> Hola, " + usuario.getUsername() + " acaba de agregar un nuevo producto a su tienda virtual!");
            System.out.println(">> Producto: " + producto.getNombre());
            System.out.println(">> Descripcion: " + producto.getDescripcion());
            System.out.println(">> Precio: " + producto.getPrecio());
            System.out.println(">> Stock: " + producto.getStock());
            System.out.println("\nEnviando correo Electronico...\n");
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    // Servicio

    @KafkaListener(topics = "servicio-notification", groupId = "${spring.kafka.consumer.group-id}")
    public void Escuchar(String mesage) {
        ObjectMapper mapper = new ObjectMapper();
        try {

            ServicioRequestDTO servicio = mapper.readValue(mesage, ServicioRequestDTO.class);
            // Obtener usuario por idUsuario
            Usuario usuario = usuarioClient.getById(servicio.getIdUsuario());

            System.out.println(">>>>>>>>>>> Nuevo Mensaje >>>>>>>>>>");
            System.out.println("Asunto: Nuevo servicio de " + usuario.getUsername());
            System.out.println("Destinatario: " + usuario.getCorreo());
            System.out.println("Cuerpo:");
            System.out.println(">> Hola, " + usuario.getUsername() + " se agrego nuevo servicio a su catálogo!");
            System.out.println(">> Servicio: " + servicio.getNombre());
            System.out.println(">> Descripcion: " + servicio.getDescripcion());
            System.out.println(">> Precio: " + servicio.getPrecio());
            System.out.println("\nEnviando correo Electronico...\n");

        } catch (Exception e) { // Mejor que JsonProcessingException solo
            System.err.println("Error procesando mensaje de Kafka: " + mesage);
            e.printStackTrace(); // O loguea adecuadamente
        }
    }
}
