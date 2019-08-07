package com.hackorama.mcore.service;

import static org.junit.Assert.*;

import java.net.HttpURLConnection;

import org.junit.Test;

import com.mashape.unirest.http.exceptions.UnirestException;

import com.hackorama.mcore.common.Debug;
import com.hackorama.mcore.common.TestServer;
import com.hackorama.mcore.http.Request;
import com.hackorama.mcore.http.Response;
import com.hackorama.mcore.server.ServerTest;

public class ParamsTest extends ServerTest {

    private static class TestParamsService extends Service {

        @Override
        public void configure() {
            GET("/testpathparams/{one}/{two}/{three}", this::getPathParams);
            GET("/testpathparams/{one}/{two}/{three}/", this::getPathParams);
            GET("/testqueryparams", this::getQueryParams);
            GET("/testqueryparams/", this::getQueryParams);
            GET("/testpathqueryparams/{one}/{two}/{three}", this::getPathQueryParams);
            GET("/testpathqueryparams/{one}/{two}/{three}/", this::getPathQueryParams);
        }

        public Response getPathParams(Request request) {
            boolean result = "one".equals(request.getPathParams().get("one"));
            result &= "two".equals(request.getPathParams().get("two"));
            result &= "three".equals(request.getPathParams().get("three"));
            return new Response("PATHOK", result ? HttpURLConnection.HTTP_OK : HttpURLConnection.HTTP_BAD_REQUEST);
        }

        public Response getPathQueryParams(Request request) {
            Debug.print(request);
            boolean result = request.getQueryParams().get("one").contains("uno");
            result &= request.getQueryParams().get("two").contains("dos");
            result &= request.getQueryParams().get("three").contains("tres");
            result &= "one".equals(request.getPathParams().get("one"));
            result &= "two".equals(request.getPathParams().get("two"));
            result &= "three".equals(request.getPathParams().get("three"));
            Response response = new Response("QUERYOK", result ? HttpURLConnection.HTTP_OK : HttpURLConnection.HTTP_BAD_REQUEST);
            Debug.print(response);
            return response;
        }

        public Response getQueryParams(Request request) {
            boolean result = request.getQueryParams().get("one").contains("uno");
            result &= request.getQueryParams().get("two").contains("dos");
            result &= request.getQueryParams().get("three").contains("tres");
            return new Response("QUERYOK", result ? HttpURLConnection.HTTP_OK : HttpURLConnection.HTTP_BAD_REQUEST);
        }
    }

    public ParamsTest(String serverType) {
        super(serverType);
        Debug.disable();
    }

    @Test
    public void service_sendPathAndQueryParameters_expectCorrectParamProcessing() throws UnirestException {
        assertTrue(TestServer.validResponseCode("/testpathqueryparams/one/two/three?one=uno&two=dos&three=tres",
                HttpURLConnection.HTTP_OK));
        assertTrue(TestServer.validResponseCode("/testpathqueryparams/one/two/three/?one=uno&two=dos&three=tres",
                HttpURLConnection.HTTP_OK));
    }

    @Test
    public void service_sendPathParameters_expectCorrectParamProcessing() throws UnirestException {
        assertTrue(TestServer.validResponseCode("/testpathparams/one/two/three", HttpURLConnection.HTTP_OK));
        assertTrue(TestServer.validResponseCode("/testpathparams/one/two/three/", HttpURLConnection.HTTP_OK));
        assertFalse(TestServer.validResponseCode("/testpathparams/one/two/three/x", HttpURLConnection.HTTP_OK));
        assertFalse(TestServer.validResponseCode("/testpathparams/two/three", HttpURLConnection.HTTP_OK));
        assertFalse(TestServer.validResponseCode("/testpathparams/two/three/one", HttpURLConnection.HTTP_OK));
        assertFalse(TestServer.validResponseCode("/testpathparams/three/one/two", HttpURLConnection.HTTP_OK));
    }

    @Test
    public void service_sendQueryParameters_expectCorrectParamProcessing() throws UnirestException {
        assertTrue(
                TestServer.validResponseCode("/testqueryparams?one=uno&two=dos&three=tres", HttpURLConnection.HTTP_OK));
        assertTrue(TestServer.validResponseCode("/testqueryparams/?one=uno&two=dos&three=tres",
                HttpURLConnection.HTTP_OK));
    }

    @Override
    protected Service useDefaultService() {
        return new TestParamsService();
    }

}
