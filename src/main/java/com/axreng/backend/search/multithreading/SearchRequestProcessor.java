package com.axreng.backend.search.multithreading;

import com.axreng.backend.config.ConfigLoader;
import com.axreng.backend.search.entities.Search;
import com.axreng.backend.search.entities.SearchStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static com.axreng.backend.config.ConfigNames.MAX_IDLE_TIME;
import static com.axreng.backend.config.ConfigNames.MAX_RETRIES;
import static com.axreng.backend.config.ConfigNames.URLS_MAX_SIZE;

public class SearchRequestProcessor implements SearchPerformable, Runnable {

    private static final Logger logger = LoggerFactory.getLogger(SearchRequestProcessor.class);

    private final BlockingQueue<String> linksQueue;

    private final Search search;

    private final Set<String> searchedLinks;

    private final int maxRetires;

    private final int maxIdleTime;

    private final int urlsMaxSize;

    public SearchRequestProcessor(BlockingQueue<String> linksQueue, Search search, Set<String> searchedLinks) {
        this.linksQueue = linksQueue;
        this.search = search;
        this.searchedLinks = searchedLinks;

        var configValue = ConfigLoader.getInstance().getConfigAsInteger(MAX_RETRIES);
        maxRetires = configValue.orElse(0);

        configValue = ConfigLoader.getInstance().getConfigAsInteger(MAX_IDLE_TIME);
        maxIdleTime = configValue.orElse(0);

        configValue = ConfigLoader.getInstance().getConfigAsInteger(URLS_MAX_SIZE);
        urlsMaxSize = configValue.orElse(0);
    }

    @Override
    public void run() {
        try {
            while (true) {
                String link = linksQueue.poll(maxIdleTime, TimeUnit.MILLISECONDS);

                if (link != null) {
                    if (urlsMaxSize > 0 && search.getUrls().size() >= urlsMaxSize) {
                        search.setStatus(SearchStatus.done);
                        logger.info(String.format(
                                "SearchRequestProcessor - id: %s keyword: %s - done",
                                search.getId(),
                                search.getKeyword()
                        ));
                        break;
                    }

                    if (!searchedLinks.contains(link)) {
                        searchedLinks.add(link);

                        Set<String> linksSet = perform(search, link);
                        linksSet.stream()
                                .filter(s -> !searchedLinks.contains(s))
                                .forEach(s -> {
                                    try {
                                        linksQueue.put(s);
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }
                                });
                    }
                } else {
                    search.setStatus(SearchStatus.done);
                    logger.info(String.format(
                            "SearchRequestProcessor - id: %s keyword: %s - done",
                            search.getId(),
                            search.getKeyword()
                    ));
                    break;
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
