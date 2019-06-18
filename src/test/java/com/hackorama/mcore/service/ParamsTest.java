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

        public Response getPathParams(Request request) {
            boolean result = "one".equals(request.getPathParams().get("one"));
            result &= "two".equals(request.getPathParams().get("two"));
            result &= "three".equals(request.getPathParams().get("three"));
            return new Response("PATHOK", result ? HttpURLConnection.HTTP_OK : HttpURLConnection.HTTP_BAD_REQUEST);
        }

        public Response getQueryParams(Request request) {
            boolean result = request.getQueryParams().get("one").contains("uno");
            result &= request.getQueryParams().get("two").contains("dos");
            result &= request.getQueryParams().get("three").contains("tres");
            return new Response("QUERYOK", result ? HttpURLConnection.HTTP_OK : HttpURLConnection.HTTP_BAD_REQUEST);
        }

        public Response getPathQueryParams(Request request) {
            boolean result = request.getQueryParams().get("one").contains("uno");
            result &= request.getQueryParams().get("two").contains("dos");
            result &= request.getQueryParams().get("three").contains("tres");
            result &= "one".equals(request.getPathParams().get("one"));
            result &= "two".equals(request.getPathParams().get("two"));
            result &= "three".equals(request.getPathParams().get("three"));
            return new Response("QUERYOK", result ? HttpURLConnection.HTTP_OK : HttpURLConnection.HTTP_BAD_REQUEST);
        }

        @Override
        public void configure() {
            GET("/testpathparams/{one}/{two}/{three}", this::getPathParams);
            GET("/testpathparams/{one}/{two}/{three}/", this::getPathParams);
            GET("/testqueryparams", this::getQueryParams);
            GET("/testqueryparams/", this::getQueryParams);
            GET("/testpathqueryparams/{one}/{two}/{three}", this::getPathQueryParams);
            GET("/testpathqueryparams/{one}/{two}/{three}/", this::getPathQueryParams);
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
    public void service_sendPathParameters_expectCorrectParamProcessing() throws UnirestException {
        new TestParams().configureUsing(TestServer.createNewServer()).start();
        TestServer.awaitStartup();
        assertTrue(TestServer.validResponseCode("/testpathparams/one/two/three", HttpURLConnection.HTTP_OK));
        assertTrue(TestServer.validResponseCode("/testpathparams/one/two/three/", HttpURLConnection.HTTP_OK));
        assertFalse(TestServer.validResponseCode("/testpathparams/one/two/three/x", HttpURLConnection.HTTP_OK));
        assertFalse(TestServer.validResponseCode("/testpathparams/two/three", HttpURLConnection.HTTP_OK));
        assertFalse(TestServer.validResponseCode("/testpathparams/two/three/one", HttpURLConnection.HTTP_OK));
        assertFalse(TestServer.validResponseCode("/testpathparams/three/one/two", HttpURLConnection.HTTP_OK));
        TestServer.awaitShutdown();
    }

    @Test
    public void service_sendQueryParameters_expectCorrectParamProcessing() throws UnirestException {
        new TestParams().configureUsing(TestServer.createNewServer()).start();
        TestServer.awaitStartup();
        assertTrue(TestServer.validResponseCode("/testpathqueryparams/one/two/three?one=uno&two=dos&three=tres",
                HttpURLConnection.HTTP_OK));
        assertTrue(TestServer.validResponseCode("/testpathqueryparams/one/two/three/?one=uno&two=dos&three=tres",
                HttpURLConnection.HTTP_OK));
        TestServer.awaitShutdown();
    }

    @Test
    public void service_sendPathAndQueryParameters_expectCorrectParamProcessing() throws UnirestException {
        new TestParams().configureUsing(TestServer.createNewServer()).start();
        TestServer.awaitStartup();
        assertTrue(
                TestServer.validResponseCode("/testqueryparams?one=uno&two=dos&three=tres", HttpURLConnection.HTTP_OK));
        assertTrue(TestServer.validResponseCode("/testqueryparams/?one=uno&two=dos&three=tres",
                HttpURLConnection.HTTP_OK));
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
