package com.vouchervault.backend.controller;

import com.vouchervault.backend.model.User;
import com.vouchervault.backend.repository.UserRepository;
import com.vouchervault.backend.security.JwtUtil;
import com.vouchervault.backend.utils.AppConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private JwtUtil jwtUtil;

    private User user;
    private String userIdStr;

    @BeforeEach
    void setUp() {
        userIdStr = "3acd802a-c4e3-4a59-8ef4-c869f25c17ac";
        user = new User();
        user.setId(UUID.fromString(userIdStr));
        user.setEmail("test@vouchervault.com");
        user.setName("Test User");
    }

    @Test
    @WithMockUser(username = "3acd802a-c4e3-4a59-8ef4-c869f25c17ac")
    void getCurrentUser_Success() throws Exception {
        when(userRepository.findById(UUID.fromString(userIdStr))).thenReturn(Optional.of(user));

        mockMvc.perform(get(AppConstants.API_USER + "/me")
                .with(request -> {
                    SecurityContextHolder.getContext().setAuthentication(
                            new UsernamePasswordAuthenticationToken("3acd802a-c4e3-4a59-8ef4-c869f25c17ac", null,
                                    Collections.emptyList()));
                    return request;
                }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.email").value("test@vouchervault.com"));
    }
}
