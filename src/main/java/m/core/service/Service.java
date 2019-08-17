package m.core.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import m.core.data.DataStore;
import m.core.data.MemoryDataStore;
import m.core.data.cache.DataCache;
import m.core.data.queue.DataQueue;
import m.core.http.Method;
import m.core.http.Request;
import m.core.http.Response;
import m.core.server.Server;

/**
 * A REST API web service.
 * <p>
 * Extend this service to implement a REST API web service.
 *
 * <pre>
 * public class HelloService extends Service {
 *     private Response hello(Request request) {
 *         if (request.getPathParams().containsKey("name")) {
 *             return new Response("Hello " + request.getPathParams().get("name"));
 *         }
 *         return new Response("Hello world");
 *     }
 *     {@literal @}Override
 *     public void configure() {
 *        GET("/hello", this::hello);
 *        GET("/hello/{name}", this::hello);
 *     }
 * }
 * </pre>
 * <p>
 * Or using anonymous inner class
 *
 * <pre>
 * Service helloService = new Service() {
 *   private Response hello(Request request) {
 *   ...
 *   public void configure() {
 *   ...
 * };
 * </pre>
 * <p>
 * The subclass must implement the {@link Service#configure()} method and define
 * the REST API routes.
 *
 * <pre>
 * {@literal @}Override
 * public void configure() {
 *     GET("/user", this::getUser);
 *     GET("/user/{id}", this::getUser);
 *     POST("/user", this::createUser);
 *     PUT("/user/{id}", this::editUser);
 *     DELETE("/user/{id}", this::deleteUser);
 *     PATCH("/user/{id}", this::editUserProperties);
 *     OPTIONS("/user", UserService::optionsUser);
 *     HEAD("/user", UserService::headersUser);
 *     TRACE("/user", UserService::traceUser);
 * }
 * </pre>
 *
 * The service can then be deployed by configuring a {@link Server}.
 *
 * <pre>
 * Service helloService = new HelloService().configureUsing(new SparkServer("Hello Service")).start()
 * </pre>
 *
 * Or using an anonymous inner class
 *
 * <pre>
 * new Service() { ... }.configureUsing(new SpringServer("Hello Service")).start()
 * </pre>
 *
 * Other data services needed by this service like {@link DataStore},
 * {@link DataQueue}, {@link DataCache} can also be configured similarly before
 * {@code star()} using the same fluent {@code configureUsing(...)} style.
 *
 * <pre>
 * Service userService = new userService()
 *     .configureUsing(new VertxServer("User Service"))
 *     .configureUsing(new MapdbDataStore("data.test.mapdb.db"))
 *     .configureUsing(new RedisDataStoreCacheQueue()))
 *     .start()
 * </pre>
 *
 * Multiple services can be deployed to the same {@link Server} using
 * {@link Service#attach(Service)}.
 *
 * <pre>
 * Service workspaceService = new workspaceService().configureUsing(new SparkServer(" Service"))
 * 		.attach(new GroupService().configureUsing(new JDBCDataStore())).attach(new EnvironmentService()).start();
 * </pre>
 *
 */
public abstract class Service {

    private static Logger logger = LoggerFactory.getLogger(Service.class);

    protected DataCache dataCache;
    protected DataQueue dataQueue;
    protected DataStore dataStore = new MemoryDataStore();

    private void setCache(DataCache dataCache) {
        this.dataCache = dataCache;
    }

    private void setQueue(DataQueue dataQueue) {
        this.dataQueue = dataQueue;
    }

    private void setStore(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    private List<Service> attachedServices = new ArrayList<>();

    private Map<Method, Map<String, Function<Request, Response>>> routeHandlerMap = new HashMap<>();

    protected Server server;

    {
        Stream.of(Method.values()).forEach(e -> {
            routeHandlerMap.put(e, new HashMap<>());
        });
    }

    /**
     * Attach another service to this service.
     *
     * The attached services will get deployed on the same {@link Server} as this
     * service.
     *
     * @param service the other service to attach
     * @return this service
     */
    public Service attach(Service service) {
        attachedServices.add(service);
        service.configureUsing(server);
        return this;
    }

    /**
     * Configure the REST API routes.
     * <p>
     * Example: {@code POST("/user", this::createUser);}. The subclass must
     * implement this method and configure the REST API routes.
     *
     * <pre>
     * {@literal @}Override
     * public void configure() {
     *     GET("/user", this::getUser);
     *     GET("/user/{id}", this::getUser);
     *     POST("/user", this::createUser);
     *     PUT("/user/{id}", this::editUser);
     *     DELETE("/user/{id}", this::deleteUser);
     *     PATCH("/user/{id}", this::editUserProperties);
     *     OPTIONS("/user", UserService::optionsUser);
     *     HEAD("/user", UserService::headersUser);
     *     TRACE("/user", UserService::traceUser);
     * }
     * </pre>
     */
    public abstract void configure();

    private void configureRoutes(Server server) {
        configure();
        server.setRoutes(routeHandlerMap);
    }

    /**
     * Configure the data cache.
     * <p>
     * This service can use the configured {@link DataCache} as the data cache.
     *
     * @param dataCache the data cache
     * @return this service
     */
    public Service configureUsing(DataCache dataCache) {
        setCache(dataCache);
        return this;
    }

    /**
     * Configure the data queue.
     * <p>
     * This service can use the configured {@link DataQueue} as the data queue.
     *
     * @param dataQueue the data queue
     * @return this service
     */
    public Service configureUsing(DataQueue dataQueue) {
        setQueue(dataQueue);
        return this;
    }

    /**
     * Configure the data store.
     * <p>
     * This service can use the configured {@link DataStore} as the data store.
     *
     * @param dataStore the data store
     * @return this service
     */
    public Service configureUsing(DataStore dataStore) {
        setStore(dataStore);
        return this;
    }

    /**
     * Configure the server.
     * <p>
     * This service will be deployed on this web {@link Server}.
     *
     * @param server the server to deploy this service
     * @return the service
     */
    public Service configureUsing(Server server) {
        this.server = server;
        return this;
    }

    /**
     * Add API route for {@code DELETE} method.
     *
     * @param path    the path
     * @param handler the handler method in this service
     * @return this service
     */
    public Service DELETE(String path, Function<m.core.http.Request, m.core.http.Response> handler) {
        ROUTE(Method.DELETE, path, handler);
        return this;
    }

    /**
     * Add API route for {@code GET} method.
     *
     * @param path    the path
     * @param handler the handler method in this service
     * @return this service
     */
    public Service GET(String path, Function<m.core.http.Request, m.core.http.Response> handler) {
        ROUTE(Method.GET, path, handler);
        return this;
    }

    /**
     * Add API route for {@code POST} method.
     *
     * @param path    the path
     * @param handler the handler method in this service
     * @return this service
     */
    public Service POST(String path, Function<m.core.http.Request, m.core.http.Response> handler) {
        ROUTE(Method.POST, path, handler);
        return this;
    }

    /**
     * Add API route for {@code PUT} method.
     *
     * @param path    the path
     * @param handler the handler method in this service
     * @return this service
     */
    public Service PUT(String path, Function<m.core.http.Request, m.core.http.Response> handler) {
        ROUTE(Method.PUT, path, handler);
        return this;
    }

    /**
     * Add API route for {@code PATCH} method.
     *
     * @param path    the path
     * @param handler the handler method in this service
     * @return this service
     */
    public Service PATCH(String path, Function<m.core.http.Request, m.core.http.Response> handler) {
        ROUTE(Method.PATCH, path, handler);
        return this;
    }

    /**
     * Add API route for {@code OPTIONS} method.
     *
     * @param path    the path
     * @param handler the handler method in this service
     * @return this service
     */
    public Service OPTIONS(String path, Function<m.core.http.Request, m.core.http.Response> handler) {
        ROUTE(Method.OPTIONS, path, handler);
        return this;
    }

    /**
     * Add API route for {@code TRACE} method.
     *
     * @param path    the path
     * @param handler the handler method in this service
     * @return this service
     */
    public Service TRACE(String path, Function<m.core.http.Request, m.core.http.Response> handler) {
        ROUTE(Method.TRACE, path, handler);
        return this;
    }

    /**
     * Add API route for {@code HEAD} method.
     *
     * @param path    the path
     * @param handler the handler method in this service
     * @return this service
     */
    public Service HEAD(String path, Function<m.core.http.Request, m.core.http.Response> handler) {
        ROUTE(Method.HEAD, path, handler);
        return this;
    }

    /**
     * Add API route for an HTTP method.
     *
     * @param method  the HTTP method
     * @param path    the path
     * @param handler the handler method in this service
     * @return this service
     */
    public Service ROUTE(Method method, String path, Function<m.core.http.Request, m.core.http.Response> handler) {
        routeHandlerMap.get(method).put(path, handler);
        return this;
    }

    /**
     * Start this service.
     * <p>
     * Start this service with the configured {@link Server}.
     *
     * @return this service
     */
    public Service start() {
        if (server == null) {
            throw new RuntimeException("Please configure a server before starting the service");
        }
        logger.info("Starting service using server {}, data store {}, data cache {}, data queue {}",
                server.getClass().getName(), dataStore == null ? "NULL" : dataStore.getClass().getName(),
                dataCache == null ? "NULL" : dataCache.getClass().getName(),
                dataQueue == null ? "NULL" : dataQueue.getClass().getName());
        configureRoutes(server);
        attachedServices.forEach(service -> {
            ((Service) service).configureRoutes(server);
        });
        server.start();
        return this;
    }

    /**
     * Stop this service.
     */
    public void stop() {
        if (server != null) {
            server.stop();
        }
    }

}
