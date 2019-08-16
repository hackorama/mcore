package m.core.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

public class Request extends RequestResponse {

    private Map<String, String> pathParams = new HashMap<>();
    private Map<String, List<String>> queryParams = new HashMap<>();
    Session session;

    public Request() {
    }

    public Request(String body) {
        super(body);
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
        super.setBody(body);
        return this;
    }

    public Request setCookie(Cookie cookie) {
        return setCookie(cookie.getName(), cookie);
    }

    public Request setCookie(String name, Cookie cookie) {
        super.setCookie(name, cookie);
        return this;
    }

    public Request setCookies(Map<String, List<Cookie>> cookies) {
        super.setCookies(cookies);
        return this;
    }

    public Request setCookies(String name, List<Cookie> cookies) {
        super.setCookies(name, cookies);
        return this;
    }

    public Request setHeader(String name, String value) {
        super.setHeader(name, value);
        return this;
    }

    public Request setHeaders(Map<String, List<String>> headers) {
        super.setHeaders(headers);
        return this;
    }

    public Request setHeaders(String name, List<String> headers) {
        super.setHeaders(name, headers);
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
