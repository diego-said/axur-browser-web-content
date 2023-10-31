package com.axreng.backend.search.multithreading;

import com.axreng.backend.Main;
import com.axreng.backend.net.HttpRequest;
import com.axreng.backend.net.HttpResponse;
import com.axreng.backend.search.entities.Search;
import com.axreng.backend.util.SearchUtils;
import org.slf4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public interface SearchPerformable {

    int getMaxRetires();
    Logger getLogger();

    default Set<String> perform(Search search, String baseUrl) {
        int searchRetries = 1;

        while (searchRetries < getMaxRetires()) {
            try {
                final HttpRequest request = new HttpRequest(baseUrl);
                final HttpResponse response = request.get();

                if (response.isSuccessful()) {
                    boolean keywordFound = SearchUtils.isKeywordFound(search.getKeyword(), response.getContent());
                    if(keywordFound) {
                        search.getUrls().add(response.getUrl());
                    }

                    final Set<String> linksSet = new HashSet<>();

                    SearchUtils.getLinks(response).stream().filter(link -> {
                        try {
                            final URI baseURL = new URI(baseUrl);
                            final URI uri = new URI(String.valueOf(link));
                            return baseURL.getHost().equals(uri.getHost());
                        } catch (URISyntaxException e) {
                            throw new RuntimeException(e);
                        }
                    }).forEach(linksSet::add);
                    return linksSet;
                } else if (response.isServerError()) {
                    searchRetries++;
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    return Collections.emptySet();
                }
            }  catch (RuntimeException e) {
                getLogger().error("SearchPerformable - request url: " + Main.BASE_URL, e);
                searchRetries++;
            }
        }
        return Collections.emptySet();
    }

}
