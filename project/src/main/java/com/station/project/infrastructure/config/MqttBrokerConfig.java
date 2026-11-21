
package com.station.project.infrastructure.config;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.messaging.MessageChannel;

@Configuration
@EnableIntegration
public class MqttBrokerConfig {

    @Value("${mqtt.url}")
    private String brokerUrl;

    @Value("${mqtt.username}")
    private String username;

    @Value("${mqtt.password}")
    private String password;

    @Value("${mqtt.id}")
    private String clientId;

    @Value("${mqtt.topic}")
    private String topic;

    @Value("${mqtt.certificate}")
    private Resource caCertificate;

    private final MqttSSLConfig mqttSSLConfig;

    public MqttBrokerConfig(MqttSSLConfig mqttSSLConfig) {
        this.mqttSSLConfig = mqttSSLConfig;
    }

    @Bean
    public MqttPahoClientFactory mqttClientFactory()
            throws Exception {

        MqttConnectOptions options = new MqttConnectOptions();

        options.setServerURIs(new String[]{brokerUrl});

        options.setUserName(username);
        options.setPassword(password.toCharArray());

        options.setSocketFactory(
                mqttSSLConfig.createSocketFactory(caCertificate)
        );

        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(30);
        options.setKeepAliveInterval(60);

        DefaultMqttPahoClientFactory factory =
                new DefaultMqttPahoClientFactory();

        factory.setConnectionOptions(options);

        return factory;
    }

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MqttPahoMessageDrivenChannelAdapter mqttInbound(
            MqttPahoClientFactory mqttClientFactory) {

        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(
                        clientId,
                        mqttClientFactory,
                        topic
                );

        adapter.setCompletionTimeout(5000);
        adapter.setQos(1);
        adapter.setOutputChannel(mqttInputChannel());

        return adapter;
    }
}
