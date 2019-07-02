package com.hackorama.mcore.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

public class Request {

    private String body;
    private Map<String, List<Cookie>> cookies = new HashMap<>();
    private Map<String, List<String>> headers = new HashMap<>();
    private Map<String, String> pathParams = new HashMap<>();
    private Map<String, List<String>> queryParams = new HashMap<>();
    HttpSession session;

    public Request() {
    }

    public Request(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public Map<String, List<Cookie>> getCookies() {
        return cookies;
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

    public HttpSession getSession() {
        return session;
    }

    public Request setBody(String body) {
        this.body = body;
        return this;
    }

    public Request setCookies(Map<String, List<Cookie>> cookies) {
        this.cookies = cookies;
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

    public Request setSession(HttpSession session) {
        this.session = session;
        return this;
    }

}
