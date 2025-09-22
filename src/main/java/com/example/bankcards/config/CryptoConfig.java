package com.example.bankcards.config;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CryptoConfig {
    
    @Value("${security.encryption.key}")
    private String encryptionKey;

    @Bean
    public SecretKey aesKey() {
        byte[] keyBytes = encryptionKey.getBytes();
        return new SecretKeySpec(keyBytes, 0, Math.min(keyBytes.length, 32), "AES");
    }
}

