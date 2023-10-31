package com.axreng.backend.search.multithreading;

import com.axreng.backend.config.ConfigLoader;
import com.axreng.backend.search.entities.Search;
import com.axreng.backend.search.entities.SearchStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SearchRequestProcessor implements SearchPerformable, Runnable {

    private static final Logger logger = LoggerFactory.getLogger(SearchRequestProcessor.class);

    private static final String CONFIG_MAX_RETRIES = "search.max.retries";
    private static final String CONFIG_URLS_MAX_SIZE = "search.urls.max.size";

    private final BlockingQueue<String> linksQueue;

    private final Search search;

    private final AtomicInteger linksToBeSearched;

    private final Set<String> searchedLinks;

    private final int maxRetires;

    private final int urlsMaxSize;

    public SearchRequestProcessor(BlockingQueue<String> linksQueue, Search search, AtomicInteger linksToBeSearched, Set<String> searchedLinks) {
        this.linksQueue = linksQueue;
        this.search = search;
        this.linksToBeSearched = linksToBeSearched;
        this.searchedLinks = searchedLinks;

        var configValue = ConfigLoader.getInstance().getConfigAsInteger(CONFIG_MAX_RETRIES);
        maxRetires = configValue.orElse(0);

        configValue = ConfigLoader.getInstance().getConfigAsInteger(CONFIG_URLS_MAX_SIZE);
        urlsMaxSize = configValue.orElse(0);
    }

    @Override
    public void run() {
        try {
            while (true) {
                String link = linksQueue.poll(1, TimeUnit.MINUTES);

                if (link != null) {
                    if (urlsMaxSize > 0 && search.getUrls().size() >= urlsMaxSize) {
                        search.setStatus(SearchStatus.done);
                        logger.info("SearchRequestProcessor - done 1");
                        break;
                    }

                    if (!searchedLinks.contains(link)) {
                        searchedLinks.add(link);

                        Set<String> linksSet = perform(search, link);
                        linksSet.stream()
                                .filter(s -> !searchedLinks.contains(s))
                                .forEach(s -> {
                                    try {
                                        linksToBeSearched.getAndIncrement();
                                        linksQueue.put(s);
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }
                                });
                    }
                } else {
                    if (linksToBeSearched.get() >= searchedLinks.size()) {
                        search.setStatus(SearchStatus.done);
                        logger.info("SearchRequestProcessor - done 2");
                        logger.info("linksToBeSearched: " + linksToBeSearched.get());
                        logger.info("searchedLinks: " + searchedLinks.size());
                        break;
                    } else {
                        logger.info("SearchRequestProcessor - deu ruim");
                    }
                }
            }
        } catch (InterruptedException e) {
            logger.error("SearchRequestProcessor - ", e);
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public int getMaxRetires() {
        return maxRetires;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

}
