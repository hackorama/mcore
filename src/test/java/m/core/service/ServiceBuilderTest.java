package m.core.service;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

import com.mashape.unirest.http.exceptions.UnirestException;

import m.core.common.HelloService;
import m.core.common.TestServer;
import m.core.data.DataStore;
import m.core.data.mapdb.MapdbDataStore;
import m.core.data.redis.RedisDataStoreCacheQueue;
import m.core.http.Request;
import m.core.http.Response;
import m.core.server.ServerTest;

public class ServiceBuilderTest extends ServerTest {

    private static class ServiceOne extends Service {

        public static Response getOne(Request request) {
            return new Response("ONE");
        }

        @Override
        public void configure() {
            GET("/one", ServiceOne::getOne);

        }
    }

    private static class ServiceTwo extends Service {

        public static Response getTwo(Request request) {
            return new Response("TWO");
        }

        @Override
        public void configure() {
            GET("/two", ServiceTwo::getTwo);
        }
    }

    public ServiceBuilderTest(String serverType) {
        super(serverType);
    }

    @Test
    public void service_attachServicesUnderSameServer_expectsNoErrors() throws UnirestException {
        new ServiceOne().configureUsing(TestServer.createNewServer()).attach(new ServiceTwo()).start();
        TestServer.awaitStartup();
        assertTrue(TestServer.validResponse("/one", "ONE"));
        assertTrue(TestServer.validResponse("/two", "TWO"));
        assertFalse(TestServer.validResponse("/one", "TWO"));
        assertFalse(TestServer.validResponse("/two", "ONE"));
    }

    @Test
    public void service_attachServicesUnderSameServerAttchBeforeConfigure_expectsNoErrors() throws UnirestException {
        new ServiceOne().attach(new ServiceTwo()).configureUsing(TestServer.createNewServer()).attach(new ServiceTwo())
                .start();
        TestServer.awaitStartup();
        assertTrue(TestServer.validResponse("/one", "ONE"));
        assertTrue(TestServer.validResponse("/two", "TWO"));
        assertFalse(TestServer.validResponse("/one", "TWO"));
        assertFalse(TestServer.validResponse("/two", "ONE"));
    }

    @Test
    public void service_dynamicOrchestration_expectsNoErrors()
            throws FileNotFoundException, IOException, UnirestException {
        DataStore store = null;
        Service service = null;
        if (m.core.common.TestService.getEnv("REDIS_TEST")) {
            store = new RedisDataStoreCacheQueue();
            service = new HelloService().configureUsing(TestServer.createNewServer()).configureUsing(store)
                    .configureUsing(store.asQueue()).configureUsing(store.asCache()).start();
            assertNotNull(service);
        } else {
            store = new MapdbDataStore();
            service = new HelloService().configureUsing(TestServer.createNewServer()).configureUsing(store).start();
            assertNotNull(service);
        }
        TestServer.awaitStartup();
        assertTrue(TestServer.validResponse("/hello/mcore", "hello mcore"));
    }

    @Override
    protected Service useDefaultService() {
        return new ServiceOne();
    }

}
