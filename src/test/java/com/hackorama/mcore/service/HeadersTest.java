package com.hackorama.mcore.service;

import static org.junit.Assert.*;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

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
public class HeadersTest {

    private static class TestHeaders extends BaseService {

        @Override
        public void configure() {
            GET("/testheaderpathqueryparams/{one}/{two}/{three}", this::getHeadersPathQueryParams);
            GET("/testheaderpathqueryparams/{one}/{two}/{three}/", this::getHeadersPathQueryParams);
        }

        public Response getHeadersPathQueryParams(Request request) {
            boolean result = "one".equals(request.getHeaders().get("UNO"));
            result &= "two".equals(request.getHeaders().get("DOS"));
            result &= "three".equals(request.getHeaders().get("TRES"));
            result &= request.getQueryParams().get("one").contains("uno");
            result &= request.getQueryParams().get("two").contains("dos");
            result &= request.getQueryParams().get("three").contains("tres");
            result &= "one".equals(request.getPathParams().get("one"));
            result &= "two".equals(request.getPathParams().get("two"));
            result &= "three".equals(request.getPathParams().get("three"));
            return new Response("QUERYOK", result ? HttpURLConnection.HTTP_OK : HttpURLConnection.HTTP_BAD_REQUEST);
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
