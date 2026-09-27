package com.station.project.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import com.station.project.infrastructure.components.MqttBrokerProperties;

import java.util.UUID;


@EnableIntegration 
@RequiredArgsConstructor 
@Configuration 
public class MqttBrokerConfig {
    private  final MqttBrokerProperties mqttBrokerProperties;
    @Bean 
    public MqttPahoClientFactory mqttClientFactory(){
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{mqttBrokerProperties.getUrl()});
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);
        options.setMaxInflight(100);
        options.setKeepAliveInterval(20);
        factory.setConnectionOptions(options);
        return factory;
    }
    @Bean 
    public MessageChannel mqttInputChannel(){return new DirectChannel();}
    @Bean 
    public MessageProducer mqttInbound(){
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(mqttBrokerProperties.getId(), mqttClientFactory(),mqttBrokerProperties.getTopic());
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(0);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }
    @Bean 
    public MessageChannel mqttOutboundChannel(){return  new DirectChannel();}
   
    @Bean 
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutbound(){
        MqttPahoMessageHandler handler = new MqttPahoMessageHandler(mqttBrokerProperties.getId() + UUID.randomUUID(),mqttClientFactory());
        handler.setAsync(true);
        handler.setDefaultQos(0);
        handler.setDefaultTopic(mqttBrokerProperties.getTopic());
        return handler;
    }
}
