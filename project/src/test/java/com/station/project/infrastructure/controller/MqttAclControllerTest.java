package com.station.project.infrastructure.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.station.project.application.services.TokensMQTTService;
import com.station.project.infrastructure.components.MqttBrokerProperties;

class MqttAclControllerTest {

    private TokensMQTTService tokensMQTTService;
    private MqttBrokerProperties mqttBrokerProperties;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        tokensMQTTService = mock(TokensMQTTService.class);
        mqttBrokerProperties = mock(MqttBrokerProperties.class);
        when(mqttBrokerProperties.getTopic()).thenReturn("test/topic");
        mockMvc = MockMvcBuilders.standaloneSetup(
                new MqttAclController(tokensMQTTService, mqttBrokerProperties)).build();
    }

    @Test
    void allowsAuthorizedTopicFromGoAuthJsonRequest() throws Exception {
        when(tokensMQTTService.isAuthorized("valid-token", "test/topic", 2, "test/topic"))
                .thenReturn(true);

        mockMvc.perform(post("/api/mqtt/acl")
                        .header("Authorization", "Bearer valid-token")
                        .contentType("application/json")
                        .content("{\"topic\":\"test/topic\",\"clientid\":\"mqtt-client\",\"acc\":2}"))
                .andExpect(status().isOk());
    }

    @Test
    void rejectsUnauthorizedTopicFromGoAuthJsonRequest() throws Exception {
        when(tokensMQTTService.isAuthorized("valid-token", "other/topic", 2, "test/topic"))
                .thenReturn(false);

        mockMvc.perform(post("/api/mqtt/acl")
                        .header("Authorization", "Bearer valid-token")
                        .contentType("application/json")
                        .content("{\"topic\":\"other/topic\",\"clientid\":\"mqtt-client\",\"acc\":2}"))
                .andExpect(status().isForbidden());
    }
}
