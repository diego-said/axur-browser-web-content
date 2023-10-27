package com.axreng.backend;

import com.axreng.backend.net.HttpRequest;
import com.axreng.backend.net.HttpResponse;

import static spark.Spark.get;
import static spark.Spark.post;

public class Main {
    public static void main(String[] args) {
        get("/crawl/:id", (req, res) -> {
            HttpRequest httpRequest = new HttpRequest("http://hiring.axreng.com/");
            HttpResponse resp = httpRequest.get();
            resp.getContent().forEach(System.out::println);
            return "GET /crawl/" + req.params("id");
        }
        );
        post("/crawl", (req, res) ->
                "POST /crawl" + System.lineSeparator() + req.body());
    }
}
