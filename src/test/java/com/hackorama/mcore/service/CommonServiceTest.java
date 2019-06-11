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

import com.hackorama.mcore.common.TestService;

/**
 * Common tests for server implementations
 *
 * @author Kishan Thomas (kishan.thomas@gmail.com)
 *
 */
public class CommonServiceTest {

    private static final String DEFAULT_SERVER_ENDPOINT = TestService.defaultServerEndpoint();;

    protected void setServer() {
        TestService.setServerTypeSpark();
    }

    @Before
    public void setUp() throws Exception {
        setServer();
        TestService.initServiceInstance(new UserService());
    }

    @After
    public void tearDown() throws Exception {
        TestService.clearDataOfServiceInstance();
    }

    @AfterClass
    public static void afterAllTests() throws Exception {
        TestService.stopServiceInstance();
        TestService.resetServerType();
    }

    @Test
    public void service_getResource_expectsOKStataus() throws UnirestException {
        HttpResponse<JsonNode> response = Unirest.get(DEFAULT_SERVER_ENDPOINT + "/user")
                .header("accept", "application/json").asJson();
        assertEquals(HttpURLConnection.HTTP_OK, response.getStatus());
    }

    @Test
    public void workspaceService_postResource_expectsOKStataus() throws UnirestException {
        HttpResponse<JsonNode> response = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/user")
                .header("accept", "application/json").body("{ \"name\" : \"one\" }").asJson();
        assertEquals(HttpURLConnection.HTTP_OK, response.getStatus());
    }

    @Test
    public void service_invalidURL_expectsNotFoundStatus() throws UnirestException {
        HttpResponse<String> response = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/user/bad/path")
                .header("accept", "application/json").body("{ \"name\" : \"one\" }").asString();
        assertEquals(HttpURLConnection.HTTP_NOT_FOUND, response.getStatus());
    }

    @Test
    public void service_postingMultiple_expectsSameOnGetAll() throws UnirestException {
        HttpResponse<JsonNode> jsonResponse;
        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/user")
                  .header("accept", "application/json")
                  .body("{ \"name\" : \"one\", \"email\" : \"one@example.com\" }")
                  .asJson();
        assertEquals("one", jsonResponse.getBody().getObject().getString("name"));
        assertEquals("one@example.com", jsonResponse.getBody().getObject().getString("email"));
        String idOne = jsonResponse.getBody().getObject().getString("id");

        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/user")
                  .header("accept", "application/json")
                  .body("{ \"name\" : \"two\", \"email\" : \"two@example.com\" }")
                  .asJson();
        assertEquals("two", jsonResponse.getBody().getObject().getString("name"));
        assertEquals("two@example.com", jsonResponse.getBody().getObject().getString("email"));
        String idTwo = jsonResponse.getBody().getObject().getString("id");

        jsonResponse = Unirest.get(DEFAULT_SERVER_ENDPOINT + "/user").asJson();
        if (idOne.equals(jsonResponse.getBody().getArray().getJSONObject(0).getString("id"))) {
            assertEquals("one", jsonResponse.getBody().getArray().getJSONObject(0).getString("name"));
            assertEquals("two", jsonResponse.getBody().getArray().getJSONObject(1).getString("name"));
            assertEquals(idTwo, jsonResponse.getBody().getArray().getJSONObject(1).getString("id"));
        } else {
            assertEquals("one", jsonResponse.getBody().getArray().getJSONObject(1).getString("name"));
            assertEquals("two", jsonResponse.getBody().getArray().getJSONObject(0).getString("name"));
            assertEquals(idOne, jsonResponse.getBody().getArray().getJSONObject(1).getString("id"));
        }
    }

    @Test
    public void service_getEntity_expectsMatchingEntity() throws UnirestException {
        HttpResponse<JsonNode> jsonResponse;
        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/user")
                  .header("accept", "application/json")
                  .body("{ \"name\" : \"one\", \"email\" : \"one@example.com\" }")
                  .asJson();
        assertEquals("one", jsonResponse.getBody().getObject().getString("name"));
        assertEquals("one@example.com", jsonResponse.getBody().getObject().getString("email"));
        String idOne = jsonResponse.getBody().getObject().getString("id");

        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/user")
                  .header("accept", "application/json")
                  .body("{ \"name\" : \"two\", \"email\" : \"two@example.com\" }")
                  .asJson();
        assertEquals("two", jsonResponse.getBody().getObject().getString("name"));
        assertEquals("two@example.com", jsonResponse.getBody().getObject().getString("email"));
        String idTwo = jsonResponse.getBody().getObject().getString("id");

        jsonResponse = Unirest.get(DEFAULT_SERVER_ENDPOINT + "/user/" + idOne).asJson();
        assertEquals("one", jsonResponse.getBody().getObject().getString("name"));
        assertEquals("one@example.com", jsonResponse.getBody().getObject().getString("email"));
        assertEquals(idOne, jsonResponse.getBody().getObject().getString("id"));

        jsonResponse = Unirest.get(DEFAULT_SERVER_ENDPOINT + "/user/" + idTwo).asJson();
        assertEquals("two", jsonResponse.getBody().getObject().getString("name"));
        assertEquals("two@example.com", jsonResponse.getBody().getObject().getString("email"));
        assertEquals(idTwo, jsonResponse.getBody().getObject().getString("id"));
    }

    @Test
    public void service_deleteEntity_expectsEntityRemoved() throws UnirestException {
        HttpResponse<JsonNode> jsonResponse;
        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/user")
                  .header("accept", "application/json")
                  .body("{ \"name\" : \"one\", \"email\" : \"one@example.com\" }")
                  .asJson();
        assertEquals("one", jsonResponse.getBody().getObject().getString("name"));
        assertEquals("one@example.com", jsonResponse.getBody().getObject().getString("email"));
        String idOne = jsonResponse.getBody().getObject().getString("id");

        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/user")
                  .header("accept", "application/json")
                  .body("{ \"name\" : \"two\", \"email\" : \"two@example.com\" }")
                  .asJson();
        assertEquals("two", jsonResponse.getBody().getObject().getString("name"));
        assertEquals("two@example.com", jsonResponse.getBody().getObject().getString("email"));
        String idTwo = jsonResponse.getBody().getObject().getString("id");

        jsonResponse = Unirest.get(DEFAULT_SERVER_ENDPOINT + "/user/" + idOne).asJson();
        assertEquals("one", jsonResponse.getBody().getObject().getString("name"));
        assertEquals("one@example.com", jsonResponse.getBody().getObject().getString("email"));
        assertEquals(idOne, jsonResponse.getBody().getObject().getString("id"));

        jsonResponse = Unirest.get(DEFAULT_SERVER_ENDPOINT + "/user/" + idTwo).asJson();
        assertEquals("two", jsonResponse.getBody().getObject().getString("name"));
        assertEquals("two@example.com", jsonResponse.getBody().getObject().getString("email"));
        assertEquals(idTwo, jsonResponse.getBody().getObject().getString("id"));

        jsonResponse = Unirest.delete(DEFAULT_SERVER_ENDPOINT + "/user/" + idOne).asJson();

        jsonResponse = Unirest.get(DEFAULT_SERVER_ENDPOINT + "/user").asJson();
        assertEquals("two", jsonResponse.getBody().getArray().getJSONObject(0).getString("name"));
        assertEquals("two@example.com", jsonResponse.getBody().getArray().getJSONObject(0).getString("email"));
        assertEquals(idTwo, jsonResponse.getBody().getArray().getJSONObject(0).getString("id"));
    }

    @Test
    public void service_updateEntity_expectsUpdatedEntity() throws UnirestException {
        HttpResponse<JsonNode> jsonResponse;
        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/user")
                  .header("accept", "application/json")
                  .body("{ \"name\" : \"one\", \"email\" : \"one@example.com\" }")
                  .asJson();
        assertEquals("one", jsonResponse.getBody().getObject().getString("name"));
        assertEquals("one@example.com", jsonResponse.getBody().getObject().getString("email"));

        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/user")
                  .header("accept", "application/json")
                  .body("{ \"name\" : \"two\", \"email\" : \"two@example.com\" }")
                  .asJson();
        assertEquals("two", jsonResponse.getBody().getObject().getString("name"));
        assertEquals("two@example.com", jsonResponse.getBody().getObject().getString("email"));
        String idTwo = jsonResponse.getBody().getObject().getString("id");

        jsonResponse = Unirest.put(DEFAULT_SERVER_ENDPOINT + "/user/" + idTwo)
                  .header("accept", "application/json")
                  .body("{ \"name\" : \"twentytwo\", \"email\" : \"two@example.com\" }")
                  .asJson();
        assertEquals("twentytwo", jsonResponse.getBody().getObject().getString("name"));
        assertEquals("two@example.com", jsonResponse.getBody().getObject().getString("email"));
        assertEquals(idTwo, jsonResponse.getBody().getObject().getString("id"));

        jsonResponse = Unirest.get(DEFAULT_SERVER_ENDPOINT + "/user/" + idTwo).asJson();
        assertEquals("twentytwo", jsonResponse.getBody().getObject().getString("name"));
        assertEquals("two@example.com", jsonResponse.getBody().getObject().getString("email"));
        assertEquals(idTwo, jsonResponse.getBody().getObject().getString("id"));
    }
}
