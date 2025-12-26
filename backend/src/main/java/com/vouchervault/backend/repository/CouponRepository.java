package com.vouchervault.backend.repository;

import com.vouchervault.backend.model.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, UUID> {
    Page<Coupon> findByUserIdAndDeletedAtIsNull(UUID userId, Pageable pageable);

    Optional<Coupon> findByIdAndUserIdAndDeletedAtIsNull(UUID id, UUID userId);

    List<Coupon> findByDeletedAtIsNotNull();
}
