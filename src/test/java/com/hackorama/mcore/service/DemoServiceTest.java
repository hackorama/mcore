package com.hackorama.mcore.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.mashape.unirest.http.exceptions.UnirestException;

import com.hackorama.mcore.common.Request;
import com.hackorama.mcore.common.Response;
import com.hackorama.mcore.common.TestServer;
import com.hackorama.mcore.server.ServerTest;

/**
 * Example for creating service tests that will get tested on all server types,
 * using parameterization in ServerTest.
 *
 * @author Kishan Thomas (kishan.thomas@gmail.com)
 *
 */
public class DemoServiceTest extends ServerTest {

    /*
     * Create a test service
     */
    private static class DemoService extends TestService {

        @Override
        public void configure() {
            GET("/test", this::test);
        }

        public Response test(Request request) {
            return new Response("test");
        }
    }

    public DemoServiceTest(String serverType) {
        super(serverType);
        usingService(new DemoService());
    }

    @Test
    public void test() throws UnirestException {
        assertEquals("Check response body", "test", TestServer.getResponse("/test").getBody());
    }

}
