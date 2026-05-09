package com.hospital.authservice.utils;

import java.util.HashMap;
import java.util.Map;

public class CryptoUtil {

    private static final String ORIGINAL = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789.@_-";
    private static final String SUSTITUTO = "QwErTyUiOpAsDfGhJkLzXcVbNm9876543210_-@.MNBVCXZLKJHGFDSAPOIUYTREWQ";

    private static final Map<Character, Character> MAP_ENCRYPT = new HashMap<>();
    private static final Map<Character, Character> MAP_DECRYPT = new HashMap<>();

    static {
        for (int i = 0; i < ORIGINAL.length(); i++) {
            MAP_ENCRYPT.put(ORIGINAL.charAt(i), SUSTITUTO.charAt(i));
            MAP_DECRYPT.put(SUSTITUTO.charAt(i), ORIGINAL.charAt(i));
        }
    }

    public static String encrypt(String input) {
        if (input == null) return null;

        StringBuilder result = new StringBuilder();
        for (char c : input.toCharArray()) {
            result.append(MAP_ENCRYPT.getOrDefault(c, c));
        }
        return result.toString();
    }

    public static String decrypt(String input) {
        if (input == null) return null;

        StringBuilder result = new StringBuilder();
        for (char c : input.toCharArray()) {
            result.append(MAP_DECRYPT.getOrDefault(c, c));
        }
        return result.toString();
    }
}