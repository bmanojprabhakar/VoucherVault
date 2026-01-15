package com.vouchervault.backend.service;

import com.vouchervault.backend.model.Coupon;
import com.vouchervault.backend.repository.CouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CleanupServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CleanupService cleanupService;

    private Coupon oldCoupon;
    private Coupon newCoupon;

    @BeforeEach
    void setUp() {
        oldCoupon = new Coupon();
        oldCoupon.setDeletedAt(LocalDateTime.now().minusDays(31));

        newCoupon = new Coupon();
        newCoupon.setDeletedAt(LocalDateTime.now().minusDays(29));
    }

    @Test
    void purgeOldDeletedCoupons_Success() {
        when(couponRepository.findByDeletedAtIsNotNull()).thenReturn(Arrays.asList(oldCoupon, newCoupon));

        cleanupService.purgeOldDeletedCoupons();

        verify(couponRepository, times(1)).delete(oldCoupon);
        verify(couponRepository, never()).delete(newCoupon);
    }

    @Test
    void purgeOldDeletedCoupons_NoDeletedCoupons() {
        when(couponRepository.findByDeletedAtIsNotNull()).thenReturn(Collections.emptyList());

        cleanupService.purgeOldDeletedCoupons();

        verify(couponRepository, never()).delete(any());
    }
}
