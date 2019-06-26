package com.hackorama.mcore.service;

import static org.junit.Assert.*;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import com.hackorama.mcore.common.Request;
import com.hackorama.mcore.common.Response;
import com.hackorama.mcore.common.TestServer;

@RunWith(Parameterized.class)
public class HeadersTest {

    private static class TestHeaders extends BaseService {

        @Override
        public void configure() {
            GET("/testheaderpathqueryparams/{one}/{two}/{three}", this::getHeadersPathQueryParams);
            GET("/testheaderpathqueryparams/{one}/{two}/{three}/", this::getHeadersPathQueryParams);
            GET("/testresponseheaders", this::getResponseHeaders);
        }

        public Response getHeadersPathQueryParams(Request request) {
            boolean result = "one".equals(request.getHeaders().get("UNO").get(0));
            result &= "two".equals(request.getHeaders().get("DOS").get(0));
            result &= "three".equals(request.getHeaders().get("TRES").get(0));
            result &= request.getQueryParams().get("one").contains("uno");
            result &= request.getQueryParams().get("two").contains("dos");
            result &= request.getQueryParams().get("three").contains("tres");
            result &= "one".equals(request.getPathParams().get("one"));
            result &= "two".equals(request.getPathParams().get("two"));
            result &= "three".equals(request.getPathParams().get("three"));
            return new Response("QUERYOK", result ? HttpURLConnection.HTTP_OK : HttpURLConnection.HTTP_BAD_REQUEST);
        }

        public Response getResponseHeaders(Request request) {
            Response response = new Response("OK");
            response.setHeaders(request.getHeaders());
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

    public HeadersTest(String serverType) {
        TestServer.setServerType(serverType);
    }

    @Test
    public void service_sendHeaders_expectResponseToEchoTheHeaders() throws UnirestException {
        new TestHeaders().configureUsing(TestServer.createNewServer()).start();
        TestServer.awaitStartup();
        Map<String, String> headers = new HashMap<>();
        headers.put("UNO", "one");
        headers.put("DOS", "two");
        headers.put("TRES", "three");
        Unirest.get(TestServer.getEndPoint() + "testresponseheaders").headers(headers);
        Map<String, List<String>> responseHeaders = TestServer.getResponseHeaders("/TestServer", headers);
        assertTrue(responseHeaders.get("UNO").contains("one"));
        assertTrue(responseHeaders.get("DOS").contains("two"));
        assertTrue(responseHeaders.get("TRES").contains("three"));
        assertFalse(responseHeaders.get("UNO").contains("dos"));
        assertFalse(responseHeaders.get("UNO").contains("tres"));
        TestServer.awaitShutdown();
    }

    @Test
    public void service_sendHeadersQueryParameters_expectCorrectHeaderParamProcessing() throws UnirestException {
        new TestHeaders().configureUsing(TestServer.createNewServer()).start();
        TestServer.awaitStartup();
        Map<String, String> headers = new HashMap<>();
        headers.put("UNO", "one");
        headers.put("DOS", "two");
        headers.put("TRES", "three");
        assertTrue(TestServer.validResponseCode("/testheaderpathqueryparams/one/two/three?one=uno&two=dos&three=tres",
                headers, HttpURLConnection.HTTP_OK));
        assertTrue(TestServer.validResponseCode("/testheaderpathqueryparams/one/two/three/?one=uno&two=dos&three=tres",
                headers, HttpURLConnection.HTTP_OK));
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
