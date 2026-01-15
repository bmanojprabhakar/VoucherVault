package com.vouchervault.backend.controller;

import com.vouchervault.backend.dto.ApiResponseDto;
import com.vouchervault.backend.dto.CouponRequest;
import com.vouchervault.backend.dto.CouponResponse;
import com.vouchervault.backend.service.CouponService;
import com.vouchervault.backend.utils.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping(AppConstants.API_COUPONS)
@RequiredArgsConstructor
@Slf4j
@Tag(name = AppConstants.SWAGGER_COUPON_TAG, description = AppConstants.SWAGGER_COUPON_DESC)
public class CouponController {

    private final CouponService couponService;

    @PostMapping
    @Operation(summary = "Create a new coupon", description = "Creates a new encrypted coupon for the authenticated user")
    public ResponseEntity<ApiResponseDto<CouponResponse>> createCoupon(
            @AuthenticationPrincipal String userId,
            @RequestBody CouponRequest request) {
        log.debug(AppConstants.LOG_ENTRY, "createCoupon", userId);
        CouponResponse data = couponService.createCoupon(UUID.fromString(userId), request);
        ApiResponseDto<CouponResponse> response = buildSuccessResponse(AppConstants.COUPON_CREATED_SUCCESS, data);
        log.info(AppConstants.LOG_INFO, AppConstants.COUPON_CREATED_SUCCESS);
        log.debug(AppConstants.LOG_EXIT, "createCoupon");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get user coupons", description = "Fetches a paginated list of coupons for the authenticated user")
    public ResponseEntity<ApiResponseDto<Page<CouponResponse>>> getCoupons(
            @AuthenticationPrincipal String userId,
            @org.springframework.data.web.SortDefault(sort = "expiryDate", direction = org.springframework.data.domain.Sort.Direction.ASC) Pageable pageable) {
        log.debug(AppConstants.LOG_ENTRY, "getCoupons", userId);
        Page<CouponResponse> data = couponService.getUserCoupons(UUID.fromString(userId), pageable);
        ApiResponseDto<Page<CouponResponse>> response = buildSuccessResponse("Coupons fetched successfully", data);
        log.debug(AppConstants.LOG_EXIT, "getCoupons");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get coupon by ID", description = "Fetches details of a specific coupon by its ID")
    public ResponseEntity<ApiResponseDto<CouponResponse>> getCoupon(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID id) {
        log.debug(AppConstants.LOG_ENTRY, "getCoupon", id);
        CouponResponse data = couponService.getCoupon(UUID.fromString(userId), id);
        ApiResponseDto<CouponResponse> response = buildSuccessResponse("Coupon fetched successfully", data);
        log.debug(AppConstants.LOG_EXIT, "getCoupon");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update coupon", description = "Updates an existing coupon for the authenticated user")
    public ResponseEntity<ApiResponseDto<CouponResponse>> updateCoupon(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID id,
            @RequestBody CouponRequest request) {
        log.debug(AppConstants.LOG_ENTRY, "updateCoupon", id);
        CouponResponse data = couponService.updateCoupon(UUID.fromString(userId), id, request);
        ApiResponseDto<CouponResponse> response = buildSuccessResponse(AppConstants.COUPON_UPDATED_SUCCESS, data);
        log.info(AppConstants.LOG_INFO, AppConstants.COUPON_UPDATED_SUCCESS);
        log.debug(AppConstants.LOG_EXIT, "updateCoupon");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete coupon", description = "Soft-deletes a coupon for the authenticated user")
    public ResponseEntity<ApiResponseDto<Void>> deleteCoupon(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID id) {
        log.debug(AppConstants.LOG_ENTRY, "deleteCoupon", id);
        couponService.deleteCoupon(UUID.fromString(userId), id);
        ApiResponseDto<Void> response = buildSuccessResponse(AppConstants.COUPON_DELETED_SUCCESS, null);
        log.info(AppConstants.LOG_INFO, AppConstants.COUPON_DELETED_SUCCESS);
        log.debug(AppConstants.LOG_EXIT, "deleteCoupon");
        return ResponseEntity.ok(response);
    }

    private <T> ApiResponseDto<T> buildSuccessResponse(String message, T data) {
        return ApiResponseDto.<T>builder()
                .status("SUCCESS")
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
