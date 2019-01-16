package com.hackorama.mcore.common;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hackorama.mcore.data.DataStore;
import com.hackorama.mcore.data.MemoryDataStore;
import com.hackorama.mcore.server.Server;
import com.hackorama.mcore.server.spark.SparkServer;
import com.hackorama.mcore.server.spring.SpringServer;
import com.hackorama.mcore.service.Service;
import com.hackorama.mcore.service.environment.EnvironmentService;
import com.hackorama.mcore.service.group.GroupService;
import com.hackorama.mcore.service.workspace.WorkspaceService;

public class TestUtil {

    private static Logger logger = LoggerFactory.getLogger(TestUtil.class);

    private static final long DEFUALT_WAIT_SECONDS = 1;
    private static final int DEFAULT_SERVER_PORT = 4567;
    private static final int DEFAULT_GROUP_SERVER_PORT = 4568;
    private static final int DEFAULT_ENV_SERVER_PORT = 4569;
    private static final String DEFAULT_SERVER_ENDPOINT = "http://127.0.0.1:" + DEFAULT_SERVER_PORT;
    private static final String DEFAULT_GROUP_SERVER_ENDPOINT = "http://127.0.0.1:" + DEFAULT_GROUP_SERVER_PORT;
    private static final String DEFAULT_ENV_SERVER_ENDPOINT = "http://127.0.0.1:" + DEFAULT_ENV_SERVER_PORT;
    private static Server server = null;
    private static DataStore dataStore = null;
    private static volatile Service service = null;

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
            service = new EnvironmentService().configureUsing(server).configureUsing(dataStore).start();
            TestUtil.waitForService();
            logger.info("Started Environment Service on server {}", server.getName());
        }
        return service;
    }

    public static Service initGroupServiceInstance() {
        initServer();
        if (service == null) {
            service = new GroupService().configureUsing(server).configureUsing(dataStore).start();
            TestUtil.waitForService();
            logger.info("Started Group Service on server {}", server.getName());
        }
        return service;
    }

    private static synchronized void initServer() {
        if (server == null) {
            String serverType = System.getenv("SERVER_TYPE");
            logger.info("Using server type = {}", serverType);
            if ("SPRING".equalsIgnoreCase(serverType)) {
                server = new SpringServer("Spring Server", DEFAULT_SERVER_PORT);
                logger.info("Created Spring Server {} on {}", server.getName(), DEFAULT_SERVER_PORT);
            } else {
                server = new SparkServer("Spark Server", DEFAULT_SERVER_PORT);
                logger.info("Created Spark Server {} on {}", server.getName(), DEFAULT_SERVER_PORT);
            }
        }
        if (dataStore == null) {
            dataStore = new MemoryDataStore();
        }
    }

    public static Service initServiceInstance() {
        return initWorkSpaceServiceInstance();
    }

    public static Service initWorkSpaceGroupEnvServiceInstance() {
        initServer();
        if (service == null) {
            service = new WorkspaceService().configureUsing(server).configureUsing(dataStore).attach(new GroupService())
                    .attach(new EnvironmentService()).start();
            TestUtil.waitForService();
            logger.info("Started Workspace Service on server {}", server.getName());
        }
        return service;
    }

    public static Service initWorkSpaceServiceInstance() {
        initServer();
        if (service == null) {
            service = new WorkspaceService().configureUsing(server).configureUsing(dataStore).start();
            TestUtil.waitForService();
            logger.info("Started Workspace Service on server {}", server.getName());
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

    public static String defaultGroupServerEndpoint() {
        return DEFAULT_GROUP_SERVER_ENDPOINT;
    }

    public static String defaultEnvServerEndpoint() {
        return DEFAULT_ENV_SERVER_ENDPOINT;
    }

    public static int defaultGroupServerPort() {
        return DEFAULT_GROUP_SERVER_PORT;
    }

    public static int defaultEnvServerPort() {
        return DEFAULT_ENV_SERVER_PORT;
    }

}
