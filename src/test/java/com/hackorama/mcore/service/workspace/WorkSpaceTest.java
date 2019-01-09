package com.hackorama.mcore.service.workspace;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import com.hackorama.mcore.common.TestUtil;
import com.hackorama.mcore.data.DataStore;
import com.hackorama.mcore.data.MemoryDataStore;
import com.hackorama.mcore.server.Server;
import com.hackorama.mcore.server.spark.SparkServer;
import com.hackorama.mcore.service.Service;
import com.hackorama.mcore.service.environment.EnvironmentService;
import com.hackorama.mcore.service.group.GroupService;

/**
 * Tests for Workspace service
 *
 * @author Kishan Thomas (kishan.thomas@gmail.com)
 *
 */
public class WorkSpaceTest {

    private static final String DEFAULT_SERVER_ENDPOINT = "http://127.0.0.1:4567";
    private static Server server;
    private Service service;
    private DataStore dataStore;

    @Before
    public void setUp() throws Exception {
        TestUtil.waitForService();
        if (server == null) {
            server = new SparkServer("workspace", 4567);
            TestUtil.waitForService();
        }
        if (dataStore == null) {
            dataStore = new MemoryDataStore();
        }
        if (service == null) {
            service = new WorkspaceService().configureUsing(server).configureUsing(dataStore);
        }
    }

    @After
    public void tearDown() throws Exception {
        dataStore.clear();
    }

    @AfterClass
    public static void afterAllTests() throws Exception {
        if (server != null) {
            server.stop();
        }
        TestUtil.waitForService();
    }

    @Test
    public void workspaceService_componentTreeRelations_expectsResponseWithChildenList() throws UnirestException {
        HttpResponse<JsonNode> jsonResponse;
        // Add group service to existing server
        new GroupService().configureUsing(server);
        // Add environment service to existing server
        new EnvironmentService().configureUsing(server);
        TestUtil.waitForService();
        // Create 3 workspaaces
        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/workspace").header("accept", "application/json")
                .body("{ \"name\" : \"workspace-one-with-two-groups-and-one-env\" }").asJson();
        String workspaceOneWithTwoGroupsAndOnEenv = jsonResponse.getBody().getObject().getString("id");
        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/workspace").header("accept", "application/json")
                .body("{ \"name\" : \"workspace-two-with-one-group\" }").asJson();
        String workspaceTwoWithOneGroup = jsonResponse.getBody().getObject().getString("id");
        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/workspace").header("accept", "application/json")
                .body("{ \"name\" : \"workspace-three-with-one-group-and-two-env\" }").asJson();
        String workspaceThreeWitOneGroupAndTwoEnv = jsonResponse.getBody().getObject().getString("id");

        // Create groups and env and link them
        String id;
        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/group").header("accept", "application/json")
                .body("{ \"name\" : \"workspace-one-group-one\", \"email\" : \"user@example.com\" }").asJson();
        id = jsonResponse.getBody().getObject().getString("id");
        jsonResponse = Unirest
                .post(DEFAULT_SERVER_ENDPOINT + "/workspace/" + workspaceOneWithTwoGroupsAndOnEenv + "/group/" + id)
                .header("accept", "application/json").asJson();

        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/group").header("accept", "application/json")
                .body("{ \"name\" : \"workspace-one-group-two\", \"email\" : \"user@example.com\" }").asJson();
        id = jsonResponse.getBody().getObject().getString("id");
        jsonResponse = Unirest
                .post(DEFAULT_SERVER_ENDPOINT + "/workspace/" + workspaceOneWithTwoGroupsAndOnEenv + "/group/" + id)
                .header("accept", "application/json").asJson();

        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/group").header("accept", "application/json")
                .body("{ \"name\" : \"workspace-two-group-one\", \"email\" : \"user@example.com\" }").asJson();
        id = jsonResponse.getBody().getObject().getString("id");
        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/workspace/" + workspaceTwoWithOneGroup + "/group/" + id)
                .header("accept", "application/json").asJson();

        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/group").header("accept", "application/json")
                .body("{ \"name\" : \"workspace-three-group-one\", \"email\" : \"user@example.com\" }").asJson();
        id = jsonResponse.getBody().getObject().getString("id");
        jsonResponse = Unirest
                .post(DEFAULT_SERVER_ENDPOINT + "/workspace/" + workspaceThreeWitOneGroupAndTwoEnv + "/group/" + id)
                .header("accept", "application/json").asJson();

        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/environment").header("accept", "application/json")
                .body("{ \"name\" : \"workspace-one-env-one\" }").asJson();
        id = jsonResponse.getBody().getObject().getString("id");
        jsonResponse = Unirest.post(
                DEFAULT_SERVER_ENDPOINT + "/workspace/" + workspaceOneWithTwoGroupsAndOnEenv + "/environment/" + id)
                .header("accept", "application/json").asJson();

        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/environment").header("accept", "application/json")
                .body("{ \"name\" : \"workspace-three-env-one\" }").asJson();
        id = jsonResponse.getBody().getObject().getString("id");
        jsonResponse = Unirest.post(
                DEFAULT_SERVER_ENDPOINT + "/workspace/" + workspaceThreeWitOneGroupAndTwoEnv + "/environment/" + id)
                .header("accept", "application/json").asJson();

        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/environment").header("accept", "application/json")
                .body("{ \"name\" : \"workspace-three-env-two\" }").asJson();
        id = jsonResponse.getBody().getObject().getString("id");
        jsonResponse = Unirest.post(
                DEFAULT_SERVER_ENDPOINT + "/workspace/" + workspaceThreeWitOneGroupAndTwoEnv + "/environment/" + id)
                .header("accept", "application/json").asJson();

        jsonResponse = Unirest
                .get(DEFAULT_SERVER_ENDPOINT + "/workspace/" + workspaceThreeWitOneGroupAndTwoEnv + "/environment")
                .header("accept", "application/json").asJson();
        jsonResponse = Unirest
                .get(DEFAULT_SERVER_ENDPOINT + "/workspace/" + workspaceThreeWitOneGroupAndTwoEnv + "/environment")
                .header("accept", "application/json").asJson();

        // get the tree of components
        jsonResponse = Unirest.get(DEFAULT_SERVER_ENDPOINT + "/component").asJson();
        List<String> workspaceOneChildren = new ArrayList<>();
        List<String> workspaceThreeChildren = new ArrayList<>();
        jsonResponse.getBody().getObject().getJSONArray("workspaces").forEach(workspaceId -> {
            try {
                HttpResponse<JsonNode> response = Unirest.get(DEFAULT_SERVER_ENDPOINT + "/workspace/" + workspaceId)
                        .asJson();
                response = Unirest.get(DEFAULT_SERVER_ENDPOINT + "/component/" + workspaceId).asJson();
                response.getBody().getObject().getJSONArray("environments").forEach(envId -> {
                    try {
                        HttpResponse<JsonNode> envResponse = Unirest
                                .get(DEFAULT_SERVER_ENDPOINT + "/environment/" + envId).asJson();
                        if (workspaceOneWithTwoGroupsAndOnEenv.equals(workspaceId)) {
                            workspaceOneChildren.add(envResponse.getBody().getObject().getString("name"));
                        }
                        if (workspaceThreeWitOneGroupAndTwoEnv.equals(workspaceId)) {
                            workspaceThreeChildren.add(envResponse.getBody().getObject().getString("name"));
                        }
                    } catch (UnirestException e) {
                        e.printStackTrace();
                    }

                });
            } catch (UnirestException e) {
                e.printStackTrace();
            }
        });
        assertEquals(1, workspaceOneChildren.size());
        assertTrue(workspaceOneChildren.contains("workspace-one-env-one"));
        assertEquals(2, workspaceThreeChildren.size());
        assertTrue(workspaceThreeChildren.contains("workspace-three-env-one"));
        assertTrue(workspaceThreeChildren.contains("workspace-three-env-two"));

        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/environment").header("accept", "application/json")
                .body("{ \"name\" : \"test-env\" }").asJson();
        id = jsonResponse.getBody().getObject().getString("id");
        jsonResponse = Unirest.post(
                DEFAULT_SERVER_ENDPOINT + "/workspace/" + workspaceTwoWithOneGroup + "/environment/" + id)
                .header("accept", "application/json").asJson();
        jsonResponse = Unirest.delete(
                DEFAULT_SERVER_ENDPOINT + "/workspace/" + workspaceTwoWithOneGroup + "/environment/" + id)
                .header("accept", "application/json").asJson();

        jsonResponse = Unirest
                .get(DEFAULT_SERVER_ENDPOINT + "/workspace/" + workspaceTwoWithOneGroup + "/environment")
                .header("accept", "application/json").asJson();

        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/group").header("accept", "application/json")
                .body("{ \"name\" : \"test-group\" }").asJson();
        id = jsonResponse.getBody().getObject().getString("id");
        jsonResponse = Unirest.post(
                DEFAULT_SERVER_ENDPOINT + "/workspace/" + workspaceTwoWithOneGroup + "/group/" + id)
                .header("accept", "application/json").asJson();
        jsonResponse = Unirest.delete(
                DEFAULT_SERVER_ENDPOINT + "/workspace/" + workspaceTwoWithOneGroup + "/group/" + id)
                .header("accept", "application/json").asJson();

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
    public void workspaceService_invalidEnvWithService_expectsLinkValidated() throws UnirestException {
        // Add env service to existing server
        new EnvironmentService().configureUsing(server);
        TestUtil.waitForService();
        HttpResponse<JsonNode> jsonResponse;
        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/workspace").header("accept", "application/json")
                .body("{ \"name\" : \"one\" }").asJson();
        assertEquals("one", jsonResponse.getBody().getObject().getString("name"));
        String workspaceId = jsonResponse.getBody().getObject().getString("id");

        String invalidEnvId = "invalid-env-id";
        Unirest.post(DEFAULT_SERVER_ENDPOINT + "/workspace/" + workspaceId + "/environment/" + invalidEnvId)
                .header("accept", "application/json").asJson();
        Unirest.get(DEFAULT_SERVER_ENDPOINT + "/workspace/" + workspaceId + "/environment")
                .header("accept", "application/json").asJson();
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
    public void workspaceService_invalidGroupWithService_expectsLinkValidated() throws UnirestException {
        // Add group service to existing server
        Service groupService = new GroupService().configureUsing(server);
        TestUtil.waitForService();
        HttpResponse<JsonNode> jsonResponse;
        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/workspace").header("accept", "application/json")
                .body("{ \"name\" : \"one\" }").asJson();
        assertEquals("one", jsonResponse.getBody().getObject().getString("name"));
        String workspaceId = jsonResponse.getBody().getObject().getString("id");
        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/workspace/" + workspaceId + "/group/invalid-group-id")
                .header("accept", "application/json").asJson();
        jsonResponse = Unirest.get(DEFAULT_SERVER_ENDPOINT + "/workspace/" + workspaceId)
                .header("accept", "application/json").asJson();
        assertEquals(0, jsonResponse.getBody().getObject().getJSONArray("owners").length());
        groupService.stop();
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

    @Test
    public void workspaceService_withGroupsinWorkspace_expectsWorkspaceResponseToIncludeGroupData()
            throws UnirestException {
        // Add group service to existing server
        Service groupService = new GroupService().configureUsing(server);
        TestUtil.waitForService();
        // Create some groups
        HttpResponse<JsonNode> jsonResponse;
        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/group").header("accept", "application/json")
                .body("{ \"name\" : \"one\", \"email\" : \"one@example.com\" }").asJson();
        assertEquals("one", jsonResponse.getBody().getObject().getString("name"));
        assertEquals("one@example.com", jsonResponse.getBody().getObject().getString("email"));
        String groupOneId = jsonResponse.getBody().getObject().getString("id");

        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/group").header("accept", "application/json")
                .body("{ \"name\" : \"two\", \"email\" : \"two@example.com\" }").asJson();
        assertEquals("two", jsonResponse.getBody().getObject().getString("name"));
        assertEquals("two@example.com", jsonResponse.getBody().getObject().getString("email"));

        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/group").header("accept", "application/json")
                .body("{ \"name\" : \"three\", \"email\" : \"three@example.com\" }").asJson();
        assertEquals("three", jsonResponse.getBody().getObject().getString("name"));
        assertEquals("three@example.com", jsonResponse.getBody().getObject().getString("email"));
        String groupThreeId = jsonResponse.getBody().getObject().getString("id");

        // Create a workspace
        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/workspace").header("accept", "application/json")
                .body("{ \"name\" : \"one\" }").asJson();
        assertEquals("one", jsonResponse.getBody().getObject().getString("name"));
        String workspaceId = jsonResponse.getBody().getObject().getString("id");
        // Attach 2 out of the 3 groups to workspace
        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/workspace/" + workspaceId + "/group/" + groupOneId)
                .header("accept", "application/json").asJson();
        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/workspace/" + workspaceId + "/group/" + groupThreeId)
                .header("accept", "application/json").asJson();
        jsonResponse = Unirest.get(DEFAULT_SERVER_ENDPOINT + "/workspace/" + workspaceId).asJson();
        // check the response includes the groups
        assertEquals(2, jsonResponse.getBody().getObject().getJSONArray("owners").length());
        String id = jsonResponse.getBody().getObject().getJSONArray("owners").getJSONObject(0).getString("id");
        if (groupOneId.equals(id)) {
            assertEquals("one@example.com",
                    jsonResponse.getBody().getObject().getJSONArray("owners").getJSONObject(0).get("email"));
            assertEquals("three@example.com",
                    jsonResponse.getBody().getObject().getJSONArray("owners").getJSONObject(1).get("email"));
        } else if (groupThreeId.equals(id)) {
            assertEquals("one@example.com",
                    jsonResponse.getBody().getObject().getJSONArray("owners").getJSONObject(1).get("email"));
            assertEquals("three@example.com",
                    jsonResponse.getBody().getObject().getJSONArray("owners").getJSONObject(0).get("email"));
        } else {
            fail("Unexpected group id " + id);
        }
        groupService.stop();
    }
}
