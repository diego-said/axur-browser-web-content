package com.axreng.backend.search.services;

import com.axreng.backend.search.entities.Search;
import com.axreng.backend.util.KeywordUtils;
import com.axreng.backend.util.SearchUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SearchService {

    private static SearchService INSTANCE;

    private final Map<String, Search> searches;

    private SearchService() {
        this.searches = new HashMap<>();
    }

    public synchronized static SearchService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SearchService();
        }
        return INSTANCE;
    }

    public synchronized Search createSearch(String keyword) {
        if (!KeywordUtils.isKeywordValid(keyword))
            throw new IllegalArgumentException("invalid keyword");

        final Search search = new Search(SearchUtils.randomId(), keyword);
        searches.put(search.getId(), search);
        return search;
    }

    public synchronized Optional<Search> getSearchById(String searchId) {
        return Optional.ofNullable(searches.get(searchId));
    }

}
