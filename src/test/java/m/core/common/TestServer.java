package m.core.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import m.core.server.Server;
import m.core.server.play.PlayServer;
import m.core.server.spark.SparkServer;
import m.core.server.spring.SpringServer;
import m.core.server.vertx.VertxServer;

/**
 * Simple convenience server for quick service tests
 *
 * @author Kishan Thomas (kishan.thomas@gmail.com)
 *
 */
public class TestServer extends Test {

    private static Logger logger = LoggerFactory.getLogger(TestServer.class);

    private static final BasicCookieStore cookieStore = new BasicCookieStore();

    private static final int DEFAULT_SERVER_PORT = 7654; // TODO Check if the port need to be unique from TestService
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

    public static void clearCookies() {
        cookieStore.clear();
    }

    public static Server createNewPlayServer() {
        awaitShutdown();
        server = new PlayServer("Play test server", DEFAULT_SERVER_PORT);
        logger.info("Created Play Server {} on {}", server.getName(), DEFAULT_SERVER_PORT);
        return server;
    }

    public static Server createNewServer() {
        if (SERVER_TYPE_SPRING.equalsIgnoreCase(serverType)) {
            return createNewSpringServer();
        } else if (SERVER_TYPE_VERTX.equalsIgnoreCase(serverType)) {
            return createNewVertxServer();
        } else if (SERVER_TYPE_PLAY.equalsIgnoreCase(serverType)) {
            return createNewPlayServer();
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

    public static void debugCookies() {
        Debug.print(cookieStore);
    }

    public static List<Cookie> getCookies() {
        return cookieStore.getCookies();
    }

    public static String getEndPoint() {
        return DEFAULT_SERVER_ENDPOINT;
    }

    public static HttpResponse<String> getResponse() throws UnirestException {
        return Unirest.get(DEFAULT_SERVER_ENDPOINT).asString();
    }

    public static HttpResponse<String> getResponse(String path) throws UnirestException {
        return Unirest.get(DEFAULT_SERVER_ENDPOINT + path).asString();
    }

    public static Map<String, List<String>> getResponseHeaders(String path) throws UnirestException {
        return getResponseHeaders(path, new HashMap<String, String>());
    }

    public static Map<String, List<String>> getResponseHeaders(String path, Map<String, String> headers)
            throws UnirestException {
        HttpResponse<String> response = Unirest.get(DEFAULT_SERVER_ENDPOINT + path).headers(headers).asString();
        return response.getHeaders();
    }

    public static String getServerType() {
        return serverType;
    }

    public static List<String> getServerTypeList() {
        return serverTypes;
    }

    public static String getServerTypePlay() {
        return SERVER_TYPE_PLAY;
    }

    public static String getServerTypeSpark() {
        return SERVER_TYPE_SPARK;
    }

    public static String getServerTypeSpring() {
        return SERVER_TYPE_SPRING;
    }

    public static String getServerTypeVertx() {
        return SERVER_TYPE_VERTX;
    }

    public static boolean isPlayServer() {
        return SERVER_TYPE_PLAY.equals(serverType);
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

    public static void setServerTypePlay() {
        serverType = SERVER_TYPE_PLAY;
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

    public static void useCookies() {
        Unirest.setHttpClient(HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build())
                .setDefaultCookieStore(cookieStore).build());
    }

    public static boolean validResponse(String url, String body) throws UnirestException {
        return body.equals(Unirest.get(DEFAULT_SERVER_ENDPOINT + url).asString().getBody());
    }

    public static boolean validResponseCode(String url, int code) throws UnirestException {
        return code == Unirest.get(DEFAULT_SERVER_ENDPOINT + url).asString().getStatus();
    }

    public static boolean validResponseCode(String url, Map<String, String> headers, int code) throws UnirestException {
        return code == Unirest.get(DEFAULT_SERVER_ENDPOINT + url).headers(headers).asString().getStatus();
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
