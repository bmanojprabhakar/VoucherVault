package com.vouchervault.backend.service;

import com.vouchervault.backend.dto.CouponRequest;
import com.vouchervault.backend.dto.CouponResponse;
import com.vouchervault.backend.exception.ResourceNotFoundException;
import com.vouchervault.backend.mapper.CouponMapper;
import com.vouchervault.backend.model.Coupon;
import com.vouchervault.backend.model.User;
import com.vouchervault.backend.repository.CouponRepository;
import com.vouchervault.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private EncryptionService encryptionService;
    @Mock
    private CouponMapper couponMapper;

    @InjectMocks
    private CouponService couponService;

    private User user;
    private UUID userId;
    private Coupon coupon;
    private UUID couponId;
    private CouponRequest couponRequest;
    private CouponResponse couponResponse;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        user = new User();
        user.setId(userId);
        user.setSocialId("google-123");
        user.setEmail("test@example.com");

        couponId = UUID.randomUUID();
        coupon = new Coupon();
        coupon.setId(couponId);
        coupon.setUser(user);
        coupon.setMerchant("Amazon");
        coupon.setCodeEncrypted("encrypted-code");

        couponRequest = new CouponRequest();
        couponRequest.setMerchant("Amazon");
        couponRequest.setCode("AMZ123");

        couponResponse = new CouponResponse();
        couponResponse.setId(couponId);
        couponResponse.setMerchant("Amazon");
    }

    @Test
    void createCoupon_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(encryptionService.encrypt(any(), any())).thenReturn("encrypted-code");
        when(couponMapper.toEntity(any())).thenReturn(coupon);
        when(couponRepository.save(any())).thenReturn(coupon);
        when(couponMapper.toResponse(any())).thenReturn(couponResponse);

        CouponResponse result = couponService.createCoupon(userId, couponRequest);

        assertNotNull(result);
        assertEquals("Amazon", result.getMerchant());
        assertEquals("AMZ123", result.getCode());
        verify(couponRepository).save(any());
    }

    @Test
    void createCoupon_UserNotFound_ThrowsException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> couponService.createCoupon(userId, couponRequest));
    }

    @Test
    void getUserCoupons_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Coupon> couponPage = new PageImpl<>(List.of(coupon));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(couponRepository.findByUserIdAndDeletedAtIsNull(userId, pageable)).thenReturn(couponPage);
        when(couponMapper.toResponse(any())).thenReturn(couponResponse);

        Page<CouponResponse> result = couponService.getUserCoupons(userId, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(couponRepository).findByUserIdAndDeletedAtIsNull(userId, pageable);
    }

    @Test
    void getCoupon_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(couponRepository.findByIdAndUserIdAndDeletedAtIsNull(couponId, userId)).thenReturn(Optional.of(coupon));
        when(couponMapper.toResponse(any())).thenReturn(couponResponse);

        CouponResponse result = couponService.getCoupon(userId, couponId);

        assertNotNull(result);
        assertEquals(couponId, result.getId());
    }

    @Test
    void deleteCoupon_Success() {
        when(couponRepository.findByIdAndUserIdAndDeletedAtIsNull(couponId, userId)).thenReturn(Optional.of(coupon));

        couponService.deleteCoupon(userId, couponId);

        assertNotNull(coupon.getDeletedAt());
        verify(couponRepository).save(coupon);
    }
}
