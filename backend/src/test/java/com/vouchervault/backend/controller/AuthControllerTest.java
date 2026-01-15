package com.vouchervault.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vouchervault.backend.security.JwtUtil;
import com.vouchervault.backend.security.auth.AuthStrategy;
import com.vouchervault.backend.security.auth.AuthStrategyFactory;
import com.vouchervault.backend.utils.AppConstants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security filters for this test
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthStrategyFactory authStrategyFactory;

    @MockitoBean
    private AuthStrategy authStrategy;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void authenticate_Success() throws Exception {
        when(authStrategyFactory.getStrategy("google")).thenReturn(authStrategy);
        when(authStrategy.authenticate(anyString())).thenReturn("mock-jwt-token");

        mockMvc.perform(post(AppConstants.API_V1_AUTH + "/google")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("code", "google-code"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.token").value("mock-jwt-token"));
    }
}
