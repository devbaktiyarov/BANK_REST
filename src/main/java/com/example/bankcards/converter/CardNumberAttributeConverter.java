package com.example.bankcards.converter;


import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.util.Base64;

@RequiredArgsConstructor
@Converter
public class CardNumberAttributeConverter implements AttributeConverter<String, String> {

    private final SecretKey secretKey;

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) return null;
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] enc = cipher.doFinal(attribute.getBytes());
            return Base64.getEncoder().encodeToString(enc);
        } catch (Exception e) {
            throw new IllegalStateException("Encryption error", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] dec = cipher.doFinal(Base64.getDecoder().decode(dbData));
            return new String(dec);
        } catch (Exception e) {
            throw new IllegalStateException("Decryption error", e);
        }
    }
}



