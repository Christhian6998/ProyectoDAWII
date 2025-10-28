package org.cibertec.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cibertec.dto.ServicioRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
//agreg service por autowiride.
@Service
public class KafkaPublisherService {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Value("${app.topic.name}")
    private String servitopicName;


    public void enviarMensageKafka(ServicioRequestDTO servicio) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String mesage = mapper.writeValueAsString(servicio);

            // KafkaTemplate.send(topic,message);
            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(servitopicName, mesage);
            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    System.out.println("Error al enviar el mensaje: " + ex.getMessage());
                } else {
                    System.out.println("Mensaje enviado con exito: " + mesage +
                            " en el tema: " + servitopicName + " Offset: " +
                            result.getRecordMetadata().offset());
                }
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
    //kafkaTemplate.send(topicName, message);"profe"
    //CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topicName, message);
//future.whenComplete((result, ex) -> {
      //  if (ex == null) {
      //      System.out.println("Sent message=[" + message + "] with offset=[" + result.getRecordMetadata().offset() + "]");
     //   } else {
      //      System.out.println("Unable to send message =[" + message + "] due to: " + ex.getMessage());
     //   }
   // });
//} catch (Exception e){
    //    e.printStackTrace();
   // }
}
