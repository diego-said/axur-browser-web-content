package com.axreng.backend.net;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.HttpURLConnection;

public class HttpRequestTest {

    @Test
    void testGet() {
        HttpRequest httpRequest = new HttpRequest("http://hiring.axreng.com/");
        HttpResponse response = httpRequest.get();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(HttpURLConnection.HTTP_OK, response.getStatus());
        Assertions.assertTrue(response.isSuccessful());
        Assertions.assertFalse(response.getContent().isEmpty());
    }

    @Test
    void testGetWithInvalidUrl() {
        HttpRequest httpRequest = new HttpRequest("http://not.a.url/");

        Assertions.assertThrows(RuntimeException.class, httpRequest::get);
    }

}