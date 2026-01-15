package com.vouchervault.backend.service;

import com.vouchervault.backend.dto.CouponRequest;
import com.vouchervault.backend.dto.CouponResponse;
import com.vouchervault.backend.exception.ResourceNotFoundException;
import com.vouchervault.backend.mapper.CouponMapper;
import com.vouchervault.backend.model.Coupon;
import com.vouchervault.backend.model.User;
import com.vouchervault.backend.repository.CouponRepository;
import com.vouchervault.backend.repository.UserRepository;
import com.vouchervault.backend.utils.AppConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponService {

    private final CouponRepository couponRepository;
    private final UserRepository userRepository;
    private final EncryptionService encryptionService;
    private final CouponMapper couponMapper;

    @Transactional
    public CouponResponse createCoupon(UUID userId, CouponRequest request) {
        log.debug(AppConstants.LOG_ENTRY, "createCoupon", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId.toString()));

        String encryptedCode = encryptionService.encrypt(request.getCode(), userId.toString());

        Coupon coupon = couponMapper.toEntity(request);
        coupon.setUser(user);
        coupon.setCodeEncrypted(encryptedCode);

        Coupon savedCoupon = couponRepository.save(coupon);
        log.info(AppConstants.LOG_INFO, "Coupon created with id: " + savedCoupon.getId());

        CouponResponse response = couponMapper.toResponse(savedCoupon);
        response.setCode(request.getCode());

        log.debug(AppConstants.LOG_EXIT, "createCoupon");
        return response;
    }

    public Page<CouponResponse> getUserCoupons(UUID userId, Pageable pageable) {
        log.debug(AppConstants.LOG_ENTRY, "getUserCoupons", userId);
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId.toString()));

        Page<Coupon> coupons = couponRepository.findByUserIdAndDeletedAtIsNull(userId, pageable);
        Page<CouponResponse> responses = coupons.map(coupon -> {
            CouponResponse response = couponMapper.toResponse(coupon);
            decryptCode(response, coupon.getCodeEncrypted(), userId.toString());
            return response;
        });

        log.debug(AppConstants.LOG_EXIT, "getUserCoupons with " + responses.getTotalElements() + " elements");
        return responses;
    }

    public CouponResponse getCoupon(UUID userId, UUID couponId) {
        log.debug(AppConstants.LOG_ENTRY, "getCoupon", couponId);
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId.toString()));

        Coupon coupon = couponRepository.findByIdAndUserIdAndDeletedAtIsNull(couponId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon", "id", couponId.toString()));

        CouponResponse response = couponMapper.toResponse(coupon);
        decryptCode(response, coupon.getCodeEncrypted(), userId.toString());

        log.debug(AppConstants.LOG_EXIT, "getCoupon");
        return response;
    }

    @Transactional
    public CouponResponse updateCoupon(UUID userId, UUID couponId, CouponRequest request) {
        log.debug(AppConstants.LOG_ENTRY, "updateCoupon", couponId);
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId.toString()));

        Coupon coupon = couponRepository.findByIdAndUserIdAndDeletedAtIsNull(couponId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon", "id", couponId.toString()));

        couponMapper.updateEntityFromRequest(request, coupon);
        if (request.getCode() != null) {
            coupon.setCodeEncrypted(encryptionService.encrypt(request.getCode(), userId.toString()));
        }

        Coupon updatedCoupon = couponRepository.save(coupon);
        log.info(AppConstants.LOG_INFO, "Coupon updated with id: " + updatedCoupon.getId());

        CouponResponse response = couponMapper.toResponse(updatedCoupon);
        response.setCode(request.getCode());

        log.debug(AppConstants.LOG_EXIT, "updateCoupon");
        return response;
    }

    @Transactional
    public void deleteCoupon(UUID userId, UUID couponId) {
        log.debug(AppConstants.LOG_ENTRY, "deleteCoupon", couponId);
        Coupon coupon = couponRepository.findByIdAndUserIdAndDeletedAtIsNull(couponId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon", "id", couponId.toString()));

        coupon.setDeletedAt(java.time.LocalDateTime.now());
        couponRepository.save(coupon);
        log.info(AppConstants.LOG_INFO, "Coupon soft-deleted with id: " + couponId);
        log.debug(AppConstants.LOG_EXIT, "deleteCoupon");
    }

    private void decryptCode(CouponResponse response, String encryptedCode, String userId) {
        try {
            response.setCode(encryptionService.decrypt(encryptedCode, userId));
        } catch (Exception e) {
            log.error(AppConstants.LOG_ERROR, "Failed to decrypt code", e.getMessage());
            response.setCode(AppConstants.DECRYPTION_ERROR);
        }
    }
}
