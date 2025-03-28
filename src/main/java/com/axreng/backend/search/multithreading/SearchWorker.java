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

import static com.axreng.backend.config.ConfigNames.MAX_RETRIES;
import static com.axreng.backend.config.ConfigNames.REQUEST_PROCESSORS;
import static com.axreng.backend.config.ConfigNames.REQUEST_PROCESSOR_LINKS_QUEUE_SIZE;

public class SearchWorker implements SearchPerformable, Runnable {

    private final Logger logger = LoggerFactory.getLogger(SearchWorker.class);

    private final BlockingQueue<Search> queue;

    private final int maxRetires;

    private final int requestProcessors;

    private final int requestProcessorQueueSize;

    public SearchWorker(BlockingQueue<Search> queue) {
        this.queue = queue;

        var configValue = ConfigLoader.getInstance().getConfigAsInteger(MAX_RETRIES);
        maxRetires = configValue.orElse(0);

        configValue = ConfigLoader.getInstance().getConfigAsInteger(REQUEST_PROCESSORS);
        requestProcessors = configValue.orElse(1);

        configValue = ConfigLoader.getInstance().getConfigAsInteger(REQUEST_PROCESSOR_LINKS_QUEUE_SIZE);
        requestProcessorQueueSize = configValue.orElse(100000);
    }

    @Override
    public void run() {
        try {
            while (true) {
                Search search = queue.take();

                search.setStatus(SearchStatus.active);

                logger.info(String.format(
                        "SearchWorker - id: %s keyword: %s",
                        search.getId(),
                        search.getKeyword()
                ));

                Set<String> linkSet = perform(search, Main.BASE_URL);
                if (!linkSet.isEmpty()) {
                    final BlockingQueue<String> linksQueue = new ArrayBlockingQueue<>(requestProcessorQueueSize);
                    final Set<String> searchedLinks = Collections.synchronizedSet(new HashSet<>());

                    createProcessors(linksQueue, search, searchedLinks);

                    for (String link : linkSet) {
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

    private void createProcessors(BlockingQueue<String> linksQueue, Search search, Set<String> searchedLinks) {
        for (int i = 0; i < requestProcessors; i++) {
            SearchRequestProcessor processor = new SearchRequestProcessor(linksQueue, search, searchedLinks);
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
