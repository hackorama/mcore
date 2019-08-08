package m.core.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

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

import m.core.common.TestServer;
import m.core.common.TestService;

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
        return TestService.getServerTypeList();
    }

    public UserServiceTest(String serverType) {
        TestService.setServerType(serverType);
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
    public void userService_patchUser_expectsUpdatedUserproperties() throws UnirestException {
        HttpResponse<JsonNode> jsonResponse;
        jsonResponse = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/user").header("accept", "application/json")
                .body("{ \"name\" : \"one\", \"email\" : \"one@example.com\" }").asJson();
        String id = jsonResponse.getBody().getObject().getString("id");
        String originalName = jsonResponse.getBody().getObject().getString("name");
        String originalEmail = jsonResponse.getBody().getObject().getString("email");
        jsonResponse = Unirest.patch(DEFAULT_SERVER_ENDPOINT + "/user/" + id).header("accept", "application/json")
                .body("{ \"name\" : \"updatedone\" }").asJson();
        assertEquals("Check the same resource was modified", id, jsonResponse.getBody().getObject().getString("id"));
        assertNotEquals("Check one of the property was modified", originalName,
                jsonResponse.getBody().getObject().getString("name"));
        assertEquals("Check one of the property was modified", "updatedone",
                jsonResponse.getBody().getObject().getString("name"));
        assertEquals("Check the other the property was not modified", originalEmail,
                jsonResponse.getBody().getObject().getString("email"));
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
    public void userService_sendHeadRequest_expectsHeadersWithoutBody() throws UnirestException {
        HttpResponse<JsonNode> jsonResponse;
        jsonResponse = Unirest.head(DEFAULT_SERVER_ENDPOINT + "/user").header("accept", "application/json").asJson();
        assertEquals("Check no body in response", null, jsonResponse.getBody());
        assertEquals("Check expected header in response", "OK", jsonResponse.getHeaders().get("HEAD").get(0));
    }

    @Test
    public void userService_sendOptionsRequest_expectsAllowedMethodsInAllowHeader() throws UnirestException {
        HttpResponse<JsonNode> jsonResponse;
        jsonResponse = Unirest.options(DEFAULT_SERVER_ENDPOINT + "/user").header("accept", "application/json").asJson();
        assertEquals("Check empty body in response", "{}", jsonResponse.getBody().toString());
        assertEquals("Check allowed methods in Allow header", "GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS",
                jsonResponse.getHeaders().get("Allow").get(0));
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
