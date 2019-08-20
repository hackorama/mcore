package m.core.client;

import m.core.http.Response;

/**
 * An HTTP client.
 * <p>
 * A simple HTTP client to do basic {@code HTTP GET} and {@code HTTP POST}.
 */
public interface Client {

    /**
     * Returns the {@code Response} from the specified URL using {@code HTTP GET}.
     * <p>
     * The {@link Response} body returned is expected to be JSON formatted by default.
     *
     * @param url the URL
     * @return the server response
     */
    public Response get(String url);

    /**
     * Returns the {@code Response} from the specified URL using {@code HTTP GET}.
     * <p>
     * The {@link Response} body will be in text format, does not have to be JSON formatted.
     *
     * @param url the URL
     * @return the server response
     */
    public Response getAsString(String url);

    /**
     * Returns the {@code Response} from the specified URL after sending a {@code Request} with the specified body using {@code HTTP POST}.
     *
     * @param url  the URL
     * @param body the request body
     * @return the server response
     */
    public Response post(String url, String body);

    /**
     * Sets the specified connection timeouts for this client.
     *
     * @param connectionTimeoutMilliSecs the connection timeout in milliseconds
     * @param socketTimeoutMilliSecs     the socket timeout in milliseconds
     */
    public void setTimeOuts(int connectionTimeoutMilliSecs, int socketTimeoutMilliSecs);

}
