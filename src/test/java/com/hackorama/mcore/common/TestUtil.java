package com.hackorama.mcore.common;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hackorama.mcore.data.DataStore;
import com.hackorama.mcore.data.MemoryDataStore;
import com.hackorama.mcore.server.Server;
import com.hackorama.mcore.server.spark.SparkServer;
import com.hackorama.mcore.service.Service;
import com.hackorama.mcore.service.environment.EnvironmentService;
import com.hackorama.mcore.service.group.GroupService;
import com.hackorama.mcore.service.workspace.WorkspaceService;

public class TestUtil {

    private static Logger logger = LoggerFactory.getLogger(TestUtil.class);

    private static final long DEFUALT_WAIT_SECONDS = 3;
    private static final String DEFAULT_SERVER_ENDPOINT = "http://127.0.0.1:4567";
    private static volatile Server server = null;
    private static volatile Service service = null;
    private static volatile DataStore dataStore = null;

    public static void clearDataOfServiceInstance() {
        if (dataStore != null) {
            dataStore.clear();
        }
    }

    public static String defaultServerEndpoint() {
        return DEFAULT_SERVER_ENDPOINT;
    }

    public static Server getServer() {
        initServer();
        return server;
    }

    public static Service initEnvServiceInstance() {
        initServer();
        if (service == null) {
            service = new EnvironmentService().configureUsing(server).configureUsing(dataStore);
            TestUtil.waitForService();
        }
        return service;
    }

    public static Service initGroupServiceInstance() {
        initServer();
        if (service == null) {
            service = new GroupService().configureUsing(server).configureUsing(dataStore);
            TestUtil.waitForService();
        }
        return service;
    }

    private static void initServer() {
        if (server == null) {
            server = new SparkServer("testserver", 4567);
            server.start();
        }
        if (dataStore == null) {
            dataStore = new MemoryDataStore();
        }
    }

    public static Service initServiceInstance() {
        return initWorkSpaceServiceInstance();
    }

    public static Service initWorkSpaceServiceInstance() {
        initServer();
        if (service == null) {
            service = new WorkspaceService().configureUsing(server).configureUsing(dataStore);
            TestUtil.waitForService();
        }
        return service;
    }

    public static void stopServiceInstance() {
        if (server != null) {
            server.stop();
            TestUtil.waitForService();
        }
        dataStore = null;
        service = null;
        server = null;
    }

    public static boolean waitForSeconds(long seconds) {
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(seconds));
        } catch (InterruptedException e) {
            return false;
        }
        return true;
    }

    public static boolean waitForService() {
        return waitForSeconds(DEFUALT_WAIT_SECONDS);
    }

    // Don't let anyone else instantiate this class
    private TestUtil() {
    }

}
