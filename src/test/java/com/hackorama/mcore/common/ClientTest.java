package com.hackorama.mcore.common;

import static org.junit.Assert.*;

import java.net.HttpURLConnection;

import javax.servlet.http.Cookie;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import com.hackorama.mcore.client.Client;
import com.hackorama.mcore.client.unirest.CachingUnirestClient;
import com.hackorama.mcore.client.unirest.CookieUnirestClient;
import com.hackorama.mcore.client.unirest.UnirestClient;
import com.hackorama.mcore.service.BaseService;

@RunWith(Parameterized.class)
public class ClientTest {

    private static class TestService extends BaseService {

        @Override
        public void configure() {
            GET("/test", this::test);
            GET("/test/cookie", this::cookie);
        }

        public Response cookie(Request request) {
            Debug.print(request);
            Cookie cookie = new Cookie("SERVER", "CHOCO");
            Response response = new Response("COOKIE_OK", HttpURLConnection.HTTP_OK);
            response.setCookie(cookie);
            Debug.print(response);
            return response;
        }

        public Response test(Request request) {
            Debug.log(request);
            Response response = new Response("CLIENT_OK", HttpURLConnection.HTTP_OK);
            Debug.log(response);
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

    public ClientTest(String serverType) {
        TestServer.setServerType(serverType);
    }

    @Test
    public void client_useDifferentClients_expectMatchingResponse() throws UnirestException {
        new TestService().configureUsing(TestServer.createNewServer()).start();
        TestServer.awaitStartup();
        Client client = new UnirestClient();
        Client cachingClient = new CachingUnirestClient();
        Client cookieClient = new CookieUnirestClient();
        Response clientResponse = client.get(TestServer.getEndPoint() + "/test");
        Response cachingClientResponse = cachingClient.get(TestServer.getEndPoint() + "/test");
        Response cookieClientResponse = cookieClient.get(TestServer.getEndPoint() + "/test");
        assertEquals("Validate same response from all clients", clientResponse.getBody(),
                cachingClientResponse.getBody());
        assertEquals("Validate same response from all clients", clientResponse.getBody(),
                cookieClientResponse.getBody());
        TestServer.awaitShutdown();
    }

    @Test
    public void cookieClient_verifyCookieProcessing() throws UnirestException {
        CookieUnirestClient cookieClient = new CookieUnirestClient();
        cookieClient.clearCookies();
        new TestService().configureUsing(TestServer.createNewServer()).start();
        TestServer.awaitStartup();
        assertEquals("COOKIE_OK", Unirest.get(TestServer.getEndPoint() + "/test/cookie")
                .header("Cookie", "CLIENT=VANILLA").asString().getBody());
        assertEquals("SERVER", cookieClient.getCookies().get(0).getName());
        assertEquals("CHOCO", cookieClient.getCookies().get(0).getValue());
        cookieClient.debugPrintCookies();
        cookieClient.debugLogCookies();
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
