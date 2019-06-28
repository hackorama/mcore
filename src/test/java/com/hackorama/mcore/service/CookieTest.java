package com.hackorama.mcore.service;

import static org.junit.Assert.*;

import javax.servlet.http.Cookie;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.mashape.unirest.http.exceptions.UnirestException;

import com.hackorama.mcore.common.Request;
import com.hackorama.mcore.common.Response;
import com.hackorama.mcore.common.TestServer;

@RunWith(Parameterized.class)
public class CookieTest {

    private static class TestCookies extends BaseService {

        @Override
        public void configure() {
            GET("/test", this::getCookie);
            GET("/test/multi", this::getMultiCookie);
            GET("/test/", this::getCookie);
            GET("/test/{any}", this::getCookie);
        }

        public Response getCookie(Request request) {
            Response response = new Response("OK");
            Cookie cookie = new Cookie("FOO", "BAR");
            response.setCookie(cookie);
            return response;
        }

        public Response getMultiCookie(Request request) {
            Response response = new Response("OK");
            Cookie cookie = new Cookie("FOO", "BAR");
            cookie.setPath("/bar");
            response.setCookie(cookie);
            cookie = new Cookie("FOO", "BAZ");
            cookie.setPath("/baz");
            response.setCookie(cookie);
            return response;
        }
    }

    @AfterClass
    public static void afterAllTests() throws Exception {
        TestServer.awaitShutdown();
    }

    @Parameters
    public static Iterable<? extends Object> data() {
        return TestServer.getServerTypeList();
    }

    public CookieTest(String serverType) {
        TestServer.setServerType(serverType);
    }

    @Test
    public void service_verifyCookieInResponse() throws UnirestException {
        new TestCookies().configureUsing(TestServer.createNewServer()).start();
        TestServer.awaitStartup();
        TestServer.useCookies();
        TestServer.clearCookies();
        assertTrue(TestServer.validResponse("/test", "OK"));
        assertTrue("Check cookie name and value", TestServer.getCookies().stream().anyMatch( e -> "FOO".equals(e.getName()) && "BAR".equals(e.getValue())));
        assertFalse("Check invalid cookie name and value", TestServer.getCookies().stream().anyMatch( e -> "BAR".equals(e.getName()) && "FOO".equals(e.getValue())));
        assertFalse("Check invalid cookie name and value", TestServer.getCookies().stream().anyMatch( e -> "BAR".equals(e.getName()) && "BAR".equals(e.getValue())));
        assertFalse("Check invalid cookie name and value", TestServer.getCookies().stream().anyMatch( e -> "FOO".equals(e.getName()) && "FOO".equals(e.getValue())));
        assertFalse("Check invalid cookie name and value", TestServer.getCookies().stream().anyMatch( e -> "FAIL".equals(e.getName()) && "BAR".equals(e.getValue())));
        assertFalse("Check invalid cookie name and value", TestServer.getCookies().stream().anyMatch( e -> "FOO".equals(e.getName()) && "FAIL".equals(e.getValue())));
        assertFalse("Check invalid cookie name and value", TestServer.getCookies().stream().anyMatch( e -> "FAIL".equals(e.getName()) && "FAIL".equals(e.getValue())));
        TestServer.clearCookies();
        TestServer.awaitShutdown();
    }

    @Test
    public void service_verifyMultipleValueCookieInResponse() throws UnirestException {
        new TestCookies().configureUsing(TestServer.createNewServer()).start();
        TestServer.awaitStartup();
        TestServer.useCookies();
        TestServer.clearCookies();
        assertTrue(TestServer.validResponse("/test/multi", "OK"));
        if (!TestServer.isVertxServer()) {
            // Vertx does not allow two cookies of same name, overwrites with the last one
            assertTrue("Check cookie name and value", TestServer.getCookies().stream()
                    .anyMatch(e -> "FOO".equals(e.getName()) && "BAR".equals(e.getValue())));
        }
        assertFalse("Check invalid cookie name and value", TestServer.getCookies().stream().anyMatch( e -> "BAR".equals(e.getName()) && "FOO".equals(e.getValue())));
        assertFalse("Check invalid cookie name and value", TestServer.getCookies().stream().anyMatch( e -> "BAR".equals(e.getName()) && "BAR".equals(e.getValue())));
        assertFalse("Check invalid cookie name and value", TestServer.getCookies().stream().anyMatch( e -> "FOO".equals(e.getName()) && "FOO".equals(e.getValue())));
        assertFalse("Check invalid cookie name and value", TestServer.getCookies().stream().anyMatch( e -> "FAIL".equals(e.getName()) && "BAR".equals(e.getValue())));
        assertFalse("Check invalid cookie name and value", TestServer.getCookies().stream().anyMatch( e -> "FOO".equals(e.getName()) && "FAIL".equals(e.getValue())));
        assertFalse("Check invalid cookie name and value", TestServer.getCookies().stream().anyMatch( e -> "FAIL".equals(e.getName()) && "FAIL".equals(e.getValue())));

        assertTrue("Check cookie name and value", TestServer.getCookies().stream().anyMatch( e -> "FOO".equals(e.getName()) && "BAZ".equals(e.getValue())));
        assertFalse("Check invalid cookie name and value", TestServer.getCookies().stream().anyMatch( e -> "BAZ".equals(e.getName()) && "FOO".equals(e.getValue())));
        assertFalse("Check invalid cookie name and value", TestServer.getCookies().stream().anyMatch( e -> "BAZ".equals(e.getName()) && "BAZ".equals(e.getValue())));
        assertFalse("Check invalid cookie name and value", TestServer.getCookies().stream().anyMatch( e -> "FOO".equals(e.getName()) && "FOO".equals(e.getValue())));
        assertFalse("Check invalid cookie name and value", TestServer.getCookies().stream().anyMatch( e -> "FAIL".equals(e.getName()) && "BAZ".equals(e.getValue())));
        assertFalse("Check invalid cookie name and value", TestServer.getCookies().stream().anyMatch( e -> "FOO".equals(e.getName()) && "FAIL".equals(e.getValue())));
        assertFalse("Check invalid cookie name and value", TestServer.getCookies().stream().anyMatch( e -> "FAIL".equals(e.getName()) && "FAIL".equals(e.getValue())));
        TestServer.clearCookies();
        TestServer.awaitShutdown();
    }

    @Test
    public void service_withPathParams_verifyCookieInResponse() throws UnirestException {
        new TestCookies().configureUsing(TestServer.createNewServer()).start();
        TestServer.awaitStartup();
        TestServer.useCookies();
        TestServer.clearCookies();
        assertTrue(TestServer.validResponse("/test/param", "OK"));
        assertEquals("Check cookie name", "FOO", TestServer.getCookies().get(0).getName());
        assertEquals("Check cookie value", "BAR", TestServer.getCookies().get(0).getValue());
        TestServer.clearCookies();
        TestServer.awaitShutdown();
    }

    @Before
    public void setUp() throws Exception {
        System.out.println("Testing with server type: " + TestServer.getServerType());
    }

    @After
    public void tearDown() throws Exception {
        TestServer.awaitShutdown();
    }

}
