package com.vouchervault.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CouponRequest {
    private String merchant;
    private String code;
    private String discountDetails;
    private String description;
    private LocalDateTime expiryDate;
    private java.math.BigDecimal minAmount;
}
