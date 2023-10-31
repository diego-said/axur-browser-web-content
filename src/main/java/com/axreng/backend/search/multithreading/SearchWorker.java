package com.axreng.backend.search.multithreading;

import com.axreng.backend.Main;
import com.axreng.backend.config.ConfigLoader;
import com.axreng.backend.search.entities.Search;
import com.axreng.backend.search.entities.SearchStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class SearchWorker implements SearchPerformable, Runnable {

    private static final String CONFIG_MAX_RETRIES = "search.max.retries";
    private static final String CONFIG_REQUEST_PROCESSORS = "search.request.processors";

    private final Logger logger = LoggerFactory.getLogger(SearchWorker.class);

    private final BlockingQueue<Search> queue;

    private final int maxRetires;

    private final int requestProcessors;

    public SearchWorker(BlockingQueue<Search> queue) {
        this.queue = queue;

        var configValue = ConfigLoader.getInstance().getConfigAsInteger(CONFIG_MAX_RETRIES);
        maxRetires = configValue.orElse(0);

        configValue = ConfigLoader.getInstance().getConfigAsInteger(CONFIG_REQUEST_PROCESSORS);
        requestProcessors = configValue.orElse(0);
    }

    @Override
    public void run() {
        try {
            while (true) {
                Search search = queue.take();

                search.setStatus(SearchStatus.active);

                Set<String> linkSet = perform(search, Main.BASE_URL);
                if(!linkSet.isEmpty()) {
                    final BlockingQueue<String> linksQueue = new ArrayBlockingQueue<>(100000);
                    final Set<String> searchedLinks = Collections.synchronizedSet(new HashSet<>());
                    final AtomicInteger linksToBeSearched = new AtomicInteger(linkSet.size());

                    createProcessors(linksQueue, search, linksToBeSearched,searchedLinks);

                    for(String link : linkSet) {
                        try {
                            linksQueue.put(link);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } else {
                    search.setStatus(SearchStatus.done);
                }
            }
        } catch (InterruptedException e) {
            logger.error("SearchWorker - ", e);
            Thread.currentThread().interrupt();
        }
    }

    private void createProcessors(BlockingQueue<String> linksQueue, Search search, AtomicInteger linksToBeSearched, Set<String> searchedLinks) {
        for(int i = 0; i < requestProcessors; i++) {
            SearchRequestProcessor processor = new SearchRequestProcessor(linksQueue, search, linksToBeSearched, searchedLinks);
            new Thread(processor).start();
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
