package m.core.service;

import static org.junit.Assert.*;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.junit.Test;

import m.core.http.Response;

public class ResponseTest {

    @Test
    public void verifyResponseBody() {
        assertEquals("Verify default body", null, new Response().getBody());
        assertEquals("Verify body", "foo", new Response().setBody("foo").getBody());
        assertEquals("Verify empty body", "", new Response().setBody("").getBody());
        assertEquals("Verify null body", null, new Response().setBody(null).getBody());
    }

    @Test
    public void verifyResponseCookie() {
        assertTrue("Verify default empty cookies", new Response().getCookies().isEmpty());

        Map<String, List<Cookie>> cookies = new HashMap<>();
        assertTrue("Verify empty cookies", new Response().setCookies(cookies).getCookies().isEmpty());

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
        assertEquals("Verify cookies", cookies.size(), new Response().setCookies(cookies).getCookies().size());
        assertEquals("Verify cookies", cookies.get("One"), new Response().setCookies(cookies).getCookies().get("One"));
        assertEquals("Verify cookies", cookies.get("TWO"), new Response().setCookies(cookies).getCookies().get("TWO"));
        assertEquals("Verify cookies", cookies.get("three"),
                new Response().setCookies(cookies).getCookies().get("three"));
        assertNotEquals("Verify cookies", cookies.get("One"),
                new Response().setCookies(cookies).getCookies().get("three"));
        Response response = new Response().setCookies(cookies);
        assertEquals("Verify cookies using value and list getters", response.getCookie("One"),
                response.getCookies().get("One").get(0));
        assertEquals("Verify cookies using value and list getters", response.getCookie("TWO"),
                response.getCookies().get("TWO").get(0));
        assertEquals("Verify cookies using value and list getters", response.getCookie("three"),
                response.getCookies().get("three").get(0));

        cookies = new HashMap<>();
        values = new ArrayList<>();
        values.add(new Cookie("empty", ""));
        cookies.put("empty", values);
        values = new ArrayList<>();
        values.add(null);
        cookies.put("null", values);
        assertEquals("Verify cookies empty and null values", cookies.size(),
                new Response().setCookies(cookies).getCookies().size());
        assertEquals("Verify cookies empty value", cookies.get("empty"),
                new Response().setCookies(cookies).getCookies().get("empty"));
        assertEquals("Verify cookies null values", cookies.get("null"),
                new Response().setCookies(cookies).getCookies().get("null"));
        assertEquals("Verify invaid cookies", null, new Response().setCookies(cookies).getCookies().get("invalid"));
        assertEquals("Verify invaid cookies", null, new Response().setCookies(cookies).getCookies().get(null));
    }

    @Test
    public void verifyResponseCookiesWithMultipleValues() {
        Map<String, List<Cookie>> cookies = new HashMap<>();
        assertTrue("Verify empty cookies", new Response().setCookies(cookies).getCookies().isEmpty());

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
                new Response().setCookies(cookies).getCookies().size());
        assertEquals("Verify cookies with multiple values", cookies.get("One").size(),
                new Response().setCookies(cookies).getCookies().get("One").size());
        assertEquals("Verify cookies with multiple values", cookies.get("TWO").size(),
                new Response().setCookies(cookies).getCookies().get("TWO").size());
        assertEquals("Verify cookies with multiple values", cookies.get("three").size(),
                new Response().setCookies(cookies).getCookies().get("three").size());
        assertEquals("Verify cookies with multiple values", cookies.get("One").get(0),
                new Response().setCookies(cookies).getCookies().get("One").get(0));
        assertEquals("Verify cookies with multiple values", cookies.get("TWO").get(1),
                new Response().setCookies(cookies).getCookies().get("TWO").get(1));
        assertEquals("Verify cookies with multiple values", cookies.get("three").get(2),
                new Response().setCookies(cookies).getCookies().get("three").get(2));
        Response response = new Response().setCookies(cookies);
        assertEquals("Verify cookies using single value getter will get first of the multiple values",
                response.getCookie("One"), response.getCookies().get("One").get(0));
        assertEquals("Verify cookies using single value getter will get first of the multiple value",
                response.getCookie("TWO"), response.getCookies().get("TWO").get(0));
        assertEquals("Verify cookies using single value getter will get first of the multiple values",
                response.getCookie("three"), response.getCookies().get("three").get(0));
    }

    @Test
    public void verifyResponseHeader() {
        assertTrue("Verify default empty headers", new Response().getHeaders().isEmpty());

        Map<String, List<String>> headers = new HashMap<>();
        assertTrue("Verify empty headers", new Response().setHeaders(headers).getHeaders().isEmpty());

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
        assertEquals("Verify headers", headers.size(), new Response().setHeaders(headers).getHeaders().size());
        assertEquals("Verify headers", headers.get("One"), new Response().setHeaders(headers).getHeaders().get("One"));
        assertEquals("Verify headers", headers.get("TWO"), new Response().setHeaders(headers).getHeaders().get("TWO"));
        assertEquals("Verify headers", headers.get("three"),
                new Response().setHeaders(headers).getHeaders().get("three"));
        assertNotEquals("Verify headers", headers.get("One"),
                new Response().setHeaders(headers).getHeaders().get("three"));
        Response response = new Response().setHeaders(headers);
        assertEquals("Verify headers using value and list getters", response.getHeader("One"),
                response.getHeaders().get("One").get(0));
        assertEquals("Verify headers using value and list getters", response.getHeader("TWO"),
                response.getHeaders().get("TWO").get(0));
        assertEquals("Verify headers using value and list getters", response.getHeader("three"),
                response.getHeaders().get("three").get(0));

        headers = new HashMap<>();
        values = new ArrayList<>();
        values.add("");
        headers.put("empty", values);
        values = new ArrayList<>();
        values.add(null);
        headers.put("null", values);
        assertEquals("Verify headers empty and null values", headers.size(),
                new Response().setHeaders(headers).getHeaders().size());
        assertEquals("Verify headers empty value", headers.get("empty"),
                new Response().setHeaders(headers).getHeaders().get("empty"));
        assertEquals("Verify headers null values", headers.get("null"),
                new Response().setHeaders(headers).getHeaders().get("null"));
        assertEquals("Verify invaid headers", null, new Response().setHeaders(headers).getHeaders().get("invalid"));
        assertEquals("Verify invaid headers", null, new Response().setHeaders(headers).getHeaders().get(null));

    }

    @Test
    public void verifyResponseHeadersWithMultipleValues() {
        Map<String, List<String>> headers = new HashMap<>();
        assertTrue("Verify empty headers", new Response().setHeaders(headers).getHeaders().isEmpty());

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
                new Response().setHeaders(headers).getHeaders().size());
        assertEquals("Verify headers with multiple values", headers.get("One").size(),
                new Response().setHeaders(headers).getHeaders().get("One").size());
        assertEquals("Verify headers with multiple values", headers.get("TWO").size(),
                new Response().setHeaders(headers).getHeaders().get("TWO").size());
        assertEquals("Verify headers with multiple values", headers.get("three").size(),
                new Response().setHeaders(headers).getHeaders().get("three").size());
        assertEquals("Verify headers with multiple values", headers.get("One").get(0),
                new Response().setHeaders(headers).getHeaders().get("One").get(0));
        assertEquals("Verify headers with multiple values", headers.get("TWO").get(1),
                new Response().setHeaders(headers).getHeaders().get("TWO").get(1));
        assertEquals("Verify headers with multiple values", headers.get("three").get(2),
                new Response().setHeaders(headers).getHeaders().get("three").get(2));
        Response response = new Response().setHeaders(headers);
        assertEquals("Verify headers using single value getter will get first of the multiple values",
                response.getHeader("One"), response.getHeaders().get("One").get(0));
        assertEquals("Verify headers using single value getter will get first of the multiple value",
                response.getHeader("TWO"), response.getHeaders().get("TWO").get(0));
        assertEquals("Verify headers using single value getter will get first of the multiple values",
                response.getHeader("three"), response.getHeaders().get("three").get(0));
    }

    @Test
    public void verifyResponseStatus() {
        assertEquals("Verify default status", HttpURLConnection.HTTP_OK, new Response().getStatus());
        assertEquals("Verify default status", 42, new Response().setStatus(42).getStatus());
    }

}
