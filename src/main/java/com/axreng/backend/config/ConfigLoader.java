package com.axreng.backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

public final class ConfigLoader {

    private static final String fileName = "config.properties";

    private static ConfigLoader INSTANCE;

    private final Logger logger = LoggerFactory.getLogger(ConfigLoader.class);

    private Properties properties;

    private ConfigLoader() {
    }

    public synchronized static ConfigLoader getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ConfigLoader();
            INSTANCE.load();
        }
        return INSTANCE;
    }

    private void load() {
        if(properties ==  null) {
            properties = new Properties();
            try {
                properties.load(ConfigLoader.class.getClassLoader().getResourceAsStream(fileName));
            } catch (IOException e) {
                logger.error("Failed to load file ["+ fileName +"]", e);
                throw new RuntimeException(e);
            }
        }
    }

    public Optional<String> getConfigAsString(String configName) {
        return Optional.ofNullable(properties.getProperty(configName));
    }

    public Optional<Integer> getConfigAsInteger(ConfigNames name) {
        return getConfigAsInteger(name.toString());
    }

    public Optional<Integer> getConfigAsInteger(String configName) {
        Optional<String> value = Optional.ofNullable(properties.getProperty(configName));
        if (value.isPresent() && !value.get().isBlank())
            return Optional.of(Integer.valueOf(value.get()));
        return Optional.empty();
    }

}
