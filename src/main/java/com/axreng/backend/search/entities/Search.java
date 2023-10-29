package com.axreng.backend.search.entities;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Search {

    @Expose
    private final String id;

    @Expose
    private SearchStatus status;

    private final String keyword;

    @Expose
    private final List<String> urls;

    public Search(String id, String keyword) {
        this.id = id;
        this.keyword = keyword;
        status = SearchStatus.create;
        urls = Collections.synchronizedList(new ArrayList<>());
    }

    public String getId() {
        return id;
    }

    public String getIdAsJson() {
        return "{" +
                "\"id\": \"" + id + '\"' +
                '}';
    }

    public String getKeyword() {
        return keyword;
    }

    public List<String> getUrls() {
        return urls;
    }

    public SearchStatus getStatus() {
        return status;
    }

    public void setStatus(SearchStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Search{" +
                "id='" + id + '\'' +
                ", status=" + status +
                ", keyword='" + keyword + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Search search = (Search) o;
        return Objects.equals(id, search.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
