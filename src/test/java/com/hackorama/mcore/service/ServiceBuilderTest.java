package com.hackorama.mcore.service;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.mashape.unirest.http.exceptions.UnirestException;

import com.hackorama.mcore.common.HttpMethod;
import com.hackorama.mcore.common.Request;
import com.hackorama.mcore.common.Response;
import com.hackorama.mcore.common.TestServer;
import com.hackorama.mcore.common.TestUtil;
import com.hackorama.mcore.data.DataStore;
import com.hackorama.mcore.data.mapdb.MapdbDataStore;
import com.hackorama.mcore.data.redis.RedisDataStoreCacheQueue;
import com.hackorama.mcore.demo.HelloService;

public class ServiceBuilderTest {

    protected void setServer() {
        TestServer.setServerTypeSpark();
    }

    @Before
    public void setUp() throws Exception {
        setServer();
    }

    @After
    public void tearDown() throws Exception {
        TestServer.awaitShutdown();
    }

    @AfterClass
    public static void afterAllTests() throws Exception {
        TestServer.awaitShutdown();
    }

    @Test
    public void service_dynamicOrchestration_expectsNoErrors()
            throws FileNotFoundException, IOException, UnirestException {
        DataStore store = null;
        Service service = null;
        if (TestUtil.getEnv("REDIS_TEST")) {
            store = new RedisDataStoreCacheQueue();
            service = new HelloService().configureUsing(TestServer.getServer()).configureUsing(store)
                    .configureUsing(store.asQueue()).configureUsing(store.asCache()).start();
            assertNotNull(service);
        } else {
            store = new MapdbDataStore();
            service = new HelloService().configureUsing(TestServer.getServer()).configureUsing(store).start();
            assertNotNull(service);
        }
        TestServer.awaitStartup();
        assertTrue(TestServer.validResponse("/hello/mcore", "hello mcore"));
        TestServer.awaitShutdown();
    }

    @Test
    public void service_attachServicesUnderSameServer_expectsNoErrors() throws UnirestException {
        new ServiceOne().configureUsing(TestServer.getServer()).attach(new ServiceTwo()).start();
        TestServer.awaitStartup();
        assertTrue(TestServer.validResponse("/one", "ONE"));
        assertTrue(TestServer.validResponse("/two", "TWO"));
        assertFalse(TestServer.validResponse("/one", "TWO"));
        assertFalse(TestServer.validResponse("/two", "ONE"));
        TestServer.awaitShutdown();
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void service_attachServicesUnderSameServerAttchBeforeConfigure_expectsNoErrors() throws UnirestException {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Please configure a server before attaching a service");
        new ServiceOne().attach(new ServiceTwo());
    }

    private static class ServiceOne extends BaseService {

        public static Response getOne(Request request) {
            return new Response("ONE");
        }

        @Override
        public void configure() {
            server.setRoutes(HttpMethod.GET, "/one", ServiceOne::getOne);

        }
    };

    private static class ServiceTwo extends BaseService {

        public static Response getTwo(Request request) {
            return new Response("TWO");
        }

        @Override
        public void configure() {
            if (server == null) {
                throw new RuntimeException("Valid server required before configuring the server");
            }
            server.setRoutes(HttpMethod.GET, "/two", ServiceTwo::getTwo);
        }
    };

}
