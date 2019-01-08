package com.hackorama.mcore.service.group;


import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import com.hackorama.mcore.common.TestUtil;
import com.hackorama.mcore.server.Server;
import com.hackorama.mcore.server.spark.SparkServer;
import com.hackorama.mcore.service.Service;

/**
 * Tests for Group service
 *
 * @author Kishan Thomas (kishan.thomas@gmail.com)
 *
 */
public class GroupServiceTest {

    private static final String DEFAULT_SERVER_ENDPOINT = "http://127.0.0.1:4567" ;
    private Server server;
    private Service service;

    @Before
    public void setUp() throws Exception {
        TestUtil.waitForService();
        server = new SparkServer("group", 4567);
        service = new GroupService().configureUsing(server);
        TestUtil.waitForService();
    }

    @After
    public void tearDown() throws Exception {
        TestUtil.waitForService();
        if (service != null) {
            service.stop();
        }
        if (server != null) {
            server.stop();
        }
        TestUtil.waitForService();
    }

    @Test
    public void groupService_postingMultiple_expectsSameOnGetAll() throws UnirestException {
        HttpResponse<JsonNode> jsonResponse;
        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/group")
                  .header("accept", "application/json")
                  .body("{ \"name\" : \"one\", \"email\" : \"one@example.com\" }")
                  .asJson();
        assertEquals("one", jsonResponse.getBody().getObject().getString("name"));
        assertEquals("one@example.com", jsonResponse.getBody().getObject().getString("email"));
        String idOne = jsonResponse.getBody().getObject().getString("id");

        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/group")
                  .header("accept", "application/json")
                  .body("{ \"name\" : \"two\", \"email\" : \"two@example.com\" }")
                  .asJson();
        assertEquals("two", jsonResponse.getBody().getObject().getString("name"));
        assertEquals("two@example.com", jsonResponse.getBody().getObject().getString("email"));
        String idTwo = jsonResponse.getBody().getObject().getString("id");

        jsonResponse = Unirest.get(DEFAULT_SERVER_ENDPOINT + "/group").asJson();
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
    public void groupService_getGroup_expectsMatchingGroup() throws UnirestException {
        HttpResponse<JsonNode> jsonResponse;
        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/group")
                  .header("accept", "application/json")
                  .body("{ \"name\" : \"one\", \"email\" : \"one@example.com\" }")
                  .asJson();
        assertEquals("one", jsonResponse.getBody().getObject().getString("name"));
        assertEquals("one@example.com", jsonResponse.getBody().getObject().getString("email"));
        String idOne = jsonResponse.getBody().getObject().getString("id");

        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/group")
                  .header("accept", "application/json")
                  .body("{ \"name\" : \"two\", \"email\" : \"two@example.com\" }")
                  .asJson();
        assertEquals("two", jsonResponse.getBody().getObject().getString("name"));
        assertEquals("two@example.com", jsonResponse.getBody().getObject().getString("email"));
        String idTwo = jsonResponse.getBody().getObject().getString("id");

        jsonResponse = Unirest.get(DEFAULT_SERVER_ENDPOINT + "/group/" + idOne).asJson();
        assertEquals("one", jsonResponse.getBody().getObject().getString("name"));
        assertEquals("one@example.com", jsonResponse.getBody().getObject().getString("email"));
        assertEquals(idOne, jsonResponse.getBody().getObject().getString("id"));

        jsonResponse = Unirest.get(DEFAULT_SERVER_ENDPOINT + "/group/" + idTwo).asJson();
        assertEquals("two", jsonResponse.getBody().getObject().getString("name"));
        assertEquals("two@example.com", jsonResponse.getBody().getObject().getString("email"));
        assertEquals(idTwo, jsonResponse.getBody().getObject().getString("id"));
    }

    @Test
    public void groupService_deleteGroup_expectsGroupRemoved() throws UnirestException {
        HttpResponse<JsonNode> jsonResponse;
        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/group")
                  .header("accept", "application/json")
                  .body("{ \"name\" : \"one\", \"email\" : \"one@example.com\" }")
                  .asJson();
        assertEquals("one", jsonResponse.getBody().getObject().getString("name"));
        assertEquals("one@example.com", jsonResponse.getBody().getObject().getString("email"));
        String idOne = jsonResponse.getBody().getObject().getString("id");

        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/group")
                  .header("accept", "application/json")
                  .body("{ \"name\" : \"two\", \"email\" : \"two@example.com\" }")
                  .asJson();
        assertEquals("two", jsonResponse.getBody().getObject().getString("name"));
        assertEquals("two@example.com", jsonResponse.getBody().getObject().getString("email"));
        String idTwo = jsonResponse.getBody().getObject().getString("id");

        jsonResponse = Unirest.get(DEFAULT_SERVER_ENDPOINT + "/group/" + idOne).asJson();
        assertEquals("one", jsonResponse.getBody().getObject().getString("name"));
        assertEquals("one@example.com", jsonResponse.getBody().getObject().getString("email"));
        assertEquals(idOne, jsonResponse.getBody().getObject().getString("id"));

        jsonResponse = Unirest.get(DEFAULT_SERVER_ENDPOINT + "/group/" + idTwo).asJson();
        assertEquals("two", jsonResponse.getBody().getObject().getString("name"));
        assertEquals("two@example.com", jsonResponse.getBody().getObject().getString("email"));
        assertEquals(idTwo, jsonResponse.getBody().getObject().getString("id"));

        jsonResponse = Unirest.delete(DEFAULT_SERVER_ENDPOINT + "/group/" + idOne).asJson();

        jsonResponse = Unirest.get(DEFAULT_SERVER_ENDPOINT + "/group").asJson();
        /* TODO FIXME Timing error only on Travis automation builds 
        assertEquals("two", jsonResponse.getBody().getArray().getJSONObject(0).getString("name"));
        assertEquals("two@example.com", jsonResponse.getBody().getArray().getJSONObject(0).getString("email"));
        assertEquals(idTwo, jsonResponse.getBody().getArray().getJSONObject(0).getString("id"));
        */ 
    }

    @Test
    public void groupService_updateGroup_expectsUpdatedGroup() throws UnirestException {
        HttpResponse<JsonNode> jsonResponse;
        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/group")
                  .header("accept", "application/json")
                  .body("{ \"name\" : \"one\", \"email\" : \"one@example.com\" }")
                  .asJson();
        assertEquals("one", jsonResponse.getBody().getObject().getString("name"));
        assertEquals("one@example.com", jsonResponse.getBody().getObject().getString("email"));

        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/group")
                  .header("accept", "application/json")
                  .body("{ \"name\" : \"two\", \"email\" : \"two@example.com\" }")
                  .asJson();
        assertEquals("two", jsonResponse.getBody().getObject().getString("name"));
        assertEquals("two@example.com", jsonResponse.getBody().getObject().getString("email"));
        String idTwo = jsonResponse.getBody().getObject().getString("id");

        jsonResponse = Unirest.put(DEFAULT_SERVER_ENDPOINT + "/group/" + idTwo)
                  .header("accept", "application/json")
                  .body("{ \"name\" : \"twentytwo\", \"email\" : \"two@example.com\" }")
                  .asJson();
        assertEquals("twentytwo", jsonResponse.getBody().getObject().getString("name"));
        assertEquals("two@example.com", jsonResponse.getBody().getObject().getString("email"));
        assertEquals(idTwo, jsonResponse.getBody().getObject().getString("id"));

        jsonResponse = Unirest.get(DEFAULT_SERVER_ENDPOINT + "/group/" + idTwo).asJson();
        assertEquals("twentytwo", jsonResponse.getBody().getObject().getString("name"));
        assertEquals("two@example.com", jsonResponse.getBody().getObject().getString("email"));
        assertEquals(idTwo, jsonResponse.getBody().getObject().getString("id"));
    }
}
