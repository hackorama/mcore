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

import com.hackorama.mcore.common.Debug;
import com.hackorama.mcore.common.Request;
import com.hackorama.mcore.common.Response;
import com.hackorama.mcore.common.TestServer;

@RunWith(Parameterized.class)
public class CookieTest {

    private static class TestCookies extends BaseService {

        @Override
        public void configure() {
            GET("/test", this::getCookie);
            GET("/test/", this::getCookie);
            GET("/test/{any}", this::getCookie);
        }

        public Response getCookie(Request request) {
            Response response = new Response("OK");
            Cookie cookie = new Cookie("FOO", "BAR");
            response.setCookie(cookie);
            Debug.print(response);
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
        if (TestServer.isVertxServer()) {
            return; // TODO FIXME
        }
        new TestCookies().configureUsing(TestServer.createNewServer()).start();
        TestServer.awaitStartup();
        TestServer.useCookies();
        TestServer.clearCookies();
        assertTrue(TestServer.validResponse("/test", "OK"));
        assertEquals("Check cookie name", "FOO", TestServer.getCookies().get(0).getName());
        assertEquals("Check cookie value", "BAR", TestServer.getCookies().get(0).getValue());
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
