package com.axreng.backend.search.multithreading;

import com.axreng.backend.Main;
import com.axreng.backend.config.ConfigLoader;
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
import java.util.concurrent.TimeUnit;

public class SearchWorker implements Runnable {

    private static final String CONFIG_MAX_RETRIES = "search.max.retries";

    private final Logger logger = LoggerFactory.getLogger(SearchWorker.class);

    private final BlockingQueue<Search> queue;

    private final int maxRetires;

    public SearchWorker(BlockingQueue<Search> queue) {
        this.queue = queue;

        var configValue = ConfigLoader.getInstance().getConfigAsInteger(CONFIG_MAX_RETRIES);
        maxRetires = configValue.orElse(0);
    }

    @Override
    public void run() {
        try {
            while (true) {
                Search search = queue.take();
                performFirstLevelSearch(search);
            }
        } catch (InterruptedException e) {
            logger.error("SearchWorker - ", e);
            Thread.currentThread().interrupt();
        }
    }

    private void performFirstLevelSearch(Search search) {
        search.setStatus(SearchStatus.active);

        int searchRetries = 1;

        while (searchRetries < maxRetires) {
            try {
                final HttpRequest request = new HttpRequest(Main.BASE_URL);
                final HttpResponse response = request.get();

                if (response.isSuccessful()) {
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
                    break;
                } else if (response.isServerError()) {
                    searchRetries++;
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                break;
            }  catch (RuntimeException e) {
                logger.error("SearchWorker - request url: " + Main.BASE_URL, e);
                searchRetries++;
            }
        }

        search.setStatus(SearchStatus.done);
    }

}
