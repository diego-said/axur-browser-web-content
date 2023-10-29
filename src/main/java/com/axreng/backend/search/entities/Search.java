package com.axreng.backend.search.entities;

public class Search {

    private final String id;

    public Search(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getIdAsJson() {
        return "{" +
                "\"id\": \"" + id + '\"' +
                '}';
    }

    @Override
    public String toString() {
        return "Search{" +
                "id='" + id + '\'' +
                '}';
    }
}
