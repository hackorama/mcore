package m.core.service;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.junit.Test;

import m.core.http.Request;
import m.core.http.Session;

public class RequestTest {

    @Test
    public void verifyRequestBody() {
        assertEquals("Verify default body", null, new Request().getBody());
        assertEquals("Verify body", "foo", new Request().setBody("foo").getBody());
        assertEquals("Verify empty body", "", new Request().setBody("").getBody());
        assertEquals("Verify null body", null, new Request().setBody(null).getBody());
    }

    @Test
    public void verifyRequestCookie() {
        assertTrue("Verify default empty cookies", new Request().getCookies().isEmpty());

        Map<String, List<Cookie>> cookies = new HashMap<>();
        assertTrue("Verify empty cookies", new Request().setCookies(cookies).getCookies().isEmpty());

        cookies = new HashMap<>();
        List<Cookie> values = new ArrayList<>();
        values.add(new Cookie("First", "First Value"));
        cookies.put("One", values);
        values = new ArrayList<>();
        values.add(new Cookie("MIDDLE", "MIDDLE VALUE"));
        cookies.put("TWO", values);
        values = new ArrayList<>();
        values.add(new Cookie("last", "last value"));
        cookies.put("three", values);
        assertEquals("Verify cookies", cookies.size(), new Request().setCookies(cookies).getCookies().size());
        assertEquals("Verify cookies", cookies.get("One"), new Request().setCookies(cookies).getCookies().get("One"));
        assertEquals("Verify cookies", cookies.get("TWO"), new Request().setCookies(cookies).getCookies().get("TWO"));
        assertEquals("Verify cookies", cookies.get("three"),
                new Request().setCookies(cookies).getCookies().get("three"));
        assertNotEquals("Verify cookies", cookies.get("One"),
                new Request().setCookies(cookies).getCookies().get("three"));
        Request request = new Request().setCookies(cookies);
        assertEquals("Verify cookies using value and list getters", request.getCookie("One"),
                request.getCookies().get("One").get(0));
        assertEquals("Verify cookies using value and list getters", request.getCookie("TWO"),
                request.getCookies().get("TWO").get(0));
        assertEquals("Verify cookies using value and list getters", request.getCookie("three"),
                request.getCookies().get("three").get(0));

        cookies = new HashMap<>();
        values = new ArrayList<>();
        values.add(new Cookie("empty", ""));
        cookies.put("empty", values);
        values = new ArrayList<>();
        values.add(null);
        cookies.put("null", values);
        assertEquals("Verify cookies empty and null values", cookies.size(),
                new Request().setCookies(cookies).getCookies().size());
        assertEquals("Verify cookies empty value", cookies.get("empty"),
                new Request().setCookies(cookies).getCookies().get("empty"));
        assertEquals("Verify cookies null values", cookies.get("null"),
                new Request().setCookies(cookies).getCookies().get("null"));
        assertEquals("Verify invaid cookies", null, new Request().setCookies(cookies).getCookies().get("invalid"));
        assertEquals("Verify invaid cookies", null, new Request().setCookies(cookies).getCookies().get(null));
    }

    @Test
    public void verifyRequestCookiesWithMultipleValues() {
        Map<String, List<Cookie>> cookies = new HashMap<>();
        assertTrue("Verify empty cookies", new Request().setCookies(cookies).getCookies().isEmpty());

        cookies = new HashMap<>();
        List<Cookie> values = new ArrayList<>();
        values.add(new Cookie("One", "First"));
        values.add(new Cookie("ONE", "MIDDLE"));
        values.add(new Cookie("one", "last"));
        cookies.put("One", values);
        values = new ArrayList<>();
        values.add(new Cookie("Two", "First"));
        values.add(new Cookie("TWO", "MIDDLE"));
        values.add(new Cookie("two", "last"));
        cookies.put("TWO", values);
        values = new ArrayList<>();
        values.add(new Cookie("Three", "First"));
        values.add(new Cookie("THREE", "MIDDLE"));
        values.add(new Cookie("three", "last"));
        cookies.put("three", values);
        assertEquals("Verify cookies with multiple values", cookies.size(),
                new Request().setCookies(cookies).getCookies().size());
        assertEquals("Verify cookies with multiple values", cookies.get("One").size(),
                new Request().setCookies(cookies).getCookies().get("One").size());
        assertEquals("Verify cookies with multiple values", cookies.get("TWO").size(),
                new Request().setCookies(cookies).getCookies().get("TWO").size());
        assertEquals("Verify cookies with multiple values", cookies.get("three").size(),
                new Request().setCookies(cookies).getCookies().get("three").size());
        assertEquals("Verify cookies with multiple values", cookies.get("One").get(0),
                new Request().setCookies(cookies).getCookies().get("One").get(0));
        assertEquals("Verify cookies with multiple values", cookies.get("TWO").get(1),
                new Request().setCookies(cookies).getCookies().get("TWO").get(1));
        assertEquals("Verify cookies with multiple values", cookies.get("three").get(2),
                new Request().setCookies(cookies).getCookies().get("three").get(2));
        Request request = new Request().setCookies(cookies);
        assertEquals("Verify cookies using single value getter will get first of the multiple values",
                request.getCookie("One"), request.getCookies().get("One").get(0));
        assertEquals("Verify cookies using single value getter will get first of the multiple value",
                request.getCookie("TWO"), request.getCookies().get("TWO").get(0));
        assertEquals("Verify cookies using single value getter will get first of the multiple values",
                request.getCookie("three"), request.getCookies().get("three").get(0));
    }

    @Test
    public void verifyRequestHeader() {
        assertTrue("Verify default empty headers", new Request().getHeaders().isEmpty());

        Map<String, List<String>> headers = new HashMap<>();
        assertTrue("Verify empty headers", new Request().setHeaders(headers).getHeaders().isEmpty());

        headers = new HashMap<>();
        List<String> values = new ArrayList<>();
        values.add("First");
        headers.put("One", values);
        values = new ArrayList<>();
        values.add("MIDDLE");
        headers.put("TWO", values);
        values = new ArrayList<>();
        values.add("Last");
        headers.put("three", values);
        assertEquals("Verify headers", headers.size(), new Request().setHeaders(headers).getHeaders().size());
        assertEquals("Verify headers", headers.get("One"), new Request().setHeaders(headers).getHeaders().get("One"));
        assertEquals("Verify headers", headers.get("TWO"), new Request().setHeaders(headers).getHeaders().get("TWO"));
        assertEquals("Verify headers", headers.get("three"),
                new Request().setHeaders(headers).getHeaders().get("three"));
        assertNotEquals("Verify headers", headers.get("One"),
                new Request().setHeaders(headers).getHeaders().get("three"));
        Request request = new Request().setHeaders(headers);
        assertEquals("Verify headers using value and list getters", request.getHeader("One"),
                request.getHeaders().get("One").get(0));
        assertEquals("Verify headers using value and list getters", request.getHeader("TWO"),
                request.getHeaders().get("TWO").get(0));
        assertEquals("Verify headers using value and list getters", request.getHeader("three"),
                request.getHeaders().get("three").get(0));

        headers = new HashMap<>();
        values = new ArrayList<>();
        values.add("");
        headers.put("empty", values);
        values = new ArrayList<>();
        values.add(null);
        headers.put("null", values);
        assertEquals("Verify headers empty and null values", headers.size(),
                new Request().setHeaders(headers).getHeaders().size());
        assertEquals("Verify headers empty value", headers.get("empty"),
                new Request().setHeaders(headers).getHeaders().get("empty"));
        assertEquals("Verify headers null values", headers.get("null"),
                new Request().setHeaders(headers).getHeaders().get("null"));
        assertEquals("Verify invaid headers", null, new Request().setHeaders(headers).getHeaders().get("invalid"));
        assertEquals("Verify invaid headers", null, new Request().setHeaders(headers).getHeaders().get(null));

    }

    @Test
    public void verifyRequestHeadersWithMultipleValues() {
        Map<String, List<String>> headers = new HashMap<>();
        assertTrue("Verify empty headers", new Request().setHeaders(headers).getHeaders().isEmpty());

        headers = new HashMap<>();
        List<String> values = new ArrayList<>();
        values.add("One First");
        values.add("ONE MIDDLE");
        values.add("one last");
        headers.put("One", values);
        values = new ArrayList<>();
        values.add("Two First");
        values.add("TWO MIDDLE");
        values.add("two last");
        headers.put("TWO", values);
        values = new ArrayList<>();
        values.add("Three First");
        values.add("THREE MIDDLE");
        values.add("three last");
        headers.put("three", values);
        assertEquals("Verify headers with multiple values", headers.size(),
                new Request().setHeaders(headers).getHeaders().size());
        assertEquals("Verify headers with multiple values", headers.get("One").size(),
                new Request().setHeaders(headers).getHeaders().get("One").size());
        assertEquals("Verify headers with multiple values", headers.get("TWO").size(),
                new Request().setHeaders(headers).getHeaders().get("TWO").size());
        assertEquals("Verify headers with multiple values", headers.get("three").size(),
                new Request().setHeaders(headers).getHeaders().get("three").size());
        assertEquals("Verify headers with multiple values", headers.get("One").get(0),
                new Request().setHeaders(headers).getHeaders().get("One").get(0));
        assertEquals("Verify headers with multiple values", headers.get("TWO").get(1),
                new Request().setHeaders(headers).getHeaders().get("TWO").get(1));
        assertEquals("Verify headers with multiple values", headers.get("three").get(2),
                new Request().setHeaders(headers).getHeaders().get("three").get(2));
        Request request = new Request().setHeaders(headers);
        assertEquals("Verify headers using single value getter will get first of the multiple values",
                request.getHeader("One"), request.getHeaders().get("One").get(0));
        assertEquals("Verify headers using single value getter will get first of the multiple value",
                request.getHeader("TWO"), request.getHeaders().get("TWO").get(0));
        assertEquals("Verify headers using single value getter will get first of the multiple values",
                request.getHeader("three"), request.getHeaders().get("three").get(0));
    }

    @Test
    public void verifyRequestParam() {
        assertTrue("Verify default empty params", new Request().getParams().isEmpty());
        assertTrue("Verify default empty params", new Request().getParamsFirstValue().isEmpty());

        Map<String, List<String>> queryParams = new HashMap<>();
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("same", "path value");
        pathParams.put("path one", "path one value");
        List<String> queryValues = new ArrayList<>();
        queryValues.add("query value");
        queryParams.put("same", queryValues);
        queryValues = new ArrayList<>();
        queryValues.add("query one value");
        queryParams.put("query one", queryValues);
        assertEquals("For same param name validate path param has precedence", "path value",
                new Request().setPathParams(pathParams).setQueryParams(queryParams).getParam("same"));
        assertEquals("Verify path param using param getter when both path and query params are set", "path one value",
                new Request().setPathParams(pathParams).setQueryParams(queryParams).getParam("path one"));
        assertEquals("Verify query param using param getter when both path and query params are set", "query one value",
                new Request().setPathParams(pathParams).setQueryParams(queryParams).getParam("query one"));
        pathParams.put("path empty", "");
        pathParams.put("path null", null);
        queryParams.put("query list null", null);
        queryValues = new ArrayList<>();
        queryValues.add("");
        queryParams.put("query empty", queryValues);
        queryValues = new ArrayList<>();
        queryValues.add(null);
        queryParams.put("query null", queryValues);
        assertEquals("Verify params with invalid path/query params", "",
                new Request().setPathParams(pathParams).setQueryParams(queryParams).getParam("path empty"));
        assertEquals("Verify params with invalid path/query params", "",
                new Request().setPathParams(pathParams).setQueryParams(queryParams).getParam("query empty"));
        assertEquals("Verify params with invalid path/query params", null,
                new Request().setPathParams(pathParams).setQueryParams(queryParams).getParam("path null"));
        assertEquals("Verify params with invalid path/query params", null,
                new Request().setPathParams(pathParams).setQueryParams(queryParams).getParam("query null"));
        assertEquals("Verify params with invalid path/query params", null,
                new Request().setPathParams(pathParams).setQueryParams(queryParams).getParam("query list null"));
        assertEquals("Verify params with invalid path/query params", null,
                new Request().setPathParams(pathParams).setQueryParams(queryParams).getParam("invalid"));
        assertEquals("Verify params with invalid path/query params", null,
                new Request().setPathParams(pathParams).setQueryParams(queryParams).getParam(null));

        assertEquals("Verify params with invalid path/query params", 8,
                new Request().setPathParams(pathParams).setQueryParams(queryParams).getParams().size());
        assertEquals("Verify params with invalid path/query params", 8,
                new Request().setPathParams(pathParams).setQueryParams(queryParams).getParamsFirstValue().size());
    }

    @Test
    public void verifyRequestPathParam() {
        assertTrue("Verify default empty path params", new Request().getPathParams().isEmpty());

        Map<String, String> params = new HashMap<>();
        assertTrue("Verify empty path params", new Request().setPathParams(params).getPathParams().isEmpty());

        params = new HashMap<>();
        params.put("One", "First");
        params.put("TWO", "MIDDLE");
        params.put("three", "Last");
        assertEquals("Verify path params", params, new Request().setPathParams(params).getPathParams());
        assertEquals("Verify path params", params.size(), new Request().setPathParams(params).getPathParams().size());
        assertEquals("Verify path params", params.get("One"),
                new Request().setPathParams(params).getPathParams().get("One"));
        assertEquals("Verify path params", params.get("TWO"),
                new Request().setPathParams(params).getPathParams().get("TWO"));
        assertEquals("Verify path params", params.get("three"),
                new Request().setPathParams(params).getPathParams().get("three"));
        assertNotEquals("Verify path params", params.get("One"),
                new Request().setPathParams(params).getPathParams().get("three"));
        Request request = new Request().setPathParams(params);
        assertEquals("Verify path params using common param getter", request.getParam("One"),
                request.getPathParam("One"));
        assertEquals("Verify path params using common param getter", request.getParam("TWO"),
                request.getPathParam("TWO"));
        assertEquals("Verify path params using common param getter", request.getParam("three"),
                request.getPathParam("three"));

        params = new HashMap<>();
        params.put("empty", "");
        params.put("null", null);
        assertEquals("Verify path params empty and null values", params,
                new Request().setPathParams(params).getPathParams());
        assertEquals("Verify path params empty and null values", params.size(),
                new Request().setPathParams(params).getPathParams().size());
        assertEquals("Verify path params empty value", params.get("empty"),
                new Request().setPathParams(params).getPathParams().get("empty"));
        assertEquals("Verify path params null value", params.get("null"),
                new Request().setPathParams(params).getPathParams().get("null"));
        assertEquals("Verify invalid path params", null,
                new Request().setPathParams(params).getPathParams().get("invalid"));
        assertEquals("Verify path params null value", null,
                new Request().setPathParams(params).getPathParams().get(null));
        request = new Request().setPathParams(params);
        assertEquals("Verify invalid path params using common param getter", request.getParam("empty"),
                request.getPathParam("empty"));
        assertEquals("Verify invalid path params using common param getter", request.getParam("null"),
                request.getPathParam("null"));
        assertEquals("Verify invalid path params using common param getter", request.getParam("invalid"),
                request.getPathParam("invalid"));
        assertEquals("Verify invalid path params using common param getter", request.getParam(null),
                request.getPathParam(null));
    }

    @Test
    public void verifyRequestQueryParam() {
        assertTrue("Verify default empty query params", new Request().getQueryParams().isEmpty());

        Map<String, List<String>> params = new HashMap<>();
        assertTrue("Verify empty query params", new Request().setQueryParams(params).getQueryParams().isEmpty());

        params = new HashMap<>();
        List<String> values = new ArrayList<String>();
        values.add("First");
        params.put("One", values);
        values = new ArrayList<String>();
        values.add("MIDDLE");
        params.put("TWO", values);
        values = new ArrayList<String>();
        values.add("Last");
        params.put("three", values);
        assertEquals("Verify query params", params.size(),
                new Request().setQueryParams(params).getQueryParams().size());
        assertEquals("Verify query params", params.get("One"),
                new Request().setQueryParams(params).getQueryParams().get("One"));
        assertEquals("Verify query params", params.get("TWO"),
                new Request().setQueryParams(params).getQueryParams().get("TWO"));
        assertEquals("Verify query params", params.get("three"),
                new Request().setQueryParams(params).getQueryParams().get("three"));
        assertNotEquals("Verify query params", params.get("One"),
                new Request().setQueryParams(params).getQueryParams().get("three"));
        Request request = new Request().setQueryParams(params);
        assertEquals("Verify query params using value and list getters", request.getQueryParam("One"),
                request.getQueryParams().get("One").get(0));
        assertEquals("Verify query params using value and list getters", request.getQueryParam("TWO"),
                request.getQueryParams().get("TWO").get(0));
        assertEquals("Verify query params using value and list getters", request.getQueryParam("three"),
                request.getQueryParams().get("three").get(0));
        assertEquals("Verify query params using common param getter", request.getParam("One"),
                request.getQueryParam("One"));
        assertEquals("Verify query params using common param getter", request.getParam("TWO"),
                request.getQueryParam("TWO"));
        assertEquals("Verify query params using common param getter", request.getParam("three"),
                request.getQueryParam("three"));

        params = new HashMap<>();
        values = new ArrayList<>();
        values.add("");
        params.put("empty", values);
        values = new ArrayList<>();
        values.add(null);
        params.put("null", values);
        assertEquals("Verify query params empty and null values", params.size(),
                new Request().setQueryParams(params).getQueryParams().size());
        assertEquals("Verify query params empty value", params.get("empty"),
                new Request().setQueryParams(params).getQueryParams().get("empty"));
        assertEquals("Verify query param null values", params.get("null"),
                new Request().setQueryParams(params).getQueryParams().get("null"));
        assertEquals("Verify invaid query params", null,
                new Request().setQueryParams(params).getQueryParams().get("invalid"));
        assertEquals("Verify invaid query params", null,
                new Request().setQueryParams(params).getQueryParams().get(null));
        request = new Request().setQueryParams(params);
        assertEquals("Verify query params with empty value using value and list getters",
                request.getQueryParam("empty"), request.getQueryParams().get("empty").get(0));
        assertEquals("Verify query params with null value using value and list getters", request.getQueryParam("null"),
                request.getQueryParams().get(null));
        assertEquals("Verify invalid query params using value and list getters", request.getQueryParam("invalid"),
                request.getQueryParams().get("invalid"));
        assertEquals("Verify invalid query params using value and list getters", request.getQueryParam(null),
                request.getQueryParams().get(null));
        assertEquals("Verify invalid query params using common param getter", request.getParam("empty"),
                request.getQueryParam("empty"));
        assertEquals("Verify invalid query params using common param getter", request.getParam("null"),
                request.getQueryParam("null"));
        assertEquals("Verify invalid query params using common param getter", request.getParam("invalid"),
                request.getQueryParam("invalid"));
        assertEquals("Verify invalid query params using common param getter", request.getParam(null),
                request.getQueryParam(null));
    }

    @Test
    public void verifyRequestQueryParamWithMultipleValues() {
        Map<String, List<String>> params = new HashMap<>();
        assertTrue("Verify empty query params", new Request().setQueryParams(params).getQueryParams().isEmpty());

        params = new HashMap<>();
        List<String> values = new ArrayList<>();
        values.add("One First");
        values.add("ONE MIDDLE");
        values.add("one last");
        params.put("One", values);
        values = new ArrayList<>();
        values.add("Two First");
        values.add("TWO MIDDLE");
        values.add("two last");
        params.put("TWO", values);
        values = new ArrayList<>();
        values.add("Three First");
        values.add("THREE MIDDLE");
        values.add("three last");
        params.put("three", values);
        assertEquals("Verify query params with multiple values", params.size(),
                new Request().setQueryParams(params).getQueryParams().size());
        assertEquals("Verify query params with multiple values", params.get("One").size(),
                new Request().setQueryParams(params).getQueryParams().get("One").size());
        assertEquals("Verify query params with multiple values", params.get("TWO").size(),
                new Request().setQueryParams(params).getQueryParams().get("TWO").size());
        assertEquals("Verify query params with multiple values", params.get("three").size(),
                new Request().setQueryParams(params).getQueryParams().get("three").size());
        assertEquals("Verify query params with multiple values", params.get("One").get(0),
                new Request().setQueryParams(params).getQueryParams().get("One").get(0));
        assertEquals("Verify query params with multiple values", params.get("TWO").get(1),
                new Request().setQueryParams(params).getQueryParams().get("TWO").get(1));
        assertEquals("Verify query params with multiple values", params.get("three").get(2),
                new Request().setQueryParams(params).getQueryParams().get("three").get(2));
        Request request = new Request().setQueryParams(params);
        assertEquals("Verify query params using single value getter will get first of the multiple values",
                request.getQueryParam("One"), request.getQueryParams().get("One").get(0));
        assertEquals("Verify query params using single value getter will get first of the multiple value",
                request.getQueryParam("TWO"), request.getQueryParams().get("TWO").get(0));
        assertEquals("Verify query params using single value getter will get first of the multiple values",
                request.getQueryParam("three"), request.getQueryParams().get("three").get(0));
        assertEquals("Verify query params with multiple values using common param getter", request.getParam("One"),
                request.getQueryParam("One"));
        assertEquals("Verify query with multiple values params using common param getter", request.getParam("TWO"),
                request.getQueryParam("TWO"));
        assertEquals("Verify query with multiple values params using common param getter", request.getParam("three"),
                request.getQueryParam("three"));
    }

    @Test
    public void verifyRequestSession() {
        assertEquals("Verify default null sesion", null, new Request().getSession());
        Session session = new Session();
        session.setId("TEST").setLastAccessedTime(1).setMaxInactiveInterval(2).setAttribute("name", "value");
        Session requestSession = new Request().setSession(session).getSession();
        assertEquals("Verify default null sesion", session.getId(), requestSession.getId());
        assertEquals("Verify valid request sesion last access time", session.getLastAccessedTime(),
                requestSession.getLastAccessedTime());
        assertEquals("Verify valid request sesion max interval", session.getMaxInactiveInterval(),
                requestSession.getMaxInactiveInterval());
        assertEquals("Verify valid request sesion attribute", session.getAttribute("name"),
                requestSession.getAttribute("name"));
        assertEquals("Verify valid request sesion attrbutes", session.getAttributes().get("name"),
                requestSession.getAttributes().get("name"));
        assertEquals("Verify valid request sesion validity", false, requestSession.invalid());
        assertEquals("Verify valid request sesion validity", session.invalid(), requestSession.invalid());
        session.invalidate();
        requestSession = new Request().setSession(session).getSession();
        assertEquals("Verify valid request sesion validity", true, requestSession.invalid());
        assertEquals("Verify valid request sesion validity", session.invalid(), requestSession.invalid());
    }

}
