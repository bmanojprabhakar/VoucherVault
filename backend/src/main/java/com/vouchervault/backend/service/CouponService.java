package com.vouchervault.backend.service;

import com.vouchervault.backend.dto.CouponRequest;
import com.vouchervault.backend.dto.CouponResponse;
import com.vouchervault.backend.model.Coupon;
import com.vouchervault.backend.model.User;
import com.vouchervault.backend.repository.CouponRepository;
import com.vouchervault.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final UserRepository userRepository;
    private final EncryptionService encryptionService;

    @Transactional
    public CouponResponse createCoupon(UUID userId, CouponRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String encryptedCode = encryptionService.encrypt(request.getCode(), user.getSocialId());

        Coupon coupon = new Coupon();
        coupon.setUser(user);
        coupon.setMerchant(request.getMerchant());
        coupon.setCodeEncrypted(encryptedCode);
        coupon.setDiscountDetails(request.getDiscountDetails());
        coupon.setDescription(request.getDescription());
        coupon.setExpiryDate(request.getExpiryDate());
        coupon.setMinAmount(request.getMinAmount());

        return mapToResponse(couponRepository.save(coupon), user.getSocialId());
    }

    @Transactional(readOnly = true)
    public Page<CouponResponse> getUserCoupons(UUID userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return couponRepository.findByUserIdAndDeletedAtIsNull(userId, pageable)
                .map(coupon -> mapToResponse(coupon, user.getSocialId()));
    }

    @Transactional(readOnly = true)
    public CouponResponse getCoupon(UUID userId, UUID couponId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Coupon coupon = couponRepository.findByIdAndUserIdAndDeletedAtIsNull(couponId, userId)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));

        return mapToResponse(coupon, user.getSocialId());
    }

    @Transactional
    public CouponResponse updateCoupon(UUID userId, UUID couponId, CouponRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Coupon coupon = couponRepository.findByIdAndUserIdAndDeletedAtIsNull(couponId, userId)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));

        coupon.setMerchant(request.getMerchant());
        coupon.setDiscountDetails(request.getDiscountDetails());
        coupon.setDescription(request.getDescription());
        coupon.setExpiryDate(request.getExpiryDate());
        coupon.setMinAmount(request.getMinAmount());

        if (request.getCode() != null && !request.getCode().isEmpty()) {
            coupon.setCodeEncrypted(encryptionService.encrypt(request.getCode(), user.getSocialId()));
        }

        return mapToResponse(couponRepository.save(coupon), user.getSocialId());
    }

    @Transactional
    public void deleteCoupon(UUID userId, UUID couponId) {
        Coupon coupon = couponRepository.findByIdAndUserIdAndDeletedAtIsNull(couponId, userId)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));

        coupon.setDeletedAt(LocalDateTime.now());
        couponRepository.save(coupon);
    }

    private CouponResponse mapToResponse(Coupon coupon, String socialId) {
        CouponResponse response = new CouponResponse();
        response.setId(coupon.getId());
        response.setMerchant(coupon.getMerchant());
        response.setDiscountDetails(coupon.getDiscountDetails());
        response.setDescription(coupon.getDescription());
        response.setExpiryDate(coupon.getExpiryDate());
        response.setMinAmount(coupon.getMinAmount());
        response.setCreatedAt(coupon.getCreatedAt());

        try {
            response.setCode(encryptionService.decrypt(coupon.getCodeEncrypted(), socialId));
        } catch (Exception e) {
            response.setCode("Error Decrypting");
        }

        return response;
    }
}
