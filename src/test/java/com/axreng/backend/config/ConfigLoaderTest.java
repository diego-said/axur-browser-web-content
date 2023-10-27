package com.axreng.backend.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class ConfigLoaderTest {

    private final ConfigLoader configLoader = ConfigLoader.getInstance();

    @Test
    void testGetConfigAsString() {
        Optional<String> config = configLoader.getConfigAsString("test_string");

        Assertions.assertTrue(config.isPresent());
        Assertions.assertEquals("test", config.get());
    }

    @Test
    void testGetConfigAsStringWithEmptyString() {
        Optional<String> config = configLoader.getConfigAsString("test_empty");

        Assertions.assertTrue(config.isPresent());
        Assertions.assertTrue(config.get().isBlank());
    }

    @Test
    void testGetConfigAsStringWithInvalidName() {
        Optional<String> config = configLoader.getConfigAsString("not_found");

        Assertions.assertFalse(config.isPresent());
    }

    @Test
    void testGetConfigAsInteger() {
        Optional<Integer> config = configLoader.getConfigAsInteger("test_integer");

        Assertions.assertTrue(config.isPresent());
        Assertions.assertEquals(10, config.get());
    }

    @Test
    void testGetConfigAsIntegerWithEmptyInteger() {
        Optional<Integer> config = configLoader.getConfigAsInteger("test_empty");

        Assertions.assertFalse(config.isPresent());
    }

    @Test
    void testGetConfigAsIntegerWithInvalidName() {
        Optional<Integer> config = configLoader.getConfigAsInteger("not_found");

        Assertions.assertFalse(config.isPresent());
    }

    @Test
    void testGetConfigAsIntegerWithNaN() {
        Assertions.assertThrows(NumberFormatException.class, () -> configLoader.getConfigAsInteger("test_nan"));
    }

}
