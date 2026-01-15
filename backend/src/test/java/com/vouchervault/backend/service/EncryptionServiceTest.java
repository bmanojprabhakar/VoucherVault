package com.vouchervault.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class EncryptionServiceTest {

    private EncryptionService encryptionService;
    private final String masterSecret = "test-master-secret-32-chars-long!!";
    private final String userSocialId = "google-12345";
    private final String rawData = "MY_SECRET_COUPON_CODE_123";

    @BeforeEach
    void setUp() {
        encryptionService = new EncryptionService();
        ReflectionTestUtils.setField(encryptionService, "masterSecret", masterSecret);
    }

    @Test
    void encryptAndDecrypt_Success() {
        String encrypted = encryptionService.encrypt(rawData, userSocialId);
        assertNotNull(encrypted);
        assertNotEquals(rawData, encrypted);

        String decrypted = encryptionService.decrypt(encrypted, userSocialId);
        assertEquals(rawData, decrypted);
    }

    @Test
    void decryptWithWrongUser_Fails() {
        String encrypted = encryptionService.encrypt(rawData, userSocialId);

        // Decrypting with a different user ID should fail or return wrong data
        // For GCM with different keys, it will likely throw an exception during
        // decryption
        assertThrows(RuntimeException.class, () -> {
            encryptionService.decrypt(encrypted, "other-user-56789");
        });
    }

    @Test
    void encryptNull_ReturnsNull() {
        assertNull(encryptionService.encrypt(null, userSocialId));
    }

    @Test
    void decryptNull_ReturnsNull() {
        assertNull(encryptionService.decrypt(null, userSocialId));
    }

    @Test
    void deterministicKeyDerivation() {
        // Since we can't easily access the private deriveKey, we verify it indirectly
        // by checking that the same input always decrypts with the same key.
        String encrypted1 = encryptionService.encrypt(rawData, userSocialId);
        String decrypted1 = encryptionService.decrypt(encrypted1, userSocialId);
        assertEquals(rawData, decrypted1);
    }
}
