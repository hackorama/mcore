package m.core.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.mashape.unirest.http.exceptions.UnirestException;

import m.core.common.TestServer;
import m.core.http.Request;
import m.core.http.Response;
import m.core.server.ServerTest;

/**
 * Example for creating service tests that will get tested on all server types,
 * using parameterization in ServerTest.
 *
 * @author Kishan Thomas (kishan.thomas@gmail.com)
 *
 */
public class DemoServiceTest extends ServerTest {

    // Create one or more test services

    private static class DemoServiceOne extends TestService {

        @Override
        public void configure() {
            GET("/one", this::test);
        }

        public Response test(Request request) {
            return new Response("ONE");
        }
    }

    private static class DemoServiceTwo extends TestService {
        @Override
        public void configure() {
            GET("/two", this::test);
        }

        public Response test(Request request) {
            return new Response("TWO");
        }
    }

    public DemoServiceTest(String serverType) {
        super(serverType);
    }

    @Test
    public void test_multipleServices() throws UnirestException {
        // This shows using multiple test services using usingService() which overrides
        // the default service from DemoServiceOne()
        usingService(new DemoServiceOne());
        assertEquals("Check response body", "ONE", TestServer.getResponse("/one").getBody());
        usingService(new DemoServiceTwo());
        assertEquals("Check response body", "TWO", TestServer.getResponse("/two").getBody());
    }

    @Test
    public void test_SingleService() throws UnirestException {
        // This will run against the default service DemoServiceOne defined in
        // useDefaultService()
        assertEquals("Check response body", "ONE", TestServer.getResponse("/one").getBody());
    }

    @Override
    protected Service useDefaultService() {
        // This defines the default service tests will use if a service is not specified
        // using usingService()
        return new DemoServiceOne();
    }

}
