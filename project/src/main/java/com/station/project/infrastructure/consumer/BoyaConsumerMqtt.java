package com.station.project.infrastructure.consumer;

import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

import com.station.project.application.services.CacheDataServices;
import com.station.project.domain.model.Data;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.json.JsonMapper;
@AllArgsConstructor 
@Component 
@Slf4j 
public class BoyaConsumerMqtt implements MessageHandler{
    private CacheDataServices cacheDataServices;
    private  JsonMapper jsonMapper;
    @ServiceActivator (inputChannel = "mqttInputChannel")
    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        String payLoad = message.getPayload().toString();
        log.info("message: {}",message);
        String topic = message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC,String.class);
        Data cache =jsonMapper.readValue(payLoad, Data.class);
        cacheDataServices.save(cache);
        
    }

}
