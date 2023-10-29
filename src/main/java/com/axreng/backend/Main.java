package com.axreng.backend;

import com.axreng.backend.rest.RestErrorMessages;
import com.axreng.backend.rest.RestResponse;
import com.axreng.backend.util.KeywordUtils;
import com.google.gson.Gson;

import java.net.HttpURLConnection;
import java.util.Optional;

import static spark.Spark.get;
import static spark.Spark.post;

public class Main {
    public static void main(String[] args) {
        get("/crawl/:id", (req, res) ->
                "GET /crawl/" + req.params("id")

        );
        post("/crawl", (req, res) -> {
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

                    keyword.ifPresent(System.out::println);
//                    HttpRequest httpRequest = new HttpRequest("http://hiring.axreng.com/");
//                    HttpResponse resp = httpRequest.get();
//                    resp.getContent().forEach(System.out::println);

                    return "POST /crawl" + System.lineSeparator() + req.body();
                }
        );
    }

}
