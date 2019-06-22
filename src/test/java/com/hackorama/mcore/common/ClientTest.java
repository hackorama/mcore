package com.hackorama.mcore.common;

import static org.junit.Assert.*;

import java.net.HttpURLConnection;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.mashape.unirest.http.exceptions.UnirestException;

import com.hackorama.mcore.client.Client;
import com.hackorama.mcore.client.unirest.UnirestClient;
import com.hackorama.mcore.service.BaseService;

@RunWith(Parameterized.class)
public class ClientTest {

    private static class TestService extends BaseService {

        @Override
        public void configure() {
            GET("/test", this::test);
        }

        public Response test(Request request) {
            return new Response("CLIENTOK", HttpURLConnection.HTTP_OK);
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
        Client client = new UnirestClient();
        Client cachingClient = new UnirestClient();
        Client cookieClient = new UnirestClient();
        Response clientResponse = client.get(TestServer.getEndPoint());
        Response cachingClientResponse = cachingClient.get(TestServer.getEndPoint());
        Response cookieClientResponse = cookieClient.get(TestServer.getEndPoint());
        assertEquals("Validate same response from all clients", clientResponse.getBody(),
                cachingClientResponse.getBody());
        assertEquals("Validate same response from all clients", clientResponse.getBody(),
                cookieClientResponse.getBody());
        TestServer.awaitStartup();
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
