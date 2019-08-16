package m.core.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

public class RequestResponse {

    protected String body;
    protected Map<String, List<Cookie>> cookies = new HashMap<>();
    protected Map<String, List<String>> headers = new HashMap<>();

    public RequestResponse() {
    }

    public RequestResponse(String body) {
        this.body = body;
    }

    public RequestResponse(String body, int status) {
        this(body);
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

    public RequestResponse setBody(String body) {
        this.body = body;
        return this;
    }

    public RequestResponse setCookie(Cookie cookie) {
        return setCookie(cookie.getName(), cookie);
    }

    public RequestResponse setCookie(String name, Cookie cookie) {
        List<Cookie> values = cookies.getOrDefault(name, new ArrayList<>());
        values.add(cookie);
        setCookies(name, values);
        return this;
    }

    public RequestResponse setCookies(Map<String, List<Cookie>> cookies) {
        this.cookies = cookies;
        return this;
    }

    public RequestResponse setCookies(String name, List<Cookie> cookies) {
        this.cookies.put(name, cookies);
        return this;
    }

    public RequestResponse setHeader(String name, String value) {
        List<String> values = headers.getOrDefault(name, new ArrayList<>());
        values.add(value);
        setHeaders(name, values);
        return this;
    }

    public RequestResponse setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
        return this;
    }

    public RequestResponse setHeaders(String name, List<String> headers) {
        this.headers.put(name, headers);
        return this;
    }

}
