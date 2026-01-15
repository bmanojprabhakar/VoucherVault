package com.vouchervault.backend.mapper;

import com.vouchervault.backend.dto.CouponRequest;
import com.vouchervault.backend.dto.CouponResponse;
import com.vouchervault.backend.model.Coupon;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CouponMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "codeEncrypted", ignore = true)
    Coupon toEntity(CouponRequest request);

    @Mapping(target = "code", ignore = true)
    CouponResponse toResponse(Coupon coupon);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "codeEncrypted", ignore = true)
    void updateEntityFromRequest(CouponRequest request, @MappingTarget Coupon coupon);
}
