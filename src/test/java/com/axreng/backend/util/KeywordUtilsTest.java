package com.axreng.backend.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class KeywordUtilsTest {

    @Test
    void testGetKeyword() {
        String body = "{\"keyword\": \"security\"}";

        Optional<String> keyword = KeywordUtils.getKeyword(body);

        Assertions.assertTrue(keyword.isPresent());
        Assertions.assertEquals("security", keyword.get());
    }

    @Test
    void testGetKeywordInvalidKeyword() {
        String body = "{\"key\": \"security\"}";

        Optional<String> keyword = KeywordUtils.getKeyword(body);

        Assertions.assertFalse(keyword.isPresent());
    }

    @Test
    void testGetKeywordWithEmptyObject() {
        String body = "{}";

        Optional<String> keyword = KeywordUtils.getKeyword(body);

        Assertions.assertFalse(keyword.isPresent());
    }

    @Test
    void testGetKeywordWithEmptyBody() {
        String body = "";

        Optional<String> keyword = KeywordUtils.getKeyword(body);

        Assertions.assertFalse(keyword.isPresent());
    }

    @Test
    void testIsKeywordValid() {
        String keyword = "security";

        Assertions.assertTrue(KeywordUtils.isKeywordValid(keyword));
    }

    @Test
    void testIsKeywordValidWithInvalidKeyword() {
        String keyword = "sec";

        Assertions.assertFalse(KeywordUtils.isKeywordValid(keyword));
    }

    @Test
    void testIsKeywordValidWithEmptyKeyword() {
        String keyword = "";

        Assertions.assertFalse(KeywordUtils.isKeywordValid(keyword));
    }

}
