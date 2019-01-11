package com.hackorama.mcore.service;


import static org.junit.Assert.assertEquals;

import java.net.HttpURLConnection;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import com.hackorama.mcore.common.TestUtil;

/**
 * Tests for Workspace service
 *
 * @author Kishan Thomas (kishan.thomas@gmail.com)
 *
 */
public class CommonServiceTest {

    private static final String DEFAULT_SERVER_ENDPOINT = TestUtil.defaultServerEndpoint();;

    @Before
    public void setUp() throws Exception {
        TestUtil.initServiceInstance();
    }

    @After
    public void tearDown() throws Exception {
        TestUtil.clearDataOfServiceInstance();
    }

    @AfterClass
    public static void afterAllTests() throws Exception {
        TestUtil.stopServiceInstance();
    }

    @Test
    public void workspaceService_getResource_expectsOKStataus() throws UnirestException {
        HttpResponse<JsonNode> response = Unirest.get(DEFAULT_SERVER_ENDPOINT + "/workspace")
                .header("accept", "application/json").asJson();
        assertEquals(HttpURLConnection.HTTP_OK, response.getStatus());
    }

    @Test
    public void workspaceService_postResource_expectsOKStataus() throws UnirestException {
        HttpResponse<JsonNode> response = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/workspace")
                .header("accept", "application/json").body("{ \"name\" : \"one\" }").asJson();
        assertEquals(HttpURLConnection.HTTP_OK, response.getStatus());
    }

    @Test
    public void workspaceService_invalidURL_expectsNotFoundStatus() throws UnirestException {
        HttpResponse<String> response = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/workspace/bad/path")
                .header("accept", "application/json").body("{ \"name\" : \"one\" }").asString();
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, response.getStatus());
    }
}
