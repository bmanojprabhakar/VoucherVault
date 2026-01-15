package com.vouchervault.backend.exception;

import com.vouchervault.backend.utils.AppConstants;

public class ResourceNotFoundException extends BaseException {
    public ResourceNotFoundException(String resourceName, String fieldName, String fieldValue) {
        super(String.format(AppConstants.RESOURCE_NOT_FOUND, resourceName, fieldName, fieldValue),
                AppConstants.ERROR_CODE_NOT_FOUND);
    }
}
