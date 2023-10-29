package com.axreng.backend.search.services;

import com.axreng.backend.search.entities.Search;
import com.axreng.backend.search.entities.SearchStatus;
import com.axreng.backend.util.SearchUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public class SearchServiceTest {

    private final SearchService searchService = SearchService.getInstance();

    @Test
    void testCreateSearch() {
        Search search = searchService.createSearch("security");

        Assertions.assertNotNull(search);
        Assertions.assertEquals("security", search.getKeyword());
        Assertions.assertEquals(0, search.getUrls().size());
        Assertions.assertEquals(SearchStatus.create, search.getStatus());
    }

    @Test
    void testGetSearchById() {
        Search search = searchService.createSearch("security");

        Optional<Search> foundSearch = searchService.getSearchById(search.getId());

        Assertions.assertTrue(foundSearch.isPresent());
        Assertions.assertEquals(search.getKeyword(), foundSearch.get().getKeyword());
        Assertions.assertEquals(search.getStatus(), foundSearch.get().getStatus());
        Assertions.assertEquals(foundSearch.get(), search);
    }

    @Test
    void testGetSearchByIdWithRandomId() {
        String searchId = SearchUtils.randomId();

        Optional<Search> foundSearch = searchService.getSearchById(searchId);

        Assertions.assertFalse(foundSearch.isPresent());
    }

}
