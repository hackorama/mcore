package com.hackorama.mcore.common;


import java.net.HttpURLConnection;

public class Response {

    private int status = HttpURLConnection.HTTP_OK;
    private String body;

    public Response() {
    }

    public Response(String body) {
        this.body = body;
    }

    public Response(String body, int status) {
        this(body);
        this.status = status;
    }

    public String getBody() {
        return body;
    }

    public int getStatus() {
        return status;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
