package com.hackorama.mcore.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Request {

    private String body;
    private Map<String, List<String>> headers = new HashMap<>();
    private Map<String, String> pathParams = new HashMap<>();
    private Map<String, List<String>> queryParams = new HashMap<>();

    public Request() {
    }

    public Request(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public Map<String, String> getPathParams() {
        return pathParams;
    }

    public Map<String, List<String>> getQueryParams() {
        return queryParams;
    }

    public Request setBody(String body) {
        this.body = body;
        return this;
    }

    public Request setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
        return this;
    }

    public Request setPathParams(Map<String, String> params) {
        this.pathParams = params;
        return this;
    }

    public Request setQueryParams(Map<String, List<String>> queryParams) {
        this.queryParams = queryParams;
        return this;
    }

}
