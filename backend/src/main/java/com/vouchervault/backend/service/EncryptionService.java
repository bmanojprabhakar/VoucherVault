package com.vouchervault.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class EncryptionService {

    @Value("${app.encryption.secret}")
    private String masterSecret;

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    private SecretKey deriveKey(String userSocialId) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(masterSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] keyBytes = sha256_HMAC.doFinal(userSocialId.getBytes(StandardCharsets.UTF_8));
            return new SecretKeySpec(keyBytes, "AES");
        } catch (Exception e) {
            throw new RuntimeException("Error deriving encryption key", e);
        }
    }

    public String encrypt(String rawData, String userSocialId) {
        if (rawData == null)
            return null;
        try {
            SecretKey key = deriveKey(userSocialId);
            byte[] iv = new byte[GCM_IV_LENGTH];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, key, spec);

            byte[] cipherText = cipher.doFinal(rawData.getBytes(StandardCharsets.UTF_8));

            // Return Base64(IV + CipherText)
            byte[] combined = new byte[iv.length + cipherText.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(cipherText, 0, combined, iv.length, cipherText.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting data", e);
        }
    }

    public String decrypt(String encryptedData, String userSocialId) {
        if (encryptedData == null)
            return null;
        try {
            byte[] combined = Base64.getDecoder().decode(encryptedData);

            byte[] iv = new byte[GCM_IV_LENGTH];
            System.arraycopy(combined, 0, iv, 0, iv.length);

            int cipherTextLength = combined.length - GCM_IV_LENGTH;
            byte[] cipherText = new byte[cipherTextLength];
            System.arraycopy(combined, GCM_IV_LENGTH, cipherText, 0, cipherTextLength);

            SecretKey key = deriveKey(userSocialId);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, key, spec);

            byte[] plainText = cipher.doFinal(cipherText);
            return new String(plainText, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting data", e);
        }
    }
}
