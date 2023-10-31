package com.axreng.backend.config;

public enum ConfigNames {

    CONNECTION_TIMEOUT("http.request.connection.timeout"),
    READ_TIMEOUT("http.request.read.timeout"),
    QUEUE_SIZE("search.queue.size"),
    NUMBER_OF_WORKERS("search.workers"),
    MAX_RETRIES("search.max.retries"),
    URLS_MAX_SIZE("search.urls.max.size"),
    MAX_IDLE_TIME("search.request.processor.max.idle.time"),
    REQUEST_PROCESSORS("search.request.processors"),
    REQUEST_PROCESSOR_LINKS_QUEUE_SIZE("search.request.processor.links.queue.size"),
    ;

    private final String name;

    ConfigNames(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
