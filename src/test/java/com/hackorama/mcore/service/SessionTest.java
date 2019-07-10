package com.hackorama.mcore.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.mashape.unirest.http.exceptions.UnirestException;

import com.hackorama.mcore.common.Request;
import com.hackorama.mcore.common.Response;
import com.hackorama.mcore.common.TestServer;
import com.hackorama.mcore.server.ServerTest;

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
            Response response = new Response("END SESSION : " + request.getSession().getAttribute("NAME"));
            request.getSession().removeAttribute("NAME"); // Not required if invalidating, like in this case
            request.getSession().invalidate();
            return response;
        }

        public Response endSessionInvalidate(Request request) {
            Response response = new Response("END SESSION : " + request.getSession().getAttribute("NAME"));
            request.getSession().invalidate();
            return response;
        }

        public Response endSessionNoInvalidate(Request request) {
            Response response = new Response("END SESSION : " + request.getSession().getAttribute("NAME"));
            request.getSession().removeAttribute("NAME");
            return response;
        }

        public Response inSession(Request request) {
            Response response = null;
            if (request.getSession().getAttribute("NAME") == null
                    || request.getSession().getAttribute("NAME").toString().isEmpty()) {
                response = new Response("NO SESSION");
            } else {
                response = new Response("IN SESSION : " + request.getSession().getAttribute("NAME"));
            }
            return response;
        }

        public Response startSession(Request request) {
            request.getSession().setAttribute("NAME", request.getPathParams().get("name"));
            Response response = new Response("START SESSION : " + request.getSession().getAttribute("NAME"));
            return response;
        }

    }

    public SessionTest(String serverType) {
        super(serverType);
    }

    @Test
    public void service_testSessions() throws UnirestException {
        if(TestServer.isPlayServer()) { // TODO FIXME PLAY
            System.out.println("Skipping session tests for Play Server ...");
            return;
        }
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
        if(TestServer.isPlayServer()) { // TODO FIXME PLAY
            System.out.println("Skipping session tests for Play Server ...");
            return;
        }
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
        if(TestServer.isPlayServer()) { // TODO FIXME PLAY
            System.out.println("Skipping session tests for Play Server ...");
            return;
        }
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
