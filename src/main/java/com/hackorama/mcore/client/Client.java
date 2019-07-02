package com.hackorama.mcore.client;


import com.hackorama.mcore.common.Response;

/**
 * HTTP client interface
 *
 * @author Kishan Thomas (kishan.thomas@gmail.com)
 *
 */
public interface Client {

    /**
     * Get the response from the given URL as JSON, the default format
     *
     * @param url
     *            The URL to get the response from
     * @return
     */
    public Response get(String url);

    /**
     * Get the response from the given URL as String
     *
     * @param url
     *            The URL to get the response from
     * @return
     */
    public Response getAsString(String url);

    /**
     * Post a request to the given URL and get the response
     *
     * @param url
     *            The URL to post the request get the response from
     * @param body
     *            The response
     * @return
     */
    public Response post(String url, String body);

    /**
     * Set the timeouts for the client
     *
     * @param connectionTimeoutMilliSecs
     *            Connection timeout in milli seconds
     * @param socketTimeoutMilliSecs
     *            Socket timeout in milli seconds
     */
    public void setTimeOuts(int connectionTimeoutMilliSecs, int socketTimeoutMilliSecs);

}
