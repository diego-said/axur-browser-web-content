package com.axreng.backend.search.multithreading;

import com.axreng.backend.config.ConfigLoader;
import com.axreng.backend.search.entities.Search;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static com.axreng.backend.config.ConfigNames.NUMBER_OF_WORKERS;
import static com.axreng.backend.config.ConfigNames.QUEUE_SIZE;

public class SearchManager {

    private static SearchManager INSTANCE;

    private final BlockingQueue<Search> searchQueue;

    public SearchManager() {
        ConfigLoader configLoader = ConfigLoader.getInstance();
        var configValue = configLoader.getConfigAsInteger(QUEUE_SIZE);
        int queueSize = configValue.orElse(0);

        this.searchQueue = new ArrayBlockingQueue<>(queueSize);

        configValue = configLoader.getConfigAsInteger(NUMBER_OF_WORKERS);
        int numberOfWorkers = configValue.orElse(0);
        for (int i = 0; i < numberOfWorkers; i++) {
            createWorker();
        }
    }

    public synchronized static SearchManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SearchManager();
        }
        return INSTANCE;
    }

    public void addSearch(Search search) {
        searchQueue.add(search);
    }

    private void createWorker() {
        SearchWorker searchWorker = new SearchWorker(searchQueue);
        new Thread(searchWorker).start();
    }

}