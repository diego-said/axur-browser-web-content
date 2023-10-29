package com.axreng.backend.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SearchUtilsTest {

    @Test
    void testGetKeyword() {
        String searchId = SearchUtils.randomId();

        Assertions.assertFalse(searchId.isBlank());
        Assertions.assertEquals(SearchUtils.MAX_ID_LENGTH, searchId.length());
    }

}
