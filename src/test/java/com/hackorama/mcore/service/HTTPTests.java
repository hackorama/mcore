package com.hackorama.mcore.service;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.mashape.unirest.http.exceptions.UnirestException;

import com.hackorama.mcore.common.Request;
import com.hackorama.mcore.common.Response;
import com.hackorama.mcore.common.TestServer;

@RunWith(Parameterized.class)
public class HTTPTests {

    private static class TestService extends BaseService {

        @Override
        public void configure() {
            GET("/", this::getRoot);
            GET("/a", this::getLowerCase);
            GET("/A", this::getUpperCase);
            GET("/0", this::getNumeric);
            GET("/-", this::getSpecialChar);
            GET("/aA0", this::getMixedCase);
            GET("/empty", this::getEmpty);
            GET("/null", this::getNull);
            GET("/thequickbrownfoxjumpsoverthelazydog0123456789", this::getAlphaNumericLower);
            GET("/THEQUICKBROWNFOXJUMPSOVERTHELAZYDOG0123456789", this::getAlphaNumericUpper);
            GET("/TheQuickBrownFoxJumpsOverTheLazyDog0123456789", this::getAlphaNumericMixed);
            GET("/TheQuickBrownFoxJumpsOverTheLazyDog-0123456789", this::getAlphaNumericMixedSpecialChars);
        }

        public Response getAlphaNumericLower(Request request) {
            return new Response("thequickbrownfoxjumpsoverthelazydog0123456789");
        }

        public Response getAlphaNumericMixed(Request request) {
            return new Response("TheQuickBrownFoxJumpsOverTheLazyDog0123456789");
        }

        public Response getAlphaNumericMixedSpecialChars(Request request) {
            return new Response("TheQuickBrownFoxJumpsOverTheLazyDog-0123456789");
        }

        public Response getAlphaNumericUpper(Request request) {
            return new Response("THEQUICKBROWNFOXJUMPSOVERTHELAZYDOG0123456789");
        }

        public Response getEmpty(Request request) {
            return new Response("");
        }

        public Response getLowerCase(Request request) {
            return new Response("a");
        }

        public Response getMixedCase(Request request) {
            return new Response("aA0");
        }

        public Response getNull(Request request) {
            return new Response();
        }

        public Response getNumeric(Request request) {
            return new Response("0");
        }

        public Response getRoot(Request request) {
            return new Response("root");
        }

        public Response getSpecialChar(Request request) {
            return new Response("-");
        }

        public Response getUpperCase(Request request) {
            return new Response("A");
        }

    }

    @AfterClass
    public static void afterAllTests() throws Exception {
        TestServer.awaitShutdown();
    }

    @Parameters
    public static Iterable<? extends Object> data() {
        return TestServer.getServerTypeList();
    }

    public HTTPTests(String serverType) {
        TestServer.setServerType(serverType);
    }

    @Test
    public void service_receiveEmptyBody_verifyEmptyBodyIsHandledWithoutErrors() throws UnirestException {
        new TestService().configureUsing(TestServer.createNewServer()).start();
        TestServer.awaitStartup();
        assertTrue("Check empty response body", TestServer.getResponse("/empty").getBody().isEmpty());
        assertTrue("Check empty response body", TestServer.getResponse("/null").getBody().isEmpty());
        TestServer.awaitShutdown();
    }

    @Test
    public void service_sendDifferentPathFormats_verifyPathsAreHandledWithoutErrors() throws UnirestException {
        new TestService().configureUsing(TestServer.createNewServer()).start();
        TestServer.awaitStartup();
        assertEquals("Check response body", "root", TestServer.getResponse("/").getBody());
        assertEquals("Check response body", "a", TestServer.getResponse("/a").getBody());
        assertEquals("Check response body", "A", TestServer.getResponse("/A").getBody());
        assertEquals("Check response body", "0", TestServer.getResponse("/0").getBody());
        assertEquals("Check response body", "-", TestServer.getResponse("/-").getBody());
        assertEquals("Check response body", "aA0", TestServer.getResponse("/aA0").getBody());
        assertEquals("Check response body", "thequickbrownfoxjumpsoverthelazydog0123456789",
                TestServer.getResponse("/thequickbrownfoxjumpsoverthelazydog0123456789").getBody());
        assertEquals("Check response body", "THEQUICKBROWNFOXJUMPSOVERTHELAZYDOG0123456789",
                TestServer.getResponse("/THEQUICKBROWNFOXJUMPSOVERTHELAZYDOG0123456789").getBody());
        assertEquals("Check response body", "TheQuickBrownFoxJumpsOverTheLazyDog0123456789",
                TestServer.getResponse("/TheQuickBrownFoxJumpsOverTheLazyDog0123456789").getBody());
        assertEquals("Check response body", "TheQuickBrownFoxJumpsOverTheLazyDog-0123456789",
                TestServer.getResponse("/TheQuickBrownFoxJumpsOverTheLazyDog-0123456789").getBody());
        TestServer.awaitShutdown();
    }

    @Before
    public void setUp() throws Exception {
        System.out.println("Testing with server type: " + TestServer.getServerType());
    }

    @After
    public void tearDown() throws Exception {
        TestServer.awaitShutdown();
    }

}
