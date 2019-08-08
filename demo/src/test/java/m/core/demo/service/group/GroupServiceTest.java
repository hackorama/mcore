package m.core.demo.service.group;


import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import m.core.demo.common.TestUtil;

/**
 * Tests for Group service
 *
 * @author Kishan Thomas (kishan.thomas@gmail.com)
 *
 */
public class GroupServiceTest {

    private static final String DEFAULT_SERVER_ENDPOINT = TestUtil.defaultServerEndpoint();

    protected void setServer() {
        TestUtil.setServerTypeSpark();
    }

    @Before
    public void setUp() throws Exception {
        setServer();
        TestUtil.initGroupServiceInstance();
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
        assertEquals("two", jsonResponse.getBody().getArray().getJSONObject(0).getString("name"));
        assertEquals("two@example.com", jsonResponse.getBody().getArray().getJSONObject(0).getString("email"));
        assertEquals(idTwo, jsonResponse.getBody().getArray().getJSONObject(0).getString("id"));
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
