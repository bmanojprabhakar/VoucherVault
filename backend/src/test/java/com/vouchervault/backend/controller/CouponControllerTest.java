package com.vouchervault.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vouchervault.backend.dto.CouponRequest;
import com.vouchervault.backend.dto.CouponResponse;
import com.vouchervault.backend.repository.UserRepository;
import com.vouchervault.backend.security.JwtUtil;
import com.vouchervault.backend.service.CouponService;
import com.vouchervault.backend.utils.AppConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CouponController.class)
@AutoConfigureMockMvc(addFilters = false)
class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CouponService couponService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private CouponRequest couponRequest;
    private CouponResponse couponResponse;

    @BeforeEach
    void setUp() {
        couponRequest = new CouponRequest();
        couponRequest.setMerchant("Amazon");
        couponRequest.setCode("AMZ100");

        couponResponse = new CouponResponse();
        couponResponse.setId(UUID.randomUUID());
        couponResponse.setMerchant("Amazon");
        couponResponse.setCode("AMZ100");
    }

    @Test
    @WithMockUser(username = "3acd802a-c4e3-4a59-8ef4-c869f25c17ac")
    void createCoupon_Success() throws Exception {
        when(couponService.createCoupon(any(UUID.class), any(CouponRequest.class))).thenReturn(couponResponse);

        mockMvc.perform(post(AppConstants.API_COUPONS)
                .with(request -> {
                    SecurityContextHolder.getContext().setAuthentication(
                            new UsernamePasswordAuthenticationToken("3acd802a-c4e3-4a59-8ef4-c869f25c17ac", null,
                                    Collections.emptyList()));
                    return request;
                })
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(couponRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value(AppConstants.COUPON_CREATED_SUCCESS))
                .andExpect(jsonPath("$.data.merchant").value("Amazon"));
    }

    @Test
    @WithMockUser(username = "3acd802a-c4e3-4a59-8ef4-c869f25c17ac")
    void getCoupons_Success() throws Exception {
        when(couponService.getUserCoupons(any(UUID.class), any()))
                .thenReturn(new PageImpl<>(Collections.singletonList(couponResponse)));

        mockMvc.perform(get(AppConstants.API_COUPONS)
                .with(request -> {
                    SecurityContextHolder.getContext().setAuthentication(
                            new UsernamePasswordAuthenticationToken("3acd802a-c4e3-4a59-8ef4-c869f25c17ac", null,
                                    Collections.emptyList()));
                    return request;
                }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.content[0].merchant").value("Amazon"));
    }

    @Test
    @WithMockUser(username = "3acd802a-c4e3-4a59-8ef4-c869f25c17ac")
    void getCoupon_Success() throws Exception {
        UUID couponId = UUID.randomUUID();
        when(couponService.getCoupon(any(UUID.class), eq(couponId))).thenReturn(couponResponse);

        mockMvc.perform(get(AppConstants.API_COUPONS + "/" + couponId)
                .with(request -> {
                    SecurityContextHolder.getContext().setAuthentication(
                            new UsernamePasswordAuthenticationToken("3acd802a-c4e3-4a59-8ef4-c869f25c17ac", null,
                                    Collections.emptyList()));
                    return request;
                }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.data.merchant").value("Amazon"));
    }

    @Test
    @WithMockUser(username = "3acd802a-c4e3-4a59-8ef4-c869f25c17ac")
    void updateCoupon_Success() throws Exception {
        UUID couponId = UUID.randomUUID();
        when(couponService.updateCoupon(any(UUID.class), eq(couponId), any(CouponRequest.class)))
                .thenReturn(couponResponse);

        mockMvc.perform(put(AppConstants.API_COUPONS + "/" + couponId)
                .with(request -> {
                    SecurityContextHolder.getContext().setAuthentication(
                            new UsernamePasswordAuthenticationToken("3acd802a-c4e3-4a59-8ef4-c869f25c17ac", null,
                                    Collections.emptyList()));
                    return request;
                })
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(couponRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    @WithMockUser(username = "3acd802a-c4e3-4a59-8ef4-c869f25c17ac")
    void deleteCoupon_Success() throws Exception {
        UUID couponId = UUID.randomUUID();

        mockMvc.perform(delete(AppConstants.API_COUPONS + "/" + couponId)
                .with(request -> {
                    SecurityContextHolder.getContext().setAuthentication(
                            new UsernamePasswordAuthenticationToken("3acd802a-c4e3-4a59-8ef4-c869f25c17ac", null,
                                    Collections.emptyList()));
                    return request;
                })
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }
}
