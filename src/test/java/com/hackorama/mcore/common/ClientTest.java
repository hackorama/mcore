package com.hackorama.mcore.common;

import static org.junit.Assert.*;

import java.net.HttpURLConnection;

import javax.servlet.http.Cookie;

import org.junit.Test;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import com.hackorama.mcore.client.Client;
import com.hackorama.mcore.client.unirest.CachingUnirestClient;
import com.hackorama.mcore.client.unirest.CookieUnirestClient;
import com.hackorama.mcore.client.unirest.UnirestClient;
import com.hackorama.mcore.server.ServerTest;
import com.hackorama.mcore.service.BaseService;
import com.hackorama.mcore.service.Service;

public class ClientTest extends ServerTest {

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

    public ClientTest(String serverType) {
        super(serverType);
        Debug.disable();
    }

    @Test
    public void client_useDifferentClients_expectMatchingResponse() throws UnirestException {
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
    }

    @Test
    public void cookieClient_verifyCookieProcessing() throws UnirestException {
        CookieUnirestClient cookieClient = new CookieUnirestClient();
        cookieClient.clearCookies();
        assertEquals("COOKIE_OK", Unirest.get(TestServer.getEndPoint() + "/test/cookie")
                .header("Cookie", "CLIENT=VANILLA").asString().getBody());
        assertEquals("SERVER", cookieClient.getCookie("SERVER").getName());
        assertEquals("CHOCO", cookieClient.getCookie("SERVER").getValue());
        cookieClient.debugPrintCookies();
        cookieClient.debugLogCookies();
    }

    @Override
    protected Service useDefaultService() {
        return new TestService();
    }

}
