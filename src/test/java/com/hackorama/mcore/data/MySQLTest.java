package com.hackorama.mcore.data;

import static org.junit.Assert.assertEquals;

import java.net.HttpURLConnection;
import java.sql.SQLException;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import com.hackorama.mcore.common.HelloService;
import com.hackorama.mcore.data.jdbc.JDBCDataStore;
import com.hackorama.mcore.server.spark.SparkServer;
import com.hackorama.mcore.service.Service;

/**
 *
 * Basic MySQL/MariaDB connection tests, requires MySQL server on localhost with
 * test database and test user created.
 *
 * @author Kishan Thomas (kishan.thomas@gmail.com)
 *
 */
public class MySQLTest {

    private static final String DEFAULT_SERVER_ENDPOINT = "http://127.0.0.1:8080" ;

    public static void main(String[] args) throws SQLException, UnirestException {
        if (args.length < 1) {
            System.out.println("Usage: java MySQLTest <db_password>");
            System.exit(1);
        }
        Service service = new HelloService()
                .configureUsing(
                        new JDBCDataStore("jdbc:mysql://localhost/test", "com.mysql.cj.jdbc.Driver", "test", args[0]))
                .configureUsing(new SparkServer("test")).start();
        HttpResponse<JsonNode> response = Unirest.post(DEFAULT_SERVER_ENDPOINT + "/group")
                .header("accept", "application/json").body("{ \"name\" : \"one\" }").asJson();
        assertEquals(HttpURLConnection.HTTP_OK, response.getStatus());
        response = Unirest.get(DEFAULT_SERVER_ENDPOINT + "/group").header("accept", "application/json").asJson();
        System.out.println(response.getBody().toString());
        service.stop();
    }

}
