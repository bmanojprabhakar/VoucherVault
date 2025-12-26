package com.vouchervault.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "coupons")
@Getter
@Setter
@NoArgsConstructor
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    @jakarta.validation.constraints.NotNull
    private String merchant;

    @Column(nullable = false, name = "code_encrypted")
    @jakarta.validation.constraints.NotNull
    private String codeEncrypted;

    @Column(name = "expiry_date")
    @jakarta.validation.constraints.NotNull
    private LocalDateTime expiryDate;

    @Column(name = "min_amount")
    private java.math.BigDecimal minAmount;

    @Column(name = "discount_details", columnDefinition = "TEXT")
    private String discountDetails;

    @Column(columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
