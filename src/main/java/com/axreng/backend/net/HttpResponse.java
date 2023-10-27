package com.axreng.backend.net;

import java.util.List;

public class HttpResponse {

    private final int status;

    private final List<String> content;

    public HttpResponse(int status, List<String> content) {
        this.status = status;
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public List<String> getContent() {
        return content;
    }

}
