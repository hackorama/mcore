package m.core.http;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

public class Response extends RequestResponse {

    private int status = HttpURLConnection.HTTP_OK;

    public Response() {
    }

    public Response(String body) {
        super(body);
    }

    public Response(String body, int status) {
        this(body);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public Response setBody(String body) {
        super.setBody(body);
        return this;
    }

    public Response setCookie(Cookie cookie) {
        return setCookie(cookie.getName(), cookie);
    }

    public Response setCookie(String name, Cookie cookie) {
        super.setCookie(name, cookie);
        return this;
    }

    public Response setCookies(Map<String, List<Cookie>> cookies) {
        super.setCookies(cookies);
        return this;
    }

    public Response setCookies(String name, List<Cookie> cookies) {
        super.setCookies(name, cookies);
        return this;
    }

    public Response setHeader(String name, String value) {
        super.setHeader(name, value);
        return this;
    }

    public Response setHeaders(Map<String, List<String>> headers) {
        super.setHeaders(headers);
        return this;
    }

    public Response setHeaders(String name, List<String> headers) {
        super.setHeaders(name, headers);
        return this;
    }

    public Response setStatus(int status) {
        this.status = status;
        return this;
    }

}
