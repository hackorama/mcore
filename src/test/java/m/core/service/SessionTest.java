package m.core.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.mashape.unirest.http.exceptions.UnirestException;

import m.core.common.Debug;
import m.core.common.TestServer;
import m.core.http.Request;
import m.core.http.Response;
import m.core.server.ServerTest;

public class SessionTest extends ServerTest {

    private static class SessionTestService extends TestService {

        @Override
        public void configure() {
            GET("/start/{name}", this::startSession);
            GET("/session", this::inSession);
            GET("/end", this::endSession);
            GET("/end/invalidate", this::endSessionInvalidate);
            GET("/end/no/invalidate", this::endSessionNoInvalidate);
        }

        public Response endSession(Request request) {
            Debug.print(request);
            Response response = new Response("END SESSION : " + request.getSession().getAttribute("NAME"));
            request.getSession().removeAttribute("NAME"); // Not required if invalidating, like in this case
            request.getSession().invalidate();
            Debug.print(response);
            return response;
        }

        public Response endSessionInvalidate(Request request) {
            Debug.print(request);
            Response response = new Response("END SESSION : " + request.getSession().getAttribute("NAME"));
            request.getSession().invalidate();
            Debug.print(response);
            return response;
        }

        public Response endSessionNoInvalidate(Request request) {
            Debug.print(request);
            Response response = new Response("END SESSION : " + request.getSession().getAttribute("NAME"));
            request.getSession().removeAttribute("NAME");
            Debug.print(response);
            return response;
        }

        public Response inSession(Request request) {
            Debug.print(request);
            Response response = null;
            if (request.getSession().getAttribute("NAME") == null
                    || request.getSession().getAttribute("NAME").toString().isEmpty()) {
                response = new Response("NO SESSION");
            } else {
                response = new Response("IN SESSION : " + request.getSession().getAttribute("NAME"));
            }
            Debug.print(response);
            return response;
        }

        public Response startSession(Request request) {
            Debug.print(request);
            request.getSession().setAttribute("NAME", request.getPathParams().get("name"));
            Response response = new Response("START SESSION : " + request.getSession().getAttribute("NAME"));
            Debug.print(response);
            return response;
        }

    }

    public SessionTest(String serverType) {
        super(serverType);
        Debug.disable();
    }

    @Test
    public void service_testSessions() throws UnirestException {
        final String NAME = "mcore";
        assertEquals("Check not in session", "NO SESSION", TestServer.getResponse("/session").getBody());
        assertEquals("Check started session", "START SESSION : " + NAME,
                TestServer.getResponse("/start/" + NAME).getBody());
        assertEquals("Check in session", "IN SESSION : " + NAME, TestServer.getResponse("/session").getBody());
        assertEquals("Check in session", "IN SESSION : " + NAME, TestServer.getResponse("/session").getBody());
        assertEquals("Check session ended", "END SESSION : " + NAME, TestServer.getResponse("/end").getBody());
        assertEquals("Check not in session", "NO SESSION", TestServer.getResponse("/session").getBody());
    }

    @Test
    public void service_testSessions_withNoSessionInvalidation() throws UnirestException {
        final String NAME = "mcore";
        assertEquals("Check not in session", "NO SESSION", TestServer.getResponse("/session").getBody());
        assertEquals("Check started session", "START SESSION : " + NAME,
                TestServer.getResponse("/start/" + NAME).getBody());
        assertEquals("Check in session", "IN SESSION : " + NAME, TestServer.getResponse("/session").getBody());
        assertEquals("Check in session", "IN SESSION : " + NAME, TestServer.getResponse("/session").getBody());
        assertEquals("Check session ended without invalidation", "END SESSION : " + NAME,
                TestServer.getResponse("/end/no/invalidate").getBody());
        assertEquals("Check not in session", "NO SESSION", TestServer.getResponse("/session").getBody());
    }

    @Test
    public void service_testSessions_withSessionInvalidation() throws UnirestException {
        final String NAME = "mcore";
        assertEquals("Check not in session", "NO SESSION", TestServer.getResponse("/session").getBody());
        assertEquals("Check started session", "START SESSION : " + NAME,
                TestServer.getResponse("/start/" + NAME).getBody());
        assertEquals("Check in session", "IN SESSION : " + NAME, TestServer.getResponse("/session").getBody());
        assertEquals("Check in session", "IN SESSION : " + NAME, TestServer.getResponse("/session").getBody());
        assertEquals("Check session ended with invalidation", "END SESSION : " + NAME,
                TestServer.getResponse("/end/invalidate").getBody());
        assertEquals("Check not in session", "NO SESSION", TestServer.getResponse("/session").getBody());
    }

    @Override
    protected Service useDefaultService() {
        return new SessionTestService();
    }

}
