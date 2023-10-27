package com.axreng.backend;

import com.google.gson.Gson;

import java.util.Map;
import java.util.Optional;

import static spark.Spark.get;
import static spark.Spark.post;

public class Main {
    public static void main(String[] args) {
        get("/crawl/:id", (req, res) ->
                "GET /crawl/" + req.params("id")

        );
        post("/crawl", (req, res) -> {
                    Optional<String> keyword = getKeyword(req.body());
                    keyword.ifPresent(System.out::println);
//                    HttpRequest httpRequest = new HttpRequest("http://hiring.axreng.com/");
//                    HttpResponse resp = httpRequest.get();
//                    resp.getContent().forEach(System.out::println);

                    return "POST /crawl" + System.lineSeparator() + req.body();
                }
        );
    }

    private static Optional<String> getKeyword(String requestBody) {
        Map<?, ?> map = new Gson().fromJson(requestBody, Map.class);
        if (Optional.ofNullable(map.get("keyword")).isPresent()) {
            return String.valueOf(map.get("keyword")).describeConstable();
        }
        return Optional.empty();
    }
}
