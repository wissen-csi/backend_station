package com.station.project.infrastructure.consumer;

import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.station.project.application.services.CacheDataServices;
import com.station.project.domain.model.Data;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class BoyaConsumerMqtt {

    private final CacheDataServices cacheDataServices;
    private final ObjectMapper objectMapper; // Inyección del ObjectMapper estándar de Spring Boot

    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void consumeMqttMessage(Message<String> message) {
        log.info("--> EVENTO MQTT CAPTURADO EN CANAL INBOUND <--");
        
        try {
            String payload = message.getPayload();
            String topic = message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC, String.class);
            
            log.info("Mensaje MQTT recibido en tópico [{}]: {}", topic, payload);

            Data data = objectMapper.readValue(payload, Data.class);
            log.info("Objeto Data deserializado correctamente: {}", data);

            cacheDataServices.save(data);
            log.info("Objeto Data guardado exitosamente en cache/BD.");
            
        } catch (Exception e) {
            log.error("Error al deserializar o guardar el mensaje MQTT de la boya: {}", e.getMessage(), e);
        }
    }
}