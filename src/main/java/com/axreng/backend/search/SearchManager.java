package com.axreng.backend.search;

import com.axreng.backend.net.HttpRequest;
import com.axreng.backend.net.HttpResponse;
import com.axreng.backend.search.entities.Search;
import com.axreng.backend.util.SearchUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class SearchManager {

    private final String baseUrl = System.getenv("BASE_URL");

    private static SearchManager INSTANCE;

    private final List<Search> searchList;

    public SearchManager() {
        this.searchList = new ArrayList<>();
    }

    public synchronized static SearchManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SearchManager();
        }
        return INSTANCE;
    }

    public synchronized void addSearch(Search search) {
        searchList.add(search);
    }

    public synchronized void performNextSearchInQueue() {
        final Search search = searchList.remove(0);
        final HttpRequest request = new HttpRequest(baseUrl);
        final HttpResponse response = request.get();

        System.out.println("Keyword: " + SearchUtils.isKeywordFound(search.getKeyword(), response.getContent()));

        SearchUtils.getLinks(response).forEach(link -> {
            System.out.println(link);
            try {
                URI baseURL = new URI(baseUrl);
                URI uri = new URI(String.valueOf(link));
                System.out.println(baseURL.getHost().equals(uri.getHost()));
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

}