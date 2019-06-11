package com.hackorama.mcore.service;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import com.hackorama.mcore.common.TestServer;
import com.hackorama.mcore.common.TestService;

/**
 * Tests for User service
 *
 * @author Kishan Thomas (kishan.thomas@gmail.com)
 *
 */
@RunWith(Parameterized.class)
public class UserServiceTest {

    private static final String DEFAULT_SERVER_ENDPOINT = TestService.defaultServerEndpoint();

    @AfterClass
    public static void afterAllTests() throws Exception {
        TestService.stopServiceInstance();
    }

    @Parameters
    public static Iterable<? extends Object> data() {
        return TestServer.getServerTypeList();
    }

    public UserServiceTest(String serverType) {
        TestServer.setServerType(serverType);
    }

    @Before
    public void setUp() throws Exception {
        System.out.println("Testing with server type: " + TestServer.getServerType());
        TestService.initServiceInstance(new UserService()); // TODO Move to BeforeClass ?
    }

    @After
    public void tearDown() throws Exception {
        TestService.clearDataOfServiceInstance();
    }

    @Test
    public void userService_deleteUser_expectsUserRemoved() throws UnirestException {
        HttpResponse<JsonNode> jsonResponse;
        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/user").header("accept", "application/json")
                .body("{ \"name\" : \"one\", \"email\" : \"one@example.com\" }").asJson();
        assertEquals("one", jsonResponse.getBody().getObject().getString("name"));
        assertEquals("one@example.com", jsonResponse.getBody().getObject().getString("email"));
        String idOne = jsonResponse.getBody().getObject().getString("id");

        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/user").header("accept", "application/json")
                .body("{ \"name\" : \"two\", \"email\" : \"two@example.com\" }").asJson();
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
    public void userService_getUser_expectsMatchingGroup() throws UnirestException {
        HttpResponse<JsonNode> jsonResponse;
        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/user").header("accept", "application/json")
                .body("{ \"name\" : \"one\", \"email\" : \"one@example.com\" }").asJson();
        assertEquals("one", jsonResponse.getBody().getObject().getString("name"));
        assertEquals("one@example.com", jsonResponse.getBody().getObject().getString("email"));
        String idOne = jsonResponse.getBody().getObject().getString("id");

        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/user").header("accept", "application/json")
                .body("{ \"name\" : \"two\", \"email\" : \"two@example.com\" }").asJson();
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
    public void userService_postingMultiple_expectsSameOnGetAll() throws UnirestException {
        HttpResponse<JsonNode> jsonResponse;
        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/user").header("accept", "application/json")
                .body("{ \"name\" : \"one\", \"email\" : \"one@example.com\" }").asJson();
        assertEquals("one", jsonResponse.getBody().getObject().getString("name"));
        assertEquals("one@example.com", jsonResponse.getBody().getObject().getString("email"));
        String idOne = jsonResponse.getBody().getObject().getString("id");

        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/user").header("accept", "application/json")
                .body("{ \"name\" : \"two\", \"email\" : \"two@example.com\" }").asJson();
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
    public void userService_updateUser_expectsUpdatedUser() throws UnirestException {
        HttpResponse<JsonNode> jsonResponse;
        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/user").header("accept", "application/json")
                .body("{ \"name\" : \"one\", \"email\" : \"one@example.com\" }").asJson();
        assertEquals("one", jsonResponse.getBody().getObject().getString("name"));
        assertEquals("one@example.com", jsonResponse.getBody().getObject().getString("email"));

        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/user").header("accept", "application/json")
                .body("{ \"name\" : \"two\", \"email\" : \"two@example.com\" }").asJson();
        assertEquals("two", jsonResponse.getBody().getObject().getString("name"));
        assertEquals("two@example.com", jsonResponse.getBody().getObject().getString("email"));
        String idTwo = jsonResponse.getBody().getObject().getString("id");

        jsonResponse = Unirest.put(DEFAULT_SERVER_ENDPOINT + "/user/" + idTwo).header("accept", "application/json")
                .body("{ \"name\" : \"twentytwo\", \"email\" : \"two@example.com\" }").asJson();
        assertEquals("twentytwo", jsonResponse.getBody().getObject().getString("name"));
        assertEquals("two@example.com", jsonResponse.getBody().getObject().getString("email"));
        assertEquals(idTwo, jsonResponse.getBody().getObject().getString("id"));

        jsonResponse = Unirest.get(DEFAULT_SERVER_ENDPOINT + "/user/" + idTwo).asJson();
        assertEquals("twentytwo", jsonResponse.getBody().getObject().getString("name"));
        assertEquals("two@example.com", jsonResponse.getBody().getObject().getString("email"));
        assertEquals(idTwo, jsonResponse.getBody().getObject().getString("id"));
    }
}
