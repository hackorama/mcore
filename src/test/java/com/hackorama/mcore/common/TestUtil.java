package com.hackorama.mcore.common;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hackorama.mcore.data.DataStore;
import com.hackorama.mcore.data.MemoryDataStore;
import com.hackorama.mcore.demo.HelloService;
import com.hackorama.mcore.server.Server;
import com.hackorama.mcore.server.spark.SparkServer;
import com.hackorama.mcore.server.spring.SpringServer;
import com.hackorama.mcore.server.vertx.VertxServer;
import com.hackorama.mcore.service.Service;
import com.hackorama.mcore.service.UserService;

public class TestUtil {

    private static Logger logger = LoggerFactory.getLogger(TestUtil.class);

    private static final long DEFUALT_WAIT_SECONDS = 1;
    private static final int DEFAULT_SERVER_PORT = 4567;
    private static final int DEFAULT_GROUP_SERVER_PORT = 4568;
    private static final int DEFAULT_ENV_SERVER_PORT = 4569;
    private static final String DEFAULT_SERVER_ENDPOINT = "http://127.0.0.1:" + DEFAULT_SERVER_PORT;
    private static final String SERVER_TYPE_SPRING = "SPRING";
    private static final String SERVER_TYPE_SPARK = "SPARK";
    private static final String SERVER_TYPE_VERTX = "VERTX";

    private static Server server = null;
    private static DataStore dataStore = null;
    private static volatile Service service = null;
    private static String serverType = System.getenv("SERVER_TYPE");

    public static void clearDataOfServiceInstance() {
        if (dataStore != null) {
            dataStore.clear();
        }
    }

    public static String defaultServerEndpoint() {
        return DEFAULT_SERVER_ENDPOINT;
    }

    public static boolean getEnv(String envName) {
        return System.getenv(envName) != null;
    }

    public static String getEnv(String envName, String defaultValue) {
        String value = System.getenv(envName);
        return (value == null) ? defaultValue : value;
    }

    public static Server getServer() {
        initServer();
        return server;
    }

    public static Service initHelloServiceInstance() {
        initServer();
        if (service == null) {
            service = new HelloService().configureUsing(server).configureUsing(dataStore).start();
            TestUtil.waitForService();
            logger.info("Started Environment Service on server {}", server.getName());
        }
        return service;
    }

    public static Service initUserServiceInstance() {
        initServer();
        if (service == null) {
            service = new UserService().configureUsing(server).configureUsing(dataStore).start();
            TestUtil.waitForService();
            logger.info("Started User Service on server {}", server.getName());
        }
        return service;
    }

    private static synchronized void initServer() {
        if (server == null) {
            logger.info("Using server type = {}", serverType);
            if (SERVER_TYPE_SPRING.equalsIgnoreCase(serverType)) {
                server = new SpringServer("Spring Server", DEFAULT_SERVER_PORT);
                logger.info("Created Spring Server {} on {}", server.getName(), DEFAULT_SERVER_PORT);
            } else if (SERVER_TYPE_VERTX.equalsIgnoreCase(serverType)) {
                server = new VertxServer("Vertx Server", DEFAULT_SERVER_PORT);
                logger.info("Created Vertx Server {} on {}", server.getName(), DEFAULT_SERVER_PORT);
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
        return initHelloServiceInstance();
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

    public static int defaultGroupServerPort() {
        return DEFAULT_GROUP_SERVER_PORT;
    }

    public static int defaultEnvServerPort() {
        return DEFAULT_ENV_SERVER_PORT;
    }

    public static void setServerTypeSpring() {
        serverType = SERVER_TYPE_SPRING;
    }

    public static void setServerTypeSpark() {
        serverType = SERVER_TYPE_SPARK;
    }

    public static void setServerTypeVertx() {
        serverType = SERVER_TYPE_VERTX;
    }

    public static String getServerType() {
        return serverType;
    }

    public static void resetServerType() {
        serverType = System.getenv("SERVER_TYPE");
    }

    public static void setServerType(String serverType) {
        TestUtil.serverType = serverType;
    }

    public static boolean waitOnPort(String host, int port, int timeOutSeconds) {
        int elapsedSeconds = 0;
        while (usingPort(host, port)) {
            TestUtil.waitForSeconds(1);
            if (timeOutSeconds > 0 && elapsedSeconds++ > timeOutSeconds) {
                return false;
            }
        }
        return true;
    }

    public static boolean waitForPort(String host, int port, int timeOutSeconds) {
        int elapsedSeconds = 0;
        while (!usingPort(host, port)) {
            TestUtil.waitForSeconds(1);
            if (timeOutSeconds > 0 && elapsedSeconds++ > timeOutSeconds) {
                return false;
            }
        }
        return true;
    }

    public static boolean usingPort(String host, int port) {
        Socket socket = null;
        try {
            socket = new Socket(host, port);
        } catch (IOException e) {
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return socket != null;
    }

}
