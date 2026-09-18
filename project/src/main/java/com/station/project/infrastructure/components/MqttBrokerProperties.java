package com.station.project.infrastructure.components;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@ConfigurationProperties(prefix = "mqtt")
@Data 
@Component 
public class MqttBrokerProperties {
    private String url;
    private  String id;
    private  String topic;
}
