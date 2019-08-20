package m.core.client.unirest;


import org.apache.http.client.config.RequestConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import m.core.client.Client;
import m.core.common.Util;
import m.core.config.Configuration;
import m.core.http.Response;

/**
 * An HTTP client based on Unirest.
 *
 */
public class UnirestClient implements Client {

    private static Logger logger = LoggerFactory.getLogger(UnirestClient.class);
    protected RequestConfig requestConfig;

    /**
     * Constructs a Unirest based HTTP client.
     */
    public UnirestClient() {
        setTimeOuts(Configuration.clientConnectTimeoutMillis(), Configuration.clientSocketTimeoutMillis());
    }

    @Override
    public Response get(String url) {
        Response response = new Response();
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(url).header("accept", "application/json").asJson();
            response.setBody(jsonResponse.getBody().toString());
            response.setStatus(jsonResponse.getStatus());
        } catch (UnirestException e) {
            logger.debug("Error accessing {}", url, e);
            response.setBody(Util.toJsonString("Unirest Client Error", e.getMessage()));
            response.setStatus(0);
        }
        return response;
    }

    @Override
    public Response getAsString(String url) {
        Response response = new Response();
        try {
            HttpResponse<String> jsonResponse = Unirest.get(url).asString();
            response.setBody(jsonResponse.getBody());
            response.setStatus(jsonResponse.getStatus());
        } catch (UnirestException e) {
            logger.debug("Error accessing {}", url, e);
            response.setBody("Unirest Client Error: " + e.getMessage());
            response.setStatus(0);
        }
        return response;
    }

    @Override
    public Response post(String url, String body) {
        Response response = new Response();
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.post(url).header("accept", "application/json").body(body).asJson();
            response.setBody(jsonResponse.getBody().toString());
            response.setStatus(jsonResponse.getStatus());
        } catch (UnirestException e) {
            logger.debug("Error accessing {}", url, e);
            response.setBody(Util.toJsonString("error", e.getMessage()));
            response.setStatus(0);
        }
        return response;
    }

    @Override
    public void setTimeOuts(int connectTimeoutMillis, int socketTimeoutMillis) {
        // TODO Validate Unirest.setTimeouts(connectTimeoutMillis, socketTimeoutMillis);
        requestConfig = RequestConfig.custom().setConnectTimeout(connectTimeoutMillis)
                .setSocketTimeout(socketTimeoutMillis).build();
    }

}
