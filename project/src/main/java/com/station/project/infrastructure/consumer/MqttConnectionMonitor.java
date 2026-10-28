package com.station.project.infrastructure.consumer;

import org.springframework.context.event.EventListener;
import org.springframework.integration.mqtt.event.MqttConnectionFailedEvent;
import org.springframework.integration.mqtt.event.MqttSubscribedEvent;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MqttConnectionMonitor {

    @EventListener
    public void onSubscribed(MqttSubscribedEvent event) {
        log.info("MQTT conectado y suscrito correctamente: {}",
                event.getSource());
    }

    @EventListener
    public void onConnectionFailed(MqttConnectionFailedEvent event) {
        log.error("Error de conexión MQTT: {}", event);
    }
}