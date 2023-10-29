package com.axreng.backend.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SearchUtilsTest {

    @Test
    void testRandomId() {
        String searchId = SearchUtils.randomId();

        Assertions.assertFalse(searchId.isBlank());
        Assertions.assertEquals(SearchUtils.MAX_ID_LENGTH, searchId.length());
    }

    @Test
    void testIsSearchIdValid() {
        String searchId = SearchUtils.randomId();

        Assertions.assertTrue(SearchUtils.isSearchIdValid(searchId));
    }

    @Test
    void testIsSearchIdValidWithInvalidId() {
        String searchId = "invalid_search_id";

        Assertions.assertFalse(SearchUtils.isSearchIdValid(searchId));
    }

    @Test
    void testIsSearchIdValidWithEmptyId() {
        String searchId = "";

        Assertions.assertFalse(SearchUtils.isSearchIdValid(searchId));
    }

}
