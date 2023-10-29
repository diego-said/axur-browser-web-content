package com.axreng.backend.rest;

public class RestResponse {

    private final int status;
    private final String message;

    public RestResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public String toString() {
        return "RestResponse{" +
                "status=" + status +
                ", message='" + message + '\'' +
                '}';
    }

}
