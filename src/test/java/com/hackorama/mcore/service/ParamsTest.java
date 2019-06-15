package com.hackorama.mcore.service;

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

import com.hackorama.mcore.common.Request;
import com.hackorama.mcore.common.Response;
import com.hackorama.mcore.common.TestServer;

@RunWith(Parameterized.class)
public class ParamsTest {

    private static class TestParams extends BaseService {

        public Response getParams(Request request) {
            boolean result = "one".equals(request.getPathParams().get("one"));
            result = "two".equals(request.getPathParams().get("two"));
            result = "three".equals(request.getPathParams().get("three"));
            return new Response("ONE", result ? HttpURLConnection.HTTP_OK : HttpURLConnection.HTTP_BAD_REQUEST);
        }

        @Override
        public void configure() {
            GET("/test/{one}/{two}/{three}", this::getParams);
            GET("/test/{one}/{two}/{three}/", this::getParams);
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

    public ParamsTest(String serverType) {
        TestServer.setServerType(serverType);
    }

    @Test
    public void service_attachServicesUnderSameServer_expectsNoErrors() throws UnirestException {
        new TestParams().configureUsing(TestServer.createNewServer()).start();
        /*new BaseService() {
            public Response hello(Request request) {
                return new Response("hello");
            }
            @Override
            public void configure() {
                route(HttpMethod.GET, "/hello", this::hello);
            }

        };*/
        TestServer.awaitStartup();
        assertTrue(TestServer.validResponseCode("/test/one/two/three", HttpURLConnection.HTTP_OK));
        assertTrue(TestServer.validResponseCode("/test/one/two/three/", HttpURLConnection.HTTP_OK));
        assertFalse(TestServer.validResponseCode("/test/one/two/three/x", HttpURLConnection.HTTP_OK));
        assertFalse(TestServer.validResponseCode("/test/two/three", HttpURLConnection.HTTP_OK));
        assertFalse(TestServer.validResponseCode("/test/two/three/one", HttpURLConnection.HTTP_OK));
        assertFalse(TestServer.validResponseCode("/test/three/one/two", HttpURLConnection.HTTP_OK));
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
