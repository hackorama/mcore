package com.hackorama.mcore.service;

import static org.junit.Assert.*;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
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

import com.mashape.unirest.http.HttpResponse;
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
            GET("/test/header/path/query/params/{one}/{two}/{three}", this::getHeadersPathQueryParams);
            GET("/test/header/path/query/params/{one}/{two}/{three}/", this::getHeadersPathQueryParams);
            GET("/test/response/headers", this::getResponseHeaders);
            GET("/test/multi/value/request/headers", this::getMultiValueRequestHeaders);
            GET("/test/multi/value/separate/request/headers", this::getMultiValueSeparateRequestHeaders);
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

        public Response getMultiValueRequestHeaders(Request request) {
            List<String> values = Arrays.asList(request.getHeaders().get("UNO").get(0).split(","));
            boolean result = request.getHeaders().get("UNO").size() == 1;
            result &= values.size() == 1;
            result &= values.contains("one");
            result &= request.getHeaders().get("DOS").size() == 1;

            values = Arrays.asList(request.getHeaders().get("DOS").get(0).split(","));
            result &= request.getHeaders().get("DOS").size() == 1;
            result &= values.size() == 2;
            result &= values.contains("two_1");
            result &= values.contains("two_2");

            values = Arrays.asList(request.getHeaders().get("TRES").get(0).split(","));
            result &= request.getHeaders().get("TRES").size() == 1;
            result &= values.size() == 3;
            result &= values.contains("three_1");
            result &= values.contains("three_2");
            result &= values.contains("three_3");
            return new Response("QUERYOK", result ? HttpURLConnection.HTTP_OK : HttpURLConnection.HTTP_BAD_REQUEST);
        }

        public Response getMultiValueSeparateRequestHeaders(Request request) {
            boolean result = request.getHeaders().get("UNO").size() == 1;
            result &= request.getHeaders().get("UNO").contains("one");
            result &= request.getHeaders().get("DOS").size() == 2;
            result &= request.getHeaders().get("DOS").contains("two_1");
            result &= request.getHeaders().get("DOS").contains("two_2");
            result &= request.getHeaders().get("TRES").size() == 3;
            result &= request.getHeaders().get("TRES").contains("three_1");
            result &= request.getHeaders().get("TRES").contains("three_2");
            result &= request.getHeaders().get("TRES").contains("three_3");
            return new Response("QUERYOK", result ? HttpURLConnection.HTTP_OK : HttpURLConnection.HTTP_BAD_REQUEST);
        }

        public Response getResponseHeaders(Request request) {
            Response response = new Response("OK");
            Map<String, List<String>> responseHeaders = request.getHeaders();
            Map<String, List<String>> requestHeaders = request.getHeaders();
            requestHeaders.forEach((k, v) -> {
                List<String> values = new ArrayList<>();
                v.forEach(e -> {
                    values.add(k + "_" + e);
                });
                responseHeaders.put(k, values);
            });
            response.setHeaders(responseHeaders);
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
    public void service_sendHeaders_expectResponseToEchoTheHeaders() throws UnirestException, InterruptedException {
        new TestHeaders().configureUsing(TestServer.createNewServer()).start();
        TestServer.awaitStartup();
        Map<String, String> headers = new HashMap<>();
        headers.put("UNO", "one");
        headers.put("DOS", "two");
        headers.put("TRES", "three");
        Map<String, List<String>> responseHeaders = TestServer.getResponseHeaders("/test/response/headers", headers);
        assertTrue(responseHeaders.get("UNO").contains("UNO_one"));
        assertTrue(responseHeaders.get("DOS").contains("DOS_two"));
        assertTrue(responseHeaders.get("TRES").contains("TRES_three"));
        assertFalse(responseHeaders.get("UNO").contains("one"));
        assertFalse(responseHeaders.get("UNO").contains("dos"));
        assertFalse(responseHeaders.get("UNO").contains("UNO"));
        assertFalse(responseHeaders.get("UNO").contains("DOS"));
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
        assertTrue(
                TestServer.validResponseCode("/test/header/path/query/params/one/two/three?one=uno&two=dos&three=tres",
                        headers, HttpURLConnection.HTTP_OK));
        assertTrue(
                TestServer.validResponseCode("/test/header/path/query/params/one/two/three/?one=uno&two=dos&three=tres",
                        headers, HttpURLConnection.HTTP_OK));
        TestServer.awaitShutdown();
    }

    @Test
    public void service_sendMultiValueHeaders_expectCorrectHeaderParamProcessing() throws UnirestException {
        new TestHeaders().configureUsing(TestServer.createNewServer()).start();
        TestServer.awaitStartup();
        Map<String, String> headers = new HashMap<>();
        headers.put("UNO", "one");
        headers.put("DOS", "two_1,two_2");
        headers.put("TRES", "three_1,three_2,three_3");
        assertTrue(
                TestServer.validResponseCode("/test/multi/value/request/headers", headers, HttpURLConnection.HTTP_OK));
        TestServer.awaitShutdown();
    }

    @Test
    public void service_sendMultiValueHeaders_expectResponseToEchoTheHeaders()
            throws UnirestException, InterruptedException {
        new TestHeaders().configureUsing(TestServer.createNewServer()).start();
        TestServer.awaitStartup();
        HttpResponse<String> response = Unirest.get(TestServer.getEndPoint() + "/test/response/headers")
                .header("UNO", "one").header("DOS", "two_1").header("DOS", "two_2").header("TRES", "three_1")
                .header("TRES", "three_2").header("TRES", "three_3").asString();
        Map<String, List<String>> responseHeaders = response.getHeaders();
        assertEquals(1, responseHeaders.get("UNO").size());
        assertTrue(responseHeaders.get("UNO").contains("UNO_one"));
        assertEquals(2, responseHeaders.get("DOS").size());
        assertTrue(responseHeaders.get("DOS").contains("DOS_two_1"));
        assertTrue(responseHeaders.get("DOS").contains("DOS_two_2"));
        assertEquals(3, responseHeaders.get("TRES").size());
        assertTrue(responseHeaders.get("TRES").contains("TRES_three_1"));
        assertTrue(responseHeaders.get("TRES").contains("TRES_three_2"));
        assertTrue(responseHeaders.get("TRES").contains("TRES_three_3"));
        assertFalse(responseHeaders.get("UNO").contains("one"));
        assertFalse(responseHeaders.get("UNO").contains("dos"));
        assertFalse(responseHeaders.get("UNO").contains("UNO"));
        TestServer.awaitShutdown();
    }

    @Test
    public void service_sendMultiValueSeparateHeaders_expectCorrectHeaderParamProcessing() throws UnirestException {
        new TestHeaders().configureUsing(TestServer.createNewServer()).start();
        TestServer.awaitStartup();
        assertEquals(
                Unirest.get(TestServer.getEndPoint() + "/test/multi/value/separate/request/headers")
                        .header("UNO", "one").header("DOS", "two_1").header("DOS", "two_2").header("TRES", "three_1")
                        .header("TRES", "three_2").header("TRES", "three_3").asString().getStatus(),
                HttpURLConnection.HTTP_OK);
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
