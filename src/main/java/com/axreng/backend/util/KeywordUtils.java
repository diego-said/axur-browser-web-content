package com.axreng.backend.util;

import com.google.gson.Gson;

import java.util.Map;
import java.util.Optional;

public final class KeywordUtils {

    private final static int MIN_KEYWORD_LENGTH = 4;
    private final static int MAX_KEYWORD_LENGTH = 32;

    public static Optional<String> getKeyword(String requestBody) {
        if(!requestBody.isBlank()) {
            Map<?, ?> map = new Gson().fromJson(requestBody, Map.class);
            if (Optional.ofNullable(map.get("keyword")).isPresent()) {
                return String.valueOf(map.get("keyword")).describeConstable();
            }
        }
        return Optional.empty();
    }

    public static boolean isKeywordValid(String keyword) {
        return keyword != null &&
                !keyword.isBlank() &&
                keyword.length() >= MIN_KEYWORD_LENGTH &&
                keyword.length() <= MAX_KEYWORD_LENGTH;
    }

}
