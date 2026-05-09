package com.hospital.authservice.utils;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class CryptoConverter implements AttributeConverter<String, String> {

    @Override
    public String convertToDatabaseColumn(String attribute) {
        try {
            return attribute == null ? null : CryptoUtil.encrypt(attribute);
        } catch (Exception e) {
            throw new RuntimeException("Error encriptando", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        try {
            return dbData == null ? null : CryptoUtil.decrypt(dbData);
        } catch (Exception e) {
            throw new RuntimeException("Error desencriptando", e);
        }
    }
}