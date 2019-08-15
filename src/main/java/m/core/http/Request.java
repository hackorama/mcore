package m.core.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

public class Request {

    private String body;
    private Map<String, List<Cookie>> cookies = new HashMap<>();
    private Map<String, List<String>> headers = new HashMap<>();
    private Map<String, String> pathParams = new HashMap<>();
    private Map<String, List<String>> queryParams = new HashMap<>();
    Session session;

    public Request() {
    }

    public Request(String body) {
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public Cookie getCookie(String name) {
        List<Cookie> cookies = getCookies(name);
        return cookies.isEmpty() ? null : cookies.get(0);
    }

    public Map<String, List<Cookie>> getCookies() {
        return cookies;
    }

    public List<Cookie> getCookies(String name) {
        return cookies.containsKey(name) ? cookies.get(name) : new ArrayList<>();
    }

    public String getHeader(String name) {
        List<String> headers = getHeaders(name);
        return headers.isEmpty() ? null : headers.get(0);
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public List<String> getHeaders(String name) {
        return headers.containsKey(name) ? headers.get(name) : new ArrayList<>();
    }

    public String getParam(String name) {
        return getParam(name, null);
    }

    public String getParam(String name, String defaultValue) {
        String value = getPathParam(name);
        return value == null ? getQueryParam(name, defaultValue) : value;
    }

    public Map<String, List<String>> getParams() {
        Map<String, List<String>> params = new HashMap<>();
        params.putAll(queryParams);
        pathParams.forEach((k, v) -> { // path params takes precedence
            if (!params.containsKey(k)) {
                params.put(k, new ArrayList<String>()); // TODO Init and insert
                params.get(k).add(v);
            } else {
                params.get(k).set(0, v); // path param inserted as first for multi values
            }
        });
        return params;
    }

    public Map<String, String> getParamsFirstValue() {
        Map<String, String> params = new HashMap<>();
        queryParams.forEach((k, v) -> {
            if (v != null) {
                params.put(k, v.get(0));
            } else {
                params.put(k, null);
            }
        });
        pathParams.forEach((k, v) -> { // path params takes precedence
            params.put(k, v);
        });
        return params;
    }

    public String getPathParam(String name) {
        return pathParams.get(name);
    }

    public String getPathParam(String name, String defaultValue) {
        return pathParams.getOrDefault(name, defaultValue);
    }

    public Map<String, String> getPathParams() {
        return pathParams;
    }

    public String getQueryParam(String name) {
        return getQueryParam(name, null);
    }

    public String getQueryParam(String name, String defaultValue) {
        if (queryParams.containsKey(name) && queryParams.get(name) != null && !queryParams.get(name).isEmpty()) {
            return queryParams.get(name).get(0);
        } else {
            return defaultValue;
        }
    }

    public Map<String, List<String>> getQueryParams() {
        return queryParams;
    }

    public List<String> getQueryParams(String name) {
        if (queryParams.containsKey(name) && !queryParams.get(name).isEmpty()) {
            return queryParams.get(name);
        }
        return new ArrayList<>();
    }

    public Session getSession() {
        return session;
    }

    public Request setBody(String body) {
        this.body = body;
        return this;
    }

    public Request setCookie(String name, Cookie cookie) {
        if (cookies.containsKey(name)) {
            getCookies(name).add(cookie);
        } else {
            List<Cookie> values = new ArrayList<>();
            values.add(cookie);
            setCookies(name, (values));
        }
        return this;
    }

    public Request setCookies(Map<String, List<Cookie>> cookies) {
        this.cookies = cookies;
        return this;
    }

    public Request setCookies(String name, List<Cookie> cookies) {
        this.cookies.put(name, cookies);
        return this;
    }

    public Request setHeader(String name, String value) {
        if (headers.containsKey(name)) {
            getHeaders(name).add(value);
        } else {
            List<String> values = new ArrayList<>();
            values.add(value);
            setHeaders(name, (values));
        }
        headers.getOrDefault(name, new ArrayList<>()).add(value);
        return this;
    }

    public Request setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
        return this;
    }

    public Request setHeaders(String name, List<String> values) {
        headers.put(name, values);
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

    public Request setSession(Session session) {
        this.session = session;
        return this;
    }

}
