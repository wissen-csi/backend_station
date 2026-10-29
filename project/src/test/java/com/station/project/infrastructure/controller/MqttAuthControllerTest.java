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

class MqttAuthControllerTest {

    private TokensMQTTService tokensMQTTService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        tokensMQTTService = mock(TokensMQTTService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new MqttAuthController(tokensMQTTService)).build();
    }

    @Test
    void acceptsValidTokenFromGoAuthJsonRequest() throws Exception {
        when(tokensMQTTService.isTokenActive("valid-token")).thenReturn(true);

        mockMvc.perform(post("/api/mqtt/auth")
                        .header("Authorization", "Bearer valid-token")
                        .contentType("application/json")
                        .content("{\"username\":\"\",\"password\":\"\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void rejectsInvalidTokenFromGoAuthJsonRequest() throws Exception {
        when(tokensMQTTService.isTokenActive("invalid-token")).thenReturn(false);

        mockMvc.perform(post("/api/mqtt/auth")
                        .header("Authorization", "Bearer invalid-token")
                        .contentType("application/json")
                        .content("{\"username\":\"\",\"password\":\"\"}"))
                .andExpect(status().isUnauthorized());
    }
}
