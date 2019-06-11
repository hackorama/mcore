package com.hackorama.mcore.common;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import com.hackorama.mcore.server.Server;
import com.hackorama.mcore.server.spark.SparkServer;
import com.hackorama.mcore.server.spring.SpringServer;
import com.hackorama.mcore.server.vertx.VertxServer;

/**
 * Simple convenience server for quick service tests
 *
 * @author Kishan Thomas (kishan.thomas@gmail.com)
 *
 */
public class TestServer {
    private static Logger logger = LoggerFactory.getLogger(TestServer.class);

    private static final int DEFAULT_SERVER_PORT = 7654;
    private static final String DEFAULT_SERVER_ENDPOINT = "http://127.0.0.1:" + DEFAULT_SERVER_PORT;
    private static final String SERVER_TYPE_SPARK = "SPARK";
    private static final String SERVER_TYPE_SPRING = "SPRING";
    private static final String SERVER_TYPE_VERTX = "VERTX";
    private static final List<String> serverTypes = Arrays.asList(SERVER_TYPE_SPRING, SERVER_TYPE_SPARK,
            SERVER_TYPE_VERTX);
    private static String serverType = System.getenv("SERVER_TYPE");
    private static Server server = null;

    public synchronized static void awaitShutdown() {
        if (server != null) {
            server.stop();
            waitForShutdown();
            server = null;
        }
    }

    public static void awaitStartup() {
        if (!TestUtil.waitForPort("127.0.0.1", DEFAULT_SERVER_PORT, 60)) {
            throw new RuntimeException("Server did not start as expectded");
        }
    }

    public static Server createNewServer() {
        if (SERVER_TYPE_SPRING.equalsIgnoreCase(serverType)) {
            return createNewSpringServer();
        } else if (SERVER_TYPE_VERTX.equalsIgnoreCase(serverType)) {
            return createNewVertxServer();
        } else {
            return createNewSparkServer();
        }
    }

    public static Server createNewSparkServer() {
        awaitShutdown();
        server = new SparkServer("Spark test server", DEFAULT_SERVER_PORT);
        logger.info("Created Spark Server {} on {}", server.getName(), DEFAULT_SERVER_PORT);
        return server;
    }

    public static Server createNewSpringServer() {
        awaitShutdown();
        server = new SpringServer("Spring test server", DEFAULT_SERVER_PORT);
        logger.info("Created Spring Server {} on {}", server.getName(), DEFAULT_SERVER_PORT);
        return server;
    }

    public static Server createNewVertxServer() {
        awaitShutdown();
        server = new VertxServer("Vertx test server", DEFAULT_SERVER_PORT);
        logger.info("Created Vertx Server {} on {}", server.getName(), DEFAULT_SERVER_PORT);
        return server;
    }

    public static String getServerType() {
        return serverType;
    }

    public static Iterable<? extends Object> getServerTypeList() {
        return serverTypes;
    }

    public static void setServerType(String serverType) {
        TestServer.serverType = serverType;
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

    public static boolean validResponse(String url, String body) throws UnirestException {
        return body.equals(Unirest.get(DEFAULT_SERVER_ENDPOINT + url).asString().getBody());
    }

    private static void waitForShutdown() {
        if (!TestUtil.waitOnPort("127.0.0.1", DEFAULT_SERVER_PORT, 60)) {
            throw new RuntimeException("Server did not shutdown as expectded");
        }
    }

}
