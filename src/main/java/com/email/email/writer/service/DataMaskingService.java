package com.email.email.writer.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DataMaskingService {

    // Regex to detect different sensitive data types
    private static final Pattern SENSITIVE_DATA_PATTERN = Pattern.compile(
            "(?<EMAIL>[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})|" +    // Email
            "(?<CARD>\\b(?:\\d[ -]*?){13,16}\\b)|" +                          // Credit Card
            "(?<AADHAR>\\b\\d{12}\\b)|" +                                     // Aadhaar
            "(?<PAN>[A-Z]{5}[0-9]{4}[A-Z]{1})|" +                             // PAN
            "(?<PASSWORD>(?i)(password\\s*[:=]\\s*\\S+))|" +                  // Password
            "(?<DOB>\\b\\d{2}[-/\\.]\\d{2}[-/\\.]\\d{4}\\b)|" +               // DOB
            "(?<PHONE>\\b\\+?91[- ]?[6-9]\\d{9}\\b|\\b[6-9]\\d{9}\\b)"        // Phone
    );

    /**
     * Masks sensitive data partially so users can still recognize it.
     */
    public MaskedData mask(String text) {
        if (text == null || text.isEmpty()) {
            return new MaskedData(text, new HashMap<>());
        }

        Map<String, String> replacements = new HashMap<>();
        Matcher matcher = SENSITIVE_DATA_PATTERN.matcher(text);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            String originalValue = matcher.group();
            String maskedValue;

            if (matcher.group("EMAIL") != null) {
                maskedValue = maskEmail(originalValue);
            } else if (matcher.group("CARD") != null) {
                maskedValue = maskCard(originalValue);
            } else if (matcher.group("AADHAR") != null) {
                maskedValue = maskAadhar(originalValue);
            } else if (matcher.group("PAN") != null) {
                maskedValue = maskPan(originalValue);
            } else if (matcher.group("PASSWORD") != null) {
                maskedValue = "password=********"; // Fully masked
            } else if (matcher.group("DOB") != null) {
                maskedValue = "XX-XX-" + originalValue.substring(originalValue.length() - 4); // Keep year
            } else if (matcher.group("PHONE") != null) {
                maskedValue = maskPhone(originalValue);
            } else {
                maskedValue = "-------MASKED-------";
            }

            replacements.put(maskedValue, originalValue);
            matcher.appendReplacement(sb, maskedValue);
        }
        matcher.appendTail(sb);

        return new MaskedData(sb.toString(), replacements);
    }

    // ---------------------- Masking Rules ----------------------

    private String maskEmail(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex <= 2) return "****" + email.substring(atIndex);
        return email.substring(0, 2) + "****" + email.substring(atIndex);
    }

    private String maskCard(String card) {
        card = card.replaceAll("[^0-9]", ""); // remove spaces/hyphens
        return "XXXX-XXXX-XXXX-" + card.substring(card.length() - 4);
    }

    private String maskAadhar(String aadhar) {
        return "XXXX-XXXX-" + aadhar.substring(aadhar.length() - 4);
    }

    private String maskPan(String pan) {
        return pan.substring(0, 2) + "XXXXX" + pan.substring(pan.length() - 1);
    }

    private String maskPhone(String phone) {
        String digits = phone.replaceAll("[^0-9]", "");
        return "XXXXXX" + digits.substring(digits.length() - 4);
    }

    /**
     * Restores masked values back to original.
     */
    public String unmask(String text, Map<String, String> replacements) {
        if (text == null || replacements == null || replacements.isEmpty()) {
            return text;
        }

        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            text = text.replace(entry.getKey(), entry.getValue());
        }
        return text;
    }
}
