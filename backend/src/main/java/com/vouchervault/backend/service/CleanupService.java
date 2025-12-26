package com.vouchervault.backend.service;

import com.vouchervault.backend.model.Coupon;
import com.vouchervault.backend.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CleanupService {

    private final CouponRepository couponRepository;

    // Run every day at 3 AM
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void purgeOldDeletedCoupons() {
        log.info("Starting cleanup of old deleted coupons...");

        LocalDateTime threshold = LocalDateTime.now().minusDays(30);
        List<Coupon> softDeletedCoupons = couponRepository.findByDeletedAtIsNotNull();

        int count = 0;
        for (Coupon coupon : softDeletedCoupons) {
            if (coupon.getDeletedAt().isBefore(threshold)) {
                couponRepository.delete(coupon); // Hard delete
                count++;
            }
        }

        log.info("Cleanup finished. Purged {} coupons older than 30 days.", count);
    }
}
