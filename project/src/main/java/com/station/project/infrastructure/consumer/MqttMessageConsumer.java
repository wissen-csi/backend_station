
package com.station.project.infrastructure.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class MqttMessageConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(MqttMessageConsumer.class);

    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void receive(Message<?> message) {

        log.info("Mensaje recibido desde EMQX:");
        log.info("Topic: {}", message.getHeaders().get("mqtt_receivedTopic"));
        log.info("Payload: {}", message.getPayload());
    }
}
