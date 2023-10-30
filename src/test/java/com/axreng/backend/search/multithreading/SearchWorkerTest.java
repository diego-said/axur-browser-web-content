package com.axreng.backend.search.multithreading;

import com.axreng.backend.search.entities.Search;
import com.axreng.backend.search.entities.SearchStatus;
import com.axreng.backend.util.SearchUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class SearchWorkerTest {

    @Test
    void testRun() throws InterruptedException {
        final BlockingQueue<Search> searchQueue = new ArrayBlockingQueue<>(1);

        Search search = new Search(SearchUtils.randomId(), "linux");

        SearchWorker searchWorker = new SearchWorker(searchQueue);
        new Thread(searchWorker).start();

        searchQueue.add(search);

        while (search.getStatus() != SearchStatus.done) {
            TimeUnit.MILLISECONDS.sleep(500);
        }

        Assertions.assertEquals(1, search.getUrls().size());
    }

}
