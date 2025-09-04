package com.email.email.writer.service;

import java.util.Map;

public record MaskedData(String maskedText, Map<String, String> replacements) {}

