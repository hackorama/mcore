package com.hackorama.mcore.common;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

public class Response {

    private String body;
    private Map<String, Cookie> cookies = new HashMap<>();
    private Map<String, List<String>> headers = new HashMap<>();
    private int status = HttpURLConnection.HTTP_OK;

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

    public Map<String, Cookie> getCookies() {
        return cookies;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public int getStatus() {
        return status;
    }

    public Response setBody(String body) {
        this.body = body;
        return this;
    }

    public Response setCookies(Map<String, Cookie> cookies) {
        this.cookies = cookies;
        return this;
    }

    public Response setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
        return this;
    }

    public Response setStatus(int status) {
        this.status = status;
        return this;
    }

    public Response setCookie(Cookie cookie) {
        cookies.put(cookie.getName(), cookie);
        return this;
    }

}
