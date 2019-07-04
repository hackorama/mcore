package com.hackorama.mcore.server;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.hackorama.mcore.common.TestServer;
import com.hackorama.mcore.service.BaseService;
import com.hackorama.mcore.service.Service;

/**
 * Base test that runs on all server types using parameterization on
 * estServer.getServerTypeList()
 *
 * @author Kishan Thomas (kishan.thomas@gmail.com)
 *
 */
@RunWith(Parameterized.class)
public abstract class ServerTest { // Making abstract so JUnit will not try to run

    protected static class TestService extends BaseService {
        @Override
        public void configure() {
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

    private Service service;

    public ServerTest(String serverType) {
        TestServer.setServerType(serverType);
    }

    @Before
    public void setUp() throws Exception {
        service.configureUsing(TestServer.createNewServer()).start();
        TestServer.awaitStartup(); // Wait for the server
        System.out.println("Testing with server type: " + TestServer.getServerType());
    }

    @After
    public void tearDown() throws Exception {
        TestServer.awaitShutdown();
    }

    protected void usingService(Service service) {
        this.service = service;
    }

}
