package m.core.server;

import java.util.Map;
import java.util.function.Function;

import m.core.http.Method;
import m.core.http.Request;
import m.core.http.Response;

/**
 * An HTTP server for REST API routes.
 * <p>
 * This server handles the API routes provided by the
 * {@link m.core.service.Service} instances.
 *
 * When a service is deployed on a server using
 * {@link m.core.service.Service#configureUsing(Server)} the routes configured
 * by that service are automatically enabled.
 *
 * Multiple services can be deployed on a single server using
 * {@link m.core.service.Service#attach(m.core.service.Service)}.
 *
 */
public interface Server {

    /**
     * Returns the host this server is listening on.
     *
     * @return the host server listens on
     */
    public String getHost();

    /**
     * Returns the name of this server.
     *
     * @return the server name
     */
    public String getName();

    /**
     * Returns the port this server is listening on.
     *
     * @return the port server listens on
     */
    public int getPort();

    /**
     * Sets an API route handled by this server.
     * <p>
     * The routes are set to a handler method of a {@link m.core.service.Service}.
     *
     * @param method  the HTTP method
     * @param path    the path
     * @param handler the handler service method
     */
    public void setRoutes(Method method, String path, Function<Request, Response> handler);

    /**
     * Sets a group of API routes handled by this server.
     * <p>
     * The routes are set to handler methods of a {@link m.core.service.Service}.
     *
     * @param routeHandlerMap a {@code Map} of routes grouped by HTTP method
     */
    public void setRoutes(Map<Method, Map<String, Function<Request, Response>>> routeHandlerMap);

    /**
     * Starts the server.
     *
     * @return  true if started successfully, false otherwise
     */
    public boolean start();

    /**
     * Stops the server.
     */
    public void stop();

}
