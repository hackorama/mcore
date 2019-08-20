package m.core.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

/**
 * An HTTP client request.
 */
public class Request extends RequestResponse {

    private Map<String, String> pathParams = new HashMap<>();
    private Map<String, List<String>> queryParams = new HashMap<>();
    Session session;

    /**
     * Constructs a request.
     */
    public Request() {
    }

    /**
     * Constructs a request with the specified body.
     *
     * @param body the request body
     */
    public Request(String body) {
        super(body);
    }

    /**
     * Returns the specified parameter value of this request,
     *
     * @param name the parameter name
     * @return the parameter value, or null
     */
    public String getParam(String name) {
        return getParam(name, null);
    }

    /**
     * Returns the specified parameter value of this request.
     *
     * @param name         the parameter name.
     * @param defaultValue the default value
     * @return the parameter value or {@code defaultValue} if value is null
     */
    public String getParam(String name, String defaultValue) {
        String value = getPathParam(name);
        return value == null ? getQueryParam(name, defaultValue) : value;
    }

    /**
     * Returns all the parameters of this request.
     *
     * @return the parameters as a {@code Map} of named {@code Lists}
     */
    public Map<String, List<String>> getParams() {
        Map<String, List<String>> params = new HashMap<>();
        params.putAll(queryParams);
        pathParams.forEach((k, v) -> { // Path params takes precedence
            if (!params.containsKey(k)) {
                params.put(k, new ArrayList<String>()); // TODO Init and insert
                params.get(k).add(v);
            } else {
                params.get(k).set(0, v); // Path param inserted as first for multi values
            }
        });
        return params;
    }

    /**
     * Returns a single value for each parameters of this request.
     * <p>
     * For parameters with multiple values for a name, the first value is returned.
     *
     * @return the parameter {@code Map}
     */
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

    /**
     * Returns the specified path parameter value of this request.
     *
     * @param name the path parameter name
     * @return the path parameter value, or null
     */
    public String getPathParam(String name) {
        return pathParams.get(name);
    }

    /**
     * Returns the specified path parameter value of this request
     *
     * @param name         the path parameter name
     * @param defaultValue the default value
     * @return the path parameter value or {@code defaultValue} if value is null
     */
    public String getPathParam(String name, String defaultValue) {
        return pathParams.getOrDefault(name, defaultValue);
    }

    /**
     * Returns all the path parameters of this request.
     *
     * @return the path parameters as a {@code Map} of named {@code Lists}
     */
    public Map<String, String> getPathParams() {
        return pathParams;
    }

    /**
     * Returns the specified query parameter value of this request.
     *
     * @param name the query parameter name
     * @return the query parameter value, or null
     */
    public String getQueryParam(String name) {
        return getQueryParam(name, null);
    }

    /**
     * Returns the specified query parameter value of this request
     *
     * @param name         the query parameter name
     * @param defaultValue the default value
     * @return the query parameter value or {@code defaultValue} if value is null
     */
    public String getQueryParam(String name, String defaultValue) {
        if (queryParams.containsKey(name) && queryParams.get(name) != null && !queryParams.get(name).isEmpty()) {
            return queryParams.get(name).get(0);
        } else {
            return defaultValue;
        }
    }

    /**
     * Returns all the query parameters of this request.
     *
     * @return the query parameters as a {@code Map} of named {@code Lists}
     */
    public Map<String, List<String>> getQueryParams() {
        return queryParams;
    }

    /**
     * Returns all the specified query parameter values of this request
     *
     * @param name the query parameter name
     * @return the query parameter values {@code List}, could be empty.
     */
    public List<String> getQueryParams(String name) {
        if (queryParams.containsKey(name) && !queryParams.get(name).isEmpty()) {
            return queryParams.get(name);
        }
        return new ArrayList<>();
    }

    /**
     * Returns the session of the request.
     *
     * @return the session
     */
    public Session getSession() {
        return session;
    }

    /**
     * Sets the body of this request.
     *
     * @return this request
     */
    @Override
    public Request setBody(String body) {
        super.setBody(body);
        return this;
    }

    /**
     * Sets a cookie of this request.
     *
     * @param cookie the cookie
     * @return this request
     */
    @Override
    public Request setCookie(Cookie cookie) {
        return setCookie(cookie.getName(), cookie);
    }

    /**
     * Sets a cookie of this request.
     *
     * @param name   the cookie name
     * @param cookie the cookie
     * @return this request
     */
    @Override
    public Request setCookie(String name, Cookie cookie) {
        super.setCookie(name, cookie);
        return this;
    }

    /**
     * Sets cookies of this request.
     *
     * @param cookies the {@code Map} of named cookie {@code Lists}
     * @return this request
     */
    @Override
    public Request setCookies(Map<String, List<Cookie>> cookies) {
        super.setCookies(cookies);
        return this;
    }

    /**
     * Sets cookies of this request.
     *
     * @param name    the cookie {@code List} name
     * @param cookies the cookie {@code List}
     * @return this request
     */
    @Override
    public Request setCookies(String name, List<Cookie> cookies) {
        super.setCookies(name, cookies);
        return this;
    }

    /**
     * Sets a header of this request.
     *
     * @param name  the header name
     * @param value the header value
     * @return this request
     */
    @Override
    public Request setHeader(String name, String value) {
        super.setHeader(name, value);
        return this;
    }

    /**
     * Sets headers of this request.
     *
     * @param headers the {@code Map} of named header {@code Lists}
     * @return this request
     */
    @Override
    public Request setHeaders(Map<String, List<String>> headers) {
        super.setHeaders(headers);
        return this;
    }

    /**
     * Sets headers of this request.
     *
     * @param name    the header {@code List} name
     * @param headers the header {@code List}
     * @return this request
     */
    @Override
    public Request setHeaders(String name, List<String> headers) {
        super.setHeaders(name, headers);
        return this;
    }

    /**
     * Sets the path parameters of this request.
     *
     * @param params the path parameters {@code Map}
     * @return this request
     */
    public Request setPathParams(Map<String, String> params) {
        this.pathParams = params;
        return this;
    }

    /**
     * Sets the query parameters of this request.
     *
     * @param queryParams the {@code Map} of named query parameter {@code Lists}
     * @return this request
     */
    public Request setQueryParams(Map<String, List<String>> queryParams) {
        this.queryParams = queryParams;
        return this;
    }

    /**
     * Sets the session of the request.
     *
     * @param session the session
     * @return this request
     */
    public Request setSession(Session session) {
        this.session = session;
        return this;
    }

}
