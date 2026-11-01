package com.station.project.infrastructure.consumer;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class MqttStartupListener {

    private final MqttPahoMessageDrivenChannelAdapter mqttInbound;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("Tomcat listo. Iniciando consumidor MQTT inbound...");
        if (!mqttInbound.isRunning()) {
            mqttInbound.start();
            log.info("Consumidor MQTT inbound iniciado exitosamente.");
        }
    }
}