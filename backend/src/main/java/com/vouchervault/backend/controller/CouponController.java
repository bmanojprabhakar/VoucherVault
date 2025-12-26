package com.vouchervault.backend.controller;

import com.vouchervault.backend.dto.CouponRequest;
import com.vouchervault.backend.dto.CouponResponse;
import com.vouchervault.backend.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @PostMapping
    public ResponseEntity<CouponResponse> createCoupon(
            @AuthenticationPrincipal String userId,
            @RequestBody CouponRequest request) {
        return new ResponseEntity<>(
                couponService.createCoupon(UUID.fromString(userId), request),
                HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<CouponResponse>> getCoupons(
            @AuthenticationPrincipal String userId,
            @org.springframework.data.web.SortDefault(sort = "expiryDate", direction = org.springframework.data.domain.Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(couponService.getUserCoupons(UUID.fromString(userId), pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CouponResponse> getCoupon(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID id) {
        return ResponseEntity.ok(couponService.getCoupon(UUID.fromString(userId), id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CouponResponse> updateCoupon(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID id,
            @RequestBody CouponRequest request) {
        return ResponseEntity.ok(couponService.updateCoupon(UUID.fromString(userId), id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoupon(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID id) {
        couponService.deleteCoupon(UUID.fromString(userId), id);
        return ResponseEntity.noContent().build();
    }
}
