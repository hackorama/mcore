package com.hackorama.mcore.client.unirest;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import com.hackorama.mcore.client.Client;
import com.hackorama.mcore.common.Response;
import com.hackorama.mcore.common.Util;
import com.hackorama.mcore.config.Configuration;

public class UnirestClient implements Client {

    private static Logger logger = LoggerFactory.getLogger(UnirestClient.class);
    int connectTimeoutMillis = Configuration.clientConfig().clientConnectTimeoutMillis();
    int socketTimeoutMillis = Configuration.clientConfig().clientSocketTimeoutMillis();

    public UnirestClient() {
        setTimeOuts(connectTimeoutMillis, socketTimeoutMillis);
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
        this.connectTimeoutMillis = connectTimeoutMillis;
        this.socketTimeoutMillis = socketTimeoutMillis;
        //Unirest.setTimeouts(connectTimeoutMillis, socketTimeoutMillis);
    }

}
