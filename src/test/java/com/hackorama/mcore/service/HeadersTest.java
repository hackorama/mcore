package com.hackorama.mcore.service;

import static org.junit.Assert.*;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runners.Parameterized.Parameters;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import com.hackorama.mcore.common.Request;
import com.hackorama.mcore.common.Response;
import com.hackorama.mcore.common.TestServer;
import com.hackorama.mcore.server.ServerTest;

public class HeadersTest extends ServerTest {

    private static class TestHeadersService extends BaseService {

        @Override
        public void configure() {
            GET("/test/headers/request", this::getHeadersRequest);
            GET("/test/headers/response", this::getHeadersResponse);
            GET("/test/multi/value/request/headers", this::getMultipleValueRequestHeaders);
            GET("/test/multi/value/separate/request/headers", this::getMultipleValueSeparateRequestHeaders);
        }

        public Response getHeadersRequest(Request request) {
            boolean result = "one".equals(request.getHeaders().get("UNO").get(0));
            result &= "two".equals(request.getHeaders().get("DOS").get(0));
            result &= "three".equals(request.getHeaders().get("TRES").get(0));
            return new Response("HEADERS_REQUEST",
                    result ? HttpURLConnection.HTTP_OK : HttpURLConnection.HTTP_BAD_REQUEST);
        }

        public Response getHeadersResponse(Request request) {
            Response response = new Response("HEADER_RESPONSE");
            Map<String, List<String>> responseHeaders = new HashMap<>();
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

        public Response getMultipleValueRequestHeaders(Request request) {
            List<String> values = Arrays.asList(request.getHeaders().get("ONLY").get(0).split(","));
            boolean result = request.getHeaders().get("ONLY").size() == 1;
            result &= values.size() == 1;
            result &= values.contains("ONE");

            values = Arrays.asList(request.getHeaders().get("MANY").get(0).split(","));
            result &= request.getHeaders().get("MANY").size() == 1;
            result &= values.size() == 3;
            result &= values.contains("FIRST");
            result &= values.contains("SECOND");
            result &= values.contains("LAST");

            values = Arrays.asList(request.getHeaders().get("DUPLICATE").get(0).split(","));
            result &= request.getHeaders().get("DUPLICATE").size() == 1;
            result &= values.size() == 2;
            result &= values.get(0).equals(values.get(1));
            result &= values.contains("SAME");
            return new Response("MULTIPLE_HEADERS_REQUEST",
                    result ? HttpURLConnection.HTTP_OK : HttpURLConnection.HTTP_BAD_REQUEST);
        }

        public Response getMultipleValueSeparateRequestHeaders(Request request) {
            boolean result = request.getHeaders().get("ONLY").size() == 1;
            result &= request.getHeaders().get("ONLY").contains("ONE");
            result &= request.getHeaders().get("MANY").size() == 3;
            result &= request.getHeaders().get("MANY").contains("FIRST");
            result &= request.getHeaders().get("MANY").contains("SECOND");
            result &= request.getHeaders().get("MANY").contains("LAST");
            result &= request.getHeaders().get("DUPLICATE").size() == 2;
            result &= request.getHeaders().get("DUPLICATE").contains("SAME");
            result &= request.getHeaders().get("DUPLICATE").get(0).equals(request.getHeaders().get("DUPLICATE").get(1));
            return new Response("MULTIPLE_HEADERS_REQUEST",
                    result ? HttpURLConnection.HTTP_OK : HttpURLConnection.HTTP_BAD_REQUEST);
        }
    }

    @Parameters
    public static Iterable<? extends Object> data() {
        return TestServer.getServerTypeList();
    }

    public HeadersTest(String serverType) {
        super(serverType);
    }

    @Test
    public void service_sendHeaders_expectCorrectHeadersInRequest() throws UnirestException {
        Map<String, String> headers = new HashMap<>();
        headers.put("UNO", "one");
        headers.put("DOS", "two");
        headers.put("TRES", "three");
        assertTrue(TestServer.validResponseCode("/test/headers/request", headers, HttpURLConnection.HTTP_OK));
    }

    @Test
    public void service_sendHeaders_expectResponseToEchoTheHeadersFromRequest()
            throws UnirestException, InterruptedException {
        Map<String, String> headers = new HashMap<>();
        headers.put("UNO", "one");
        headers.put("DOS", "two");
        headers.put("TRES", "three");
        Map<String, List<String>> responseHeaders = TestServer.getResponseHeaders("/test/headers/response", headers);
        assertTrue(responseHeaders.get("UNO").contains("UNO_one"));
        assertTrue(responseHeaders.get("DOS").contains("DOS_two"));
        assertTrue(responseHeaders.get("TRES").contains("TRES_three"));
        assertFalse(responseHeaders.get("UNO").contains("one"));
        assertFalse(responseHeaders.get("UNO").contains("dos"));
        assertFalse(responseHeaders.get("UNO").contains("UNO"));
        assertFalse(responseHeaders.get("UNO").contains("DOS"));
    }

    @Test
    public void service_sendMultipleValueHeaders_expectCorrectHeadersInRequest() throws UnirestException {
        Map<String, String> headers = new HashMap<>();
        headers.put("ONLY", "ONE");
        headers.put("MANY", "FIRST,SECOND,LAST");
        headers.put("DUPLICATE", "SAME,SAME");
        assertTrue(
                TestServer.validResponseCode("/test/multi/value/request/headers", headers, HttpURLConnection.HTTP_OK));
    }

    @Test
    public void service_sendMultipleValueHeaders_expectResponseToEchoTheHeaders()
            throws UnirestException, InterruptedException {
        HttpResponse<String> response = Unirest.get(TestServer.getEndPoint() + "/test/headers/response")
                .header("ONLY", "ONE").header("MANY", "FIRST").header("MANY", "SECOND").header("MANY", "LAST")
                .header("DUPLICATE", "SAME").header("DUPLICATE", "SAME").asString();
        Map<String, List<String>> responseHeaders = response.getHeaders();
        assertEquals(1, responseHeaders.get("ONLY").size());
        assertTrue(responseHeaders.get("ONLY").contains("ONLY_ONE"));
        assertEquals(3, responseHeaders.get("MANY").size());
        assertTrue(responseHeaders.get("MANY").contains("MANY_FIRST"));
        assertTrue(responseHeaders.get("MANY").contains("MANY_SECOND"));
        assertTrue(responseHeaders.get("MANY").contains("MANY_LAST"));
        assertEquals(2, responseHeaders.get("DUPLICATE").size());
        assertTrue(responseHeaders.get("DUPLICATE").contains("DUPLICATE_SAME"));
        assertTrue(responseHeaders.get("DUPLICATE").get(0).equals(responseHeaders.get("DUPLICATE").get(1)));
    }

    @Test
    public void service_sendMultipleValueHeadersAsSeparateHeaders_expectCorrectHeadersInRequest()
            throws UnirestException {
        assertEquals(
                Unirest.get(TestServer.getEndPoint() + "/test/multi/value/separate/request/headers")
                        .header("ONLY", "ONE").header("MANY", "FIRST").header("MANY", "SECOND").header("MANY", "LAST")
                        .header("DUPLICATE", "SAME").header("DUPLICATE", "SAME").asString().getStatus(),
                HttpURLConnection.HTTP_OK);
    }

    @Override
    protected Service useDefaultService() {
        return new TestHeadersService();
    }

}
