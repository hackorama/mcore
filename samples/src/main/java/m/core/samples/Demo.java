package m.core.samples;

import m.core.http.Request;
import m.core.http.Response;
import m.core.server.spark.SparkServer;
import m.core.service.Service;

public class Demo {
    public static void main(String[] args) {
        new Service() {

           private Response hello(Request request) {
                if (request.getPathParams().containsKey("name")) {
                    return new Response("Hello " + request.getPathParams().get("name"));
                }
                return new Response("Hello world");
            }

            @Override
            public void configure() {
                GET("/hello", this::hello);
                GET("/hello/{name}", this::hello);
            }

        }.configureUsing(new SparkServer("Hello")).start();
    }
}
