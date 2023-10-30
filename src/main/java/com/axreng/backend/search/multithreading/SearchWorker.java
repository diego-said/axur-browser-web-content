package com.axreng.backend.search.multithreading;

import com.axreng.backend.Main;
import com.axreng.backend.net.HttpRequest;
import com.axreng.backend.net.HttpResponse;
import com.axreng.backend.search.entities.Search;
import com.axreng.backend.search.entities.SearchStatus;
import com.axreng.backend.util.SearchUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.BlockingQueue;

public class SearchWorker implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(SearchWorker.class);

    private final BlockingQueue<Search> queue;

    public SearchWorker(BlockingQueue<Search> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Search search = queue.take();
                performSearch(search);
            }
        } catch (InterruptedException e) {
            logger.error("SearchWorker - ", e);
            Thread.currentThread().interrupt();
        }
    }

    private void performSearch(Search search) {
        search.setStatus(SearchStatus.active);

        final HttpRequest request = new HttpRequest(Main.BASE_URL);
        final HttpResponse response = request.get();

        boolean keywordFound = SearchUtils.isKeywordFound(search.getKeyword(), response.getContent());
        if(keywordFound) {
            search.getUrls().add(response.getUrl());
        }

        SearchUtils.getLinks(response).stream().filter(link -> {
            try {
                final URI baseURL = new URI(Main.BASE_URL);
                final URI uri = new URI(String.valueOf(link));
                return baseURL.getHost().equals(uri.getHost());
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }).forEach(logger::info);

        search.setStatus(SearchStatus.done);
    }

}
