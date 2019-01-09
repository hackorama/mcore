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
import com.hackorama.mcore.data.DataStore;
import com.hackorama.mcore.data.MemoryDataStore;
import com.hackorama.mcore.server.Server;
import com.hackorama.mcore.server.spark.SparkServer;
import com.hackorama.mcore.service.workspace.WorkspaceService;

/**
 * Tests for Workspace service
 *
 * @author Kishan Thomas (kishan.thomas@gmail.com)
 *
 */
public class CommonServiceTest {

    private static final String DEFAULT_SERVER_ENDPOINT = "http://127.0.0.1:4567" ;
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
