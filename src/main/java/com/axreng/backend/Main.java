package com.axreng.backend;

import com.axreng.backend.rest.RestErrorMessages;
import com.axreng.backend.rest.RestResponse;
import com.axreng.backend.search.entities.Search;
import com.axreng.backend.search.services.SearchService;
import com.axreng.backend.util.KeywordUtils;
import com.axreng.backend.util.SearchUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.HttpURLConnection;
import java.util.Optional;

import static spark.Spark.get;
import static spark.Spark.post;

public class Main {

    private final static String BASE_URL = "http://hiring.axreng.com/"; //TODO: should be a env var

    public static void main(String[] args) {
        get("/crawl/:id", (req, res) -> {
                    res.header("Content-Type", "application/json");
                    final String searchId = req.params("id");
                    if (!SearchUtils.isSearchIdValid(searchId)) {
                        res.status(HttpURLConnection.HTTP_BAD_REQUEST);
                        return new Gson().toJson(
                                new RestResponse(HttpURLConnection.HTTP_BAD_REQUEST, RestErrorMessages.INVALID_SEARCH_ID));
                    }

                    Optional<Search> search = SearchService.getInstance().getSearchById(searchId);
                    if (search.isPresent()) {
                        res.status(HttpURLConnection.HTTP_OK);
                        return new GsonBuilder()
                                .excludeFieldsWithoutExposeAnnotation()
                                .create()
                                .toJson(search.get());
                    } else {
                        res.status(HttpURLConnection.HTTP_NOT_FOUND);
                        return new Gson().toJson(
                                new RestResponse(HttpURLConnection.HTTP_NOT_FOUND,
                                        String.format(RestErrorMessages.SEARCH_NOT_FOUND, searchId)));
                    }
                }
        );

        post("/crawl", (req, res) -> {
                    res.header("Content-Type", "application/json");
                    if (req.body().isBlank()) {
                        res.status(HttpURLConnection.HTTP_BAD_REQUEST);
                        return new Gson().toJson(
                                new RestResponse(HttpURLConnection.HTTP_BAD_REQUEST, RestErrorMessages.EMPTY_KEYWORD));
                    }

                    Optional<String> keyword = KeywordUtils.getKeyword(req.body());
                    if (keyword.isPresent()) {
                        boolean validKeyword = KeywordUtils.isKeywordValid(keyword.get());
                        if (!validKeyword) {
                            res.status(HttpURLConnection.HTTP_BAD_REQUEST);
                            return new Gson().toJson(
                                    new RestResponse(HttpURLConnection.HTTP_BAD_REQUEST, RestErrorMessages.INVALID_KEYWORD));
                        }
                    } else {
                        res.status(HttpURLConnection.HTTP_BAD_REQUEST);
                        return new Gson().toJson(
                                new RestResponse(HttpURLConnection.HTTP_BAD_REQUEST, RestErrorMessages.EMPTY_KEYWORD));
                    }

                    Search search = SearchService.getInstance().createSearch(keyword.get());

                    res.status(HttpURLConnection.HTTP_OK);
                    return search.getIdAsJson();
                }
        );
    }

}
