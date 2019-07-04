package com.hackorama.mcore.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.mashape.unirest.http.exceptions.UnirestException;

import com.hackorama.mcore.common.Request;
import com.hackorama.mcore.common.Response;
import com.hackorama.mcore.common.TestServer;
import com.hackorama.mcore.server.ServerTest;

public class SessionTest extends ServerTest {

    private static class SessionTestService extends TestService {

        @Override
        public void configure() {
            GET("/test", this::test);
        }

        public Response test(Request request) {
            return new Response("test");
        }
    }

    public SessionTest(String serverType) {
        super(serverType);
    }

    @Test
    public void test() throws UnirestException {
        assertEquals("Check response body", "test", TestServer.getResponse("/test").getBody());
    }

    @Override
    protected Service useDefaultService() {
        return new SessionTestService();
    }

}
