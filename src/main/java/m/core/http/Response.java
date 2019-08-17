package m.core.http;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

/**
 * And HTTP server response.
 */
public class Response extends RequestResponse {

    private int status = HttpURLConnection.HTTP_OK;

    /**
     * Constructs a response.
     */
    public Response() {
    }

    /**
     * Constructs a response with the specified body.
     *
     * @param body the response body
     */
    public Response(String body) {
        super(body);
    }

    /**
     * Constructs a response with the specified body and status
     *
     * @param body   the body
     * @param status the status
     */
    public Response(String body, int status) {
        this(body);
        this.status = status;
    }

    /**
     * Get the status of the response.
     *
     * @return the status of this response
     */
    public int getStatus() {
        return status;
    }

    /**
     * Set the body of this response.
     *
     * @return this response
     */
    @Override
    public Response setBody(String body) {
        super.setBody(body);
        return this;
    }

    /**
     * Set a cookie of this response.
     *
     * @param cookie the cookie
     * @return this response
     */
    @Override
    public Response setCookie(Cookie cookie) {
        return setCookie(cookie.getName(), cookie);
    }

    /**
     * Set a cookie of this response.
     *
     * @param name   the cookie name
     * @param cookie the cookie
     * @return this response
     */
    @Override
    public Response setCookie(String name, Cookie cookie) {
        super.setCookie(name, cookie);
        return this;
    }

    /**
     * Set cookies of this response.
     *
     * @param cookies the {@code Map} of named cookie {@code Lists}
     * @return this response
     */
    @Override
    public Response setCookies(Map<String, List<Cookie>> cookies) {
        super.setCookies(cookies);
        return this;
    }

    /**
     * Set cookies of this response.
     *
     * @param name    the cookie {@code List} name
     * @param cookies the cookie {@code List}
     * @return this response
     */
    @Override
    public Response setCookies(String name, List<Cookie> cookies) {
        super.setCookies(name, cookies);
        return this;
    }

    /**
     * Set a header of this response.
     *
     * @param name  the header name
     * @param value the header value
     * @return this response
     */
    @Override
    public Response setHeader(String name, String value) {
        super.setHeader(name, value);
        return this;
    }

    /**
     * Set headers of this response.
     *
     * @param headers the {@code Map} of named header {@code Lists}
     * @return this response
     */
    @Override
    public Response setHeaders(Map<String, List<String>> headers) {
        super.setHeaders(headers);
        return this;
    }

    /**
     * Set headers of this response.
     *
     * @param name    the header {@code List} name
     * @param headers the header {@code List}
     * @return this response
     */
    @Override
    public Response setHeaders(String name, List<String> headers) {
        super.setHeaders(name, headers);
        return this;
    }

    /**
     * Set the status of the response.
     *
     * @param status the status
     * @return this response
     */
    public Response setStatus(int status) {
        this.status = status;
        return this;
    }

}
