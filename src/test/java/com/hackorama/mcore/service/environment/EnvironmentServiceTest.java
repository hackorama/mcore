package com.hackorama.mcore.service.environment;


import static org.junit.Assert.assertEquals;

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
 * Tests for Environment service
 *
 * @author Kishan Thomas (kishan.thomas@gmail.com)
 *
 */
public class EnvironmentServiceTest {

    private static final String DEFAULT_SERVER_ENDPOINT =  TestUtil.defaultServerEndpoint();

    @Before
    public void setUp() throws Exception {
        TestUtil.initEnvServiceInstance();
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
    public void environmentService_postingMultiple_expectsSameOnGetAll() throws UnirestException {
        HttpResponse<JsonNode> jsonResponse;
        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/environment")
                  .header("accept", "application/json")
                  .body("{ \"name\" : \"one\" }")
                  .asJson();
        assertEquals("one", jsonResponse.getBody().getObject().getString("name"));
        String idOne = jsonResponse.getBody().getObject().getString("id");

        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/environment")
                  .header("accept", "application/json")
                  .body("{ \"name\" : \"two\" }")
                  .asJson();
        assertEquals("two", jsonResponse.getBody().getObject().getString("name"));
        String idTwo = jsonResponse.getBody().getObject().getString("id");

        jsonResponse = Unirest.get(DEFAULT_SERVER_ENDPOINT + "/environment").asJson();
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
    public void environmentService_getEnvironment_expectsMatchingEnvironment() throws UnirestException {
        HttpResponse<JsonNode> jsonResponse;
        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/environment")
                  .header("accept", "application/json")
                  .body("{ \"name\" : \"one\" }")
                  .asJson();
        assertEquals("one", jsonResponse.getBody().getObject().getString("name"));
        String idOne = jsonResponse.getBody().getObject().getString("id");

        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/environment")
                  .header("accept", "application/json")
                  .body("{ \"name\" : \"two\" }")
                  .asJson();
        assertEquals("two", jsonResponse.getBody().getObject().getString("name"));
        String idTwo = jsonResponse.getBody().getObject().getString("id");

        jsonResponse = Unirest.get(DEFAULT_SERVER_ENDPOINT + "/environment/" + idOne).asJson();
        assertEquals("one", jsonResponse.getBody().getObject().getString("name"));
        assertEquals(idOne, jsonResponse.getBody().getObject().getString("id"));

        jsonResponse = Unirest.get(DEFAULT_SERVER_ENDPOINT + "/environment/" + idTwo).asJson();
        assertEquals("two", jsonResponse.getBody().getObject().getString("name"));
        assertEquals(idTwo, jsonResponse.getBody().getObject().getString("id"));
    }

    @Test
    public void environmentService_deleteEnvironment_expectsEnvironmentRemoved() throws UnirestException {
        HttpResponse<JsonNode> jsonResponse;
        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/environment")
                  .header("accept", "application/json")
                  .body("{ \"name\" : \"one\" }")
                  .asJson();
        assertEquals("one", jsonResponse.getBody().getObject().getString("name"));
        String idOne = jsonResponse.getBody().getObject().getString("id");

        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/environment")
                  .header("accept", "application/json")
                  .body("{ \"name\" : \"two\" }")
                  .asJson();
        assertEquals("two", jsonResponse.getBody().getObject().getString("name"));
        String idTwo = jsonResponse.getBody().getObject().getString("id");

        jsonResponse = Unirest.get(DEFAULT_SERVER_ENDPOINT + "/environment/" + idOne).asJson();
        assertEquals("one", jsonResponse.getBody().getObject().getString("name"));
        assertEquals(idOne, jsonResponse.getBody().getObject().getString("id"));

        jsonResponse = Unirest.get(DEFAULT_SERVER_ENDPOINT + "/environment/" + idTwo).asJson();
        assertEquals("two", jsonResponse.getBody().getObject().getString("name"));
        assertEquals(idTwo, jsonResponse.getBody().getObject().getString("id"));

        jsonResponse = Unirest.delete(DEFAULT_SERVER_ENDPOINT + "/environment/" + idOne).asJson();

        jsonResponse = Unirest.get(DEFAULT_SERVER_ENDPOINT + "/environment").asJson();
        assertEquals("two", jsonResponse.getBody().getArray().getJSONObject(0).getString("name"));
        assertEquals(idTwo, jsonResponse.getBody().getArray().getJSONObject(0).getString("id"));
    }

    @Test
    public void environmentService_updateEnvironment_expectsUpdatedEnvironment() throws UnirestException {
        HttpResponse<JsonNode> jsonResponse;
        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/environment")
                  .header("accept", "application/json")
                  .body("{ \"name\" : \"one\" }")
                  .asJson();
        assertEquals("one", jsonResponse.getBody().getObject().getString("name"));

        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/environment")
                  .header("accept", "application/json")
                  .body("{ \"name\" : \"two\" }")
                  .asJson();
        assertEquals("two", jsonResponse.getBody().getObject().getString("name"));
        String idTwo = jsonResponse.getBody().getObject().getString("id");

        jsonResponse = Unirest.put(DEFAULT_SERVER_ENDPOINT + "/environment/" + idTwo)
                  .header("accept", "application/json")
                  .body("{ \"name\" : \"twentytwo\" }")
                  .asJson();
        assertEquals("twentytwo", jsonResponse.getBody().getObject().getString("name"));
        assertEquals(idTwo, jsonResponse.getBody().getObject().getString("id"));

        jsonResponse = Unirest.get(DEFAULT_SERVER_ENDPOINT + "/environment/" + idTwo).asJson();
        assertEquals("twentytwo", jsonResponse.getBody().getObject().getString("name"));
        assertEquals(idTwo, jsonResponse.getBody().getObject().getString("id"));
    }

}
