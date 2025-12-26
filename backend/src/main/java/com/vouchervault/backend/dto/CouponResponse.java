package com.vouchervault.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CouponResponse {
    private UUID id;
    private String merchant;
    private String code;
    private String discountDetails;
    private String description;
    private LocalDateTime expiryDate;
    private java.math.BigDecimal minAmount;
    private LocalDateTime createdAt;
}
