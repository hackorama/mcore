package com.hackorama.mcore.service;

import static org.junit.Assert.*;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

import org.junit.Test;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import com.hackorama.mcore.common.Request;
import com.hackorama.mcore.common.Response;
import com.hackorama.mcore.common.TestServer;
import com.hackorama.mcore.server.ServerTest;

public class CookieTest extends ServerTest {

    private static class TestCookiesService extends BaseService {

        @Override
        public void configure() {
            GET("/test/cookie/response", this::getCookieResponse);
            GET("/test/multi/cookie/response", this::getMultipleCookieResponse);
            GET("/test/cookie/request", this::getCookieRequest);
            GET("/test/multi/cookie/request", this::getMultipleCookieRequest);
            GET("/test/multi/cookie/request/as/single/header", this::getMultipleCookieRequestAsSingleHeader);
        }

        public Response getCookieRequest(Request request) {
            Map<String, List<Cookie>> cookies = request.getCookies();
            boolean result = cookies.get("ONLY").size() == 1;
            result &= "ONLY".equals(cookies.get("ONLY").get(0).getName());
            result &= "ONE".equals(cookies.get("ONLY").get(0).getValue());
            return new Response("COOKIE_REQUEST",
                    result ? HttpURLConnection.HTTP_OK : HttpURLConnection.HTTP_BAD_REQUEST);
        }

        public Response getCookieResponse(Request request) {
            Response response = new Response("COOKIE_RESPONSE");
            Cookie cookie = new Cookie("ONLY", "ONE");
            response.setCookie(cookie);
            return response;
        }

        public Response getMultipleCookieRequest(Request request) {
            Map<String, List<Cookie>> cookies = request.getCookies();
            boolean result = cookies.get("ONLY").size() == 1;
            if (TestServer.isVertxServer() || TestServer.isPlayServer()) {
                // ONLY=ONE
                result &= cookies.containsKey("MANY") == false;
                result &= cookies.containsKey("DUPLICATE") == false;
                result &= cookies.get("ONLY").stream()
                        .anyMatch(e -> "ONLY".equals(e.getName()) && "ONE".equals(e.getValue()));
            } else if (TestServer.isSparkServer()) {
                // ONLY=ONE
                // MANY=LAST
                // DUPLICATE=SAME
                result &= cookies.get("MANY").size() == 1;
                result &= cookies.get("DUPLICATE").size() == 1;
                result &= cookies.get("ONLY").stream()
                        .anyMatch(e -> "ONLY".equals(e.getName()) && "ONE".equals(e.getValue()));
                result &= cookies.get("MANY").stream()
                        .anyMatch(e -> "MANY".equals(e.getName()) && "LAST".equals(e.getValue()));
                result &= cookies.get("DUPLICATE").stream()
                        .anyMatch(e -> "DUPLICATE".equals(e.getName()) && "SAME".equals(e.getValue()));
            } else { // Spring and future server types
                // ONLY=ONE
                // MANY=FIRST
                // MANY=SECOND
                // MANY=LAST
                // DUPLICATE=SAME
                // DUPLICATE=SAME
                result &= cookies.get("MANY").size() == 3;
                result &= cookies.get("DUPLICATE").size() == 2;
                result &= cookies.get("ONLY").stream()
                        .anyMatch(e -> "ONLY".equals(e.getName()) && "ONE".equals(e.getValue()));
                result &= cookies.get("MANY").stream()
                        .anyMatch(e -> "MANY".equals(e.getName()) && "FIRST".equals(e.getValue()));
                result &= cookies.get("MANY").stream()
                        .anyMatch(e -> "MANY".equals(e.getName()) && "SECOND".equals(e.getValue()));
                result &= cookies.get("MANY").stream()
                        .anyMatch(e -> "MANY".equals(e.getName()) && "LAST".equals(e.getValue()));
                result &= cookies.get("DUPLICATE").stream()
                        .anyMatch(e -> "DUPLICATE".equals(e.getName()) && "SAME".equals(e.getValue()));
            }
            return new Response("MULTIPLE_COOKIE_REQUEST",
                    result ? HttpURLConnection.HTTP_OK : HttpURLConnection.HTTP_BAD_REQUEST);
        }

        public Response getMultipleCookieRequestAsSingleHeader(Request request) {
            Map<String, List<Cookie>> cookies = request.getCookies();
            boolean result = cookies.get("ONLY").size() == 1;
            if (TestServer.isVertxServer() || TestServer.isPlayServer()) {
                // ONLY=ONE
                // MANY=FIRST
                // DUPLICATE=SAME
                result &= cookies.get("MANY").size() == 1;
                result &= cookies.get("DUPLICATE").size() == 1;
                result &= cookies.get("ONLY").stream()
                        .anyMatch(e -> "ONLY".equals(e.getName()) && "ONE".equals(e.getValue()));
                result &= cookies.get("MANY").stream()
                        .anyMatch(e -> "MANY".equals(e.getName()) && "FIRST".equals(e.getValue()));
                result &= cookies.get("DUPLICATE").stream()
                        .anyMatch(e -> "DUPLICATE".equals(e.getName()) && "SAME".equals(e.getValue()));
            } else if (TestServer.isSparkServer()) {
                // ONLY=ONE
                // MANY=LAST
                // DUPLICATE=SAME
                result &= cookies.get("MANY").size() == 1;
                result &= cookies.get("DUPLICATE").size() == 1;
                result &= cookies.get("ONLY").stream()
                        .anyMatch(e -> "ONLY".equals(e.getName()) && "ONE".equals(e.getValue()));
                result &= cookies.get("MANY").stream()
                        .anyMatch(e -> "MANY".equals(e.getName()) && "LAST".equals(e.getValue()));
                result &= cookies.get("DUPLICATE").stream()
                        .anyMatch(e -> "DUPLICATE".equals(e.getName()) && "SAME".equals(e.getValue()));
            } else { // Spring and future server types
                // ONLY=ONE
                // MANY=FIRST
                // MANY=SECOND
                // MANY=LAST
                // DUPLICATE=SAME
                // DUPLICATE=SAME
                result &= cookies.get("MANY").size() == 3;
                result &= cookies.get("DUPLICATE").size() == 2;
                result &= cookies.get("ONLY").stream()
                        .anyMatch(e -> "ONLY".equals(e.getName()) && "ONE".equals(e.getValue()));
                result &= cookies.get("MANY").stream()
                        .anyMatch(e -> "MANY".equals(e.getName()) && "FIRST".equals(e.getValue()));
                result &= cookies.get("MANY").stream()
                        .anyMatch(e -> "MANY".equals(e.getName()) && "SECOND".equals(e.getValue()));
                result &= cookies.get("MANY").stream()
                        .anyMatch(e -> "MANY".equals(e.getName()) && "LAST".equals(e.getValue()));
                result &= cookies.get("DUPLICATE").stream()
                        .anyMatch(e -> "DUPLICATE".equals(e.getName()) && "SAME".equals(e.getValue()));
            }
            return new Response("MULTIPLE_COOKIE_REQUEST",
                    result ? HttpURLConnection.HTTP_OK : HttpURLConnection.HTTP_BAD_REQUEST);
        }

        public Response getMultipleCookieResponse(Request request) {
            Response response = new Response("MULTIPLE_COOKIE_RESPONSE");
            Cookie cookie = new Cookie("MANY", "FIRST");
            cookie.setPath("/first");
            response.setCookie(cookie);
            cookie = new Cookie("MANY", "SECOND");
            cookie.setPath("/second");
            response.setCookie(cookie);
            return response;
        }
    }

    public CookieTest(String serverType) {
        super(serverType);
    }

    @Test
    public void service_receiveMultipleCookies_verifyMultipleCookiesInResponse() throws UnirestException {
        assertTrue(TestServer.validResponse("/test/multi/cookie/response", "MULTIPLE_COOKIE_RESPONSE"));
        if (!(TestServer.isVertxServer() || TestServer.isPlayServer())) {
            // Vertx/Play does not allow two cookies of same name, overwrites with the last one
            assertTrue("Check cookie name and value", TestServer.getCookies().stream()
                    .anyMatch(e -> "MANY".equals(e.getName()) && "FIRST".equals(e.getValue())));
        }
        assertFalse("Check invalid cookie name and value", TestServer.getCookies().stream()
                .anyMatch(e -> "FIRST".equals(e.getName()) && "MANY".equals(e.getValue())));
        assertFalse("Check invalid cookie name and value", TestServer.getCookies().stream()
                .anyMatch(e -> "FIRST".equals(e.getName()) && "FIRST".equals(e.getValue())));
        assertFalse("Check invalid cookie name and value", TestServer.getCookies().stream()
                .anyMatch(e -> "MANY".equals(e.getName()) && "MANY".equals(e.getValue())));
        assertFalse("Check invalid cookie name and value", TestServer.getCookies().stream()
                .anyMatch(e -> "FAIL".equals(e.getName()) && "FIRST".equals(e.getValue())));
        assertFalse("Check invalid cookie name and value", TestServer.getCookies().stream()
                .anyMatch(e -> "MANY".equals(e.getName()) && "FAIL".equals(e.getValue())));
        assertFalse("Check invalid cookie name and value", TestServer.getCookies().stream()
                .anyMatch(e -> "FAIL".equals(e.getName()) && "FAIL".equals(e.getValue())));

        assertTrue("Check cookie name and value", TestServer.getCookies().stream()
                .anyMatch(e -> "MANY".equals(e.getName()) && "SECOND".equals(e.getValue())));
        assertFalse("Check invalid cookie name and value", TestServer.getCookies().stream()
                .anyMatch(e -> "SECOND".equals(e.getName()) && "MANY".equals(e.getValue())));
        assertFalse("Check invalid cookie name and value", TestServer.getCookies().stream()
                .anyMatch(e -> "SECOND".equals(e.getName()) && "SECOND".equals(e.getValue())));
        assertFalse("Check invalid cookie name and value", TestServer.getCookies().stream()
                .anyMatch(e -> "MANY".equals(e.getName()) && "MANY".equals(e.getValue())));
        assertFalse("Check invalid cookie name and value", TestServer.getCookies().stream()
                .anyMatch(e -> "FAIL".equals(e.getName()) && "SECOND".equals(e.getValue())));
        assertFalse("Check invalid cookie name and value", TestServer.getCookies().stream()
                .anyMatch(e -> "MANY".equals(e.getName()) && "FAIL".equals(e.getValue())));
        assertFalse("Check invalid cookie name and value", TestServer.getCookies().stream()
                .anyMatch(e -> "FAIL".equals(e.getName()) && "FAIL".equals(e.getValue())));
    }

    @Test
    public void service_recieveCookie_verifyCookieInResponse() throws UnirestException {
        assertTrue(TestServer.validResponse("/test/cookie/response", "COOKIE_RESPONSE"));
        assertTrue("Check cookie name and value", TestServer.getCookies().stream()
                .anyMatch(e -> "ONLY".equals(e.getName()) && "ONE".equals(e.getValue())));
        assertFalse("Check invalid cookie name and value", TestServer.getCookies().stream()
                .anyMatch(e -> "ONE".equals(e.getName()) && "ONLY".equals(e.getValue())));
        assertFalse("Check invalid cookie name and value", TestServer.getCookies().stream()
                .anyMatch(e -> "ONE".equals(e.getName()) && "ONE".equals(e.getValue())));
        assertFalse("Check invalid cookie name and value", TestServer.getCookies().stream()
                .anyMatch(e -> "ONLY".equals(e.getName()) && "ONLY".equals(e.getValue())));
        assertFalse("Check invalid cookie name and value", TestServer.getCookies().stream()
                .anyMatch(e -> "FAIL".equals(e.getName()) && "ONE".equals(e.getValue())));
        assertFalse("Check invalid cookie name and value", TestServer.getCookies().stream()
                .anyMatch(e -> "ONLY".equals(e.getName()) && "FAIL".equals(e.getValue())));
        assertFalse("Check invalid cookie name and value", TestServer.getCookies().stream()
                .anyMatch(e -> "FAIL".equals(e.getName()) && "FAIL".equals(e.getValue())));
    }

    @Test
    public void service_sendCookie_verifyCookieInRequest() throws UnirestException {
        assertEquals(HttpURLConnection.HTTP_OK, Unirest.get(TestServer.getEndPoint() + "/test/cookie/request")
                .header("Cookie", "ONLY=ONE").asString().getStatus());
    }

    @Test
    public void service_sendMultipleCookies_verifyMultipleCookiesInRequest() throws UnirestException {
        assertEquals(HttpURLConnection.HTTP_OK,
                Unirest.get(TestServer.getEndPoint() + "/test/multi/cookie/request").header("Cookie", "ONLY=ONE")
                        .header("Cookie", "MANY=FIRST").header("Cookie", "MANY=SECOND").header("Cookie", "MANY=LAST")
                        .header("Cookie", "DUPLICATE=SAME").header("Cookie", "DUPLICATE=SAME").asString().getStatus());
    }

    @Test
    public void service_sendMultipleCookiesAsSingleHeader_verifyMultipleCookiesInRequest() throws UnirestException {
        assertEquals(HttpURLConnection.HTTP_OK,
                Unirest.get(TestServer.getEndPoint() + "/test/multi/cookie/request/as/single/header")
                        .header("Cookie", "ONLY=ONE;MANY=FIRST;MANY=SECOND;MANY=LAST;DUPLICATE=SAME;DUPLICATE=SAME")
                        .asString().getStatus());
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        TestServer.useCookies();
        TestServer.clearCookies();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        TestServer.clearCookies();
    }

    @Override
    protected Service useDefaultService() {
        return new TestCookiesService();
    }

}
