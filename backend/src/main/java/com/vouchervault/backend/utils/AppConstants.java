package com.vouchervault.backend.utils;

public class AppConstants {

    private AppConstants() {
        // Private constructor to prevent instantiation
    }

    // Auth Provider Keys
    public static final String GOOGLE_PROVIDER = "google";
    public static final String FACEBOOK_PROVIDER = "facebook";
    public static final String GITHUB_PROVIDER = "github";

    // Response Keys
    public static final String GOOGLE_CODE_KEY = "code";
    public static final String GOOGLE_TOKEN = "token";
    public static final String GOOGLE_TOKEN_KEY = "access_token";

    // Success Messages
    public static final String COUPON_CREATED_SUCCESS = "Coupon created successfully";
    public static final String COUPON_UPDATED_SUCCESS = "Coupon updated successfully";
    public static final String COUPON_DELETED_SUCCESS = "Coupon deleted successfully";
    public static final String AUTH_SUCCESS = "Authentication successful";

    // Error Messages
    public static final String RESOURCE_NOT_FOUND = "Resource not found with %s: %s";
    public static final String AUTHENTICATION_FAILED = "Authentication failed: %s";
    public static final String INTERNAL_SERVER_ERROR = "An unexpected error occurred. Please try again later.";
    public static final String ACCESS_DENIED = "You do not have permission to access this resource.";
    public static final String INVALID_INPUT = "Invalid input provided.";
    public static final String INVALID_TOKEN = "Invalid or expired token.";
    public static final String DECRYPTION_ERROR = "Error Decrypting";
    public static final String UNSUPPORTED_PROVIDER = "Unsupported authentication provider: %s";

    // Log Templates
    public static final String LOG_ENTRY = "Entering {} with payload: {}";
    public static final String LOG_EXIT = "Exiting {}";
    public static final String LOG_ERROR = "Error occurred in {}: {}";
    public static final String LOG_INFO = "Action successful: {}";
    public static final String LOG_DEBUG = "[DEBUG] {}: {}";

    // Swagger Descriptions
    public static final String SWAGGER_AUTH_TAG = "Authentication";
    public static final String SWAGGER_AUTH_DESC = "Endpoints for user authentication and token management";
    public static final String SWAGGER_COUPON_TAG = "Coupons";
    public static final String SWAGGER_COUPON_DESC = "Endpoints for managing user coupons";
    public static final String SWAGGER_USER_TAG = "Users";
    public static final String SWAGGER_USER_DESC = "Endpoints for user profile management";

    // API Paths
    public static final String API_V1_AUTH = "/api/v1/auth";
    public static final String API_COUPONS = "/api/coupons";
    public static final String API_USER = "/api/user";

    // Error Codes
    public static final String ERROR_CODE_NOT_FOUND = "ERR_404";
    public static final String ERROR_CODE_UNAUTHORIZED = "ERR_401";
    public static final String ERROR_CODE_FORBIDDEN = "ERR_403";
    public static final String ERROR_CODE_BAD_REQUEST = "ERR_400";
    public static final String ERROR_CODE_INTERNAL_SERVER = "ERR_500";

    // Cleanup Service
    public static final String CLEANUP_START = "Starting cleanup of old deleted coupons...";
    public static final String CLEANUP_FINISH = "Cleanup finished. Purged {} coupons older than 30 days.";
}
