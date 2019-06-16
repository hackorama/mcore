package com.hackorama.mcore.common;

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
public class TestServer extends Test {

    private static Logger logger = LoggerFactory.getLogger(TestServer.class);

    private static final int DEFAULT_SERVER_PORT = 7654; //TODO Check if the port need to be unique from TestService
    private static final String DEFAULT_SERVER_ENDPOINT = "http://" + DEFAULT_SERVER_HOST + ":" + DEFAULT_SERVER_PORT;

    public synchronized static void awaitShutdown() {
        if (server != null) {
            server.stop();
            waitForShutdown();
            server = null;
        }
    }

    public static void awaitStartup() {
        if (!TestUtil.waitForPort(DEFAULT_SERVER_HOST, DEFAULT_SERVER_PORT, 60)) {
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

    public static boolean isSparkServer() {
        return SERVER_TYPE_SPARK.equals(serverType);
    }

    public static boolean isSpringServer() {
        return SERVER_TYPE_SPRING.equals(serverType);
    }

    public static boolean isVertxServer() {
        return SERVER_TYPE_VERTX.equals(serverType);
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

    public static boolean validResponseCode(String url, int code) throws UnirestException {
        return code == Unirest.get(DEFAULT_SERVER_ENDPOINT + url).asString().getStatus();
    }

    private static void waitForShutdown() {
        if (!TestUtil.waitOnPort(DEFAULT_SERVER_HOST, DEFAULT_SERVER_PORT, 60)) {
            throw new RuntimeException("Server did not shutdown as expectded");
        }
    }

    // Don't let anyone else instantiate this class
    private TestServer() {
    }

}
