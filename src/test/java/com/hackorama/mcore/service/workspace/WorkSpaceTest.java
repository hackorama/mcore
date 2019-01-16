package com.hackorama.mcore.service.workspace;

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
 * Tests for Workspace service
 *
 * @author Kishan Thomas (kishan.thomas@gmail.com)
 *
 */
public class WorkSpaceTest {

    private static final String DEFAULT_SERVER_ENDPOINT = TestUtil.defaultServerEndpoint();

    @Before
    public void setUp() throws Exception {
        TestUtil.initWorkSpaceServiceInstance();
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
    public void workspaceService_deleteWorkspace_expectsWorkspaceRemoved() throws UnirestException {
        HttpResponse<JsonNode> jsonResponse;
        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/workspace").header("accept", "application/json")
                .body("{ \"name\" : \"one\" }").asJson();
        assertEquals("one", jsonResponse.getBody().getObject().getString("name"));
        String idOne = jsonResponse.getBody().getObject().getString("id");

        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/workspace").header("accept", "application/json")
                .body("{ \"name\" : \"two\" }").asJson();
        assertEquals("two", jsonResponse.getBody().getObject().getString("name"));
        String idTwo = jsonResponse.getBody().getObject().getString("id");

        jsonResponse = Unirest.get(DEFAULT_SERVER_ENDPOINT + "/workspace/" + idOne).asJson();
        assertEquals("one", jsonResponse.getBody().getObject().getString("name"));
        assertEquals(idOne, jsonResponse.getBody().getObject().getString("id"));

        jsonResponse = Unirest.get(DEFAULT_SERVER_ENDPOINT + "/workspace/" + idTwo).asJson();
        assertEquals("two", jsonResponse.getBody().getObject().getString("name"));
        assertEquals(idTwo, jsonResponse.getBody().getObject().getString("id"));

        jsonResponse = Unirest.delete(DEFAULT_SERVER_ENDPOINT + "/workspace/" + idOne).asJson();

        jsonResponse = Unirest.get(DEFAULT_SERVER_ENDPOINT + "/workspace").asJson();
        assertEquals("two", jsonResponse.getBody().getArray().getJSONObject(0).getString("name"));
        assertEquals(idTwo, jsonResponse.getBody().getArray().getJSONObject(0).getString("id"));
    }

    @Test
    public void workspaceService_getWorkspace_expectsMatchingWorkspace() throws UnirestException {
        HttpResponse<JsonNode> jsonResponse;
        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/workspace").header("accept", "application/json")
                .body("{ \"name\" : \"one\" }").asJson();
        assertEquals("one", jsonResponse.getBody().getObject().getString("name"));
        String idOne = jsonResponse.getBody().getObject().getString("id");

        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/workspace").header("accept", "application/json")
                .body("{ \"name\" : \"two\" }").asJson();
        assertEquals("two", jsonResponse.getBody().getObject().getString("name"));
        String idTwo = jsonResponse.getBody().getObject().getString("id");

        jsonResponse = Unirest.get(DEFAULT_SERVER_ENDPOINT + "/workspace/" + idOne).asJson();
        assertEquals("one", jsonResponse.getBody().getObject().getString("name"));
        assertEquals(idOne, jsonResponse.getBody().getObject().getString("id"));

        jsonResponse = Unirest.get(DEFAULT_SERVER_ENDPOINT + "/workspace/" + idTwo).asJson();
        assertEquals("two", jsonResponse.getBody().getObject().getString("name"));
        assertEquals(idTwo, jsonResponse.getBody().getObject().getString("id"));
    }

    @Test
    public void workspaceService_invalidEnvWithoutService_expectsLinkWillNotBeUpdated() throws UnirestException {
        HttpResponse<JsonNode> jsonResponse;
        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/workspace").header("accept", "application/json")
                .body("{ \"name\" : \"one\" }").asJson();
        assertEquals("one", jsonResponse.getBody().getObject().getString("name"));
        String workspaceId = jsonResponse.getBody().getObject().getString("id");
        String invalidEnvId = "invalid-env-id";
        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/workspace/" + workspaceId + "/environment/" + invalidEnvId)
                .header("accept", "application/json").asJson();
        jsonResponse = Unirest.get(DEFAULT_SERVER_ENDPOINT + "/workspace/" + workspaceId + "/environment")
                .header("accept", "application/json").asJson();
        assertEquals(invalidEnvId, jsonResponse.getBody().getArray().get(0));
    }


    @Test
    public void workspaceService_invalidGroupWithoutService_expectsLinkWillNotBeUpdated() throws UnirestException {
        HttpResponse<JsonNode> jsonResponse;
        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/workspace").header("accept", "application/json")
                .body("{ \"name\" : \"one\" }").asJson();
        assertEquals("one", jsonResponse.getBody().getObject().getString("name"));
        String workspaceId = jsonResponse.getBody().getObject().getString("id");
        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/workspace/" + workspaceId + "/group/invalid-group-id")
                .header("accept", "application/json").asJson();
        boolean notAccepted = jsonResponse.getBody().toString().contains("error");
        jsonResponse = Unirest.get(DEFAULT_SERVER_ENDPOINT + "/workspace/" + workspaceId)
                .header("accept", "application/json").asJson();
        if (notAccepted) {
            assertEquals(0, jsonResponse.getBody().getObject().getJSONArray("owners").length());
        } else {
            assertEquals(1, jsonResponse.getBody().getObject().getJSONArray("owners").length());
        }
    }


    @Test
    public void workspaceService_postingMultiple_expectsSameOnGetAll() throws UnirestException {
        HttpResponse<JsonNode> jsonResponse;
        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/workspace").header("accept", "application/json")
                .body("{ \"name\" : \"one\" }").asJson();
        assertEquals("one", jsonResponse.getBody().getObject().getString("name"));
        String idOne = jsonResponse.getBody().getObject().getString("id");

        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/workspace").header("accept", "application/json")
                .body("{ \"name\" : \"two\" }").asJson();
        assertEquals("two", jsonResponse.getBody().getObject().getString("name"));
        String idTwo = jsonResponse.getBody().getObject().getString("id");

        jsonResponse = Unirest.get(DEFAULT_SERVER_ENDPOINT + "/workspace").asJson();
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
    public void workspaceService_updateWorkspace_expectsUpdatedWorkspace() throws UnirestException {
        HttpResponse<JsonNode> jsonResponse;
        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/workspace").header("accept", "application/json")
                .body("{ \"name\" : \"one\" }").asJson();
        assertEquals("one", jsonResponse.getBody().getObject().getString("name"));

        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/workspace").header("accept", "application/json")
                .body("{ \"name\" : \"two\" }").asJson();
        assertEquals("two", jsonResponse.getBody().getObject().getString("name"));
        String idTwo = jsonResponse.getBody().getObject().getString("id");

        jsonResponse = Unirest.put(DEFAULT_SERVER_ENDPOINT + "/workspace/" + idTwo).header("accept", "application/json")
                .body("{ \"name\" : \"twentytwo\" }").asJson();
        assertEquals("twentytwo", jsonResponse.getBody().getObject().getString("name"));
        assertEquals(idTwo, jsonResponse.getBody().getObject().getString("id"));

        jsonResponse = Unirest.get(DEFAULT_SERVER_ENDPOINT + "/workspace/" + idTwo).asJson();
        assertEquals("twentytwo", jsonResponse.getBody().getObject().getString("name"));
        assertEquals(idTwo, jsonResponse.getBody().getObject().getString("id"));
    }


}
