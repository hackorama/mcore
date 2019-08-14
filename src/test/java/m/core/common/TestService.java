package m.core.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import m.core.data.MemoryDataStore;
import m.core.server.Server;
import m.core.server.spark.SparkServer;
import m.core.server.spring.SpringServer;
import m.core.server.vertx.VertxServer;
import m.core.service.Service;

public class TestService extends Test {

    private static Logger logger = LoggerFactory.getLogger(TestService.class);

    private static final int DEFAULT_SERVER_PORT = 4567; //TODO Check if the port need to be unique from TestServer
    private static final String DEFAULT_SERVER_ENDPOINT = "http://" + DEFAULT_SERVER_HOST + ":" + DEFAULT_SERVER_PORT;

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

    public static String getServerType() {
        return serverType;
    }

    public static Iterable<? extends Object> getServerTypeList() {
        return serverTypes;
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
        initServer();
        if (service == null) {
            service = new HelloService().configureUsing(server).configureUsing(dataStore).start();
            waitForService();
            logger.info("Started Environment Service on server {}", server.getName());
        }
        return service;
    }

    public static Service initServiceInstance(Service service) {
        initServer();
        if (TestService.service == null) {
            TestService.service = service.configureUsing(server).configureUsing(dataStore).start();
            waitForService();
            logger.info("Started service on server {}", server.getName());
        }
        return service;
    }

    public static void resetServerType() {
        serverType = System.getenv("SERVER_TYPE");
    }

    public static void setServerType(String serverType) {
        TestService.serverType = serverType;
    }

    public static void setServerTypeSpark() {
        serverType = SERVER_TYPE_SPARK;
    }

    public static void setServerTypeSpring() {
        serverType = SERVER_TYPE_SPRING;
    }

    public static void setServerTypeVertx() {
        serverType = SERVER_TYPE_VERTX;
    }

    public static void stopServiceInstance() {
        if (server != null) {
            server.stop();
            waitForShutdown();
        }
        dataStore = null;
        service = null;
        server = null;
    }

    public static void waitForService() {
          if (!TestUtil.waitForPortUp(DEFAULT_SERVER_HOST, DEFAULT_SERVER_PORT)) {
              throw new RuntimeException("Server did not start as expectded");
          }
    }

    public static void waitForShutdown() {
        if (!TestUtil.waitForPortDown(DEFAULT_SERVER_HOST, DEFAULT_SERVER_PORT)) {
            throw new RuntimeException("Server did not shutdown as expectded");
        }
    }

    // Don't let anyone else instantiate this class
    private TestService() {
    }

}
