package com.axreng.backend;

import com.axreng.backend.config.ConfigLoader;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        get("/crawl/:id", (req, res) ->
                "GET /crawl/" + req.params("id"));
        post("/crawl", (req, res) ->
                "POST /crawl" + System.lineSeparator() + req.body());
        ConfigLoader configLoader = ConfigLoader.getInstance();
        var test = configLoader.getConfigAsString("test");
        var not_found = configLoader.getConfigAsString("not_found");
        System.out.println(test.orElse("not_found"));
        System.out.println(not_found.orElse("not_found"));
    }
}
