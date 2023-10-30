package com.axreng.backend.util;

import com.axreng.backend.net.HttpRequest;
import com.axreng.backend.net.HttpResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.HttpURLConnection;
import java.util.List;

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

    @Test
    void testIsKeywordFound() {
        HttpRequest httpRequest = new HttpRequest("http://hiring.axreng.com/");
        HttpResponse response = httpRequest.get();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpURLConnection.HTTP_OK, response.getStatus());
        Assertions.assertFalse(response.getContent().isEmpty());

        Assertions.assertTrue(SearchUtils.isKeywordFound("linux", response.getContent()));
    }

    @Test
    void testGetLinks() {
        HttpRequest httpRequest = new HttpRequest("http://hiring.axreng.com/");
        HttpResponse response = httpRequest.get();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpURLConnection.HTTP_OK, response.getStatus());
        Assertions.assertFalse(response.getContent().isEmpty());

        List<String> linkList = SearchUtils.getLinks(response);

        Assertions.assertFalse(linkList.isEmpty());
    }

}
