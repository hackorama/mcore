package com.hackorama.mcore.demo;

import java.util.concurrent.TimeUnit;

import com.hackorama.mcore.client.unirest.UnirestClient;
import com.hackorama.mcore.common.Debug;
import com.hackorama.mcore.common.Request;
import com.hackorama.mcore.common.Response;
import com.hackorama.mcore.common.SuppressFBWarnings;
import com.hackorama.mcore.server.spark.SparkServer;
import com.hackorama.mcore.service.BaseService;
import com.hackorama.mcore.service.Service;

public class Session {

    public static void main(String[] args) throws InterruptedException {
        Service service = new BaseService() {

            @Override
            public void configure() {
                GET("/start/{name}", this::start);
                GET("/test", this::test);
                GET("/end", this::end);
            }

            @SuppressFBWarnings // Ignore invalid UMAC warning, method is accessed by Function interface
            public Response end(Request request) {
                Debug.print(request);
                Response response = new Response("Ended session for: " + request.getSession().getAttribute("NAME"));
                request.getSession().removeAttribute("NAME"); // Not required if invalidating, like in this case
                request.getSession().invalidate();
                Debug.print(response);
                return response;
            }

            @SuppressFBWarnings // Ignore invalid UMAC warning, method is accessed by Function interface
            public Response start(Request request) {
                Debug.print(request);
                request.getSession().setAttribute("NAME", request.getPathParams().get("name"));
                Response response = new Response("Started session for: " + request.getSession().getAttribute("NAME"));
                Debug.print(response);
                return response;
            }

            @SuppressFBWarnings // Ignore invalid UMAC warning, method is accessed by Function interface
            public Response test(Request request) {
                Debug.print(request);
                Response response = null;
                if (request.getSession().getAttribute("NAME") == null
                        || request.getSession().getAttribute("NAME").toString().isEmpty()) {
                    response = new Response("Not in a session");
                } else {
                    response = new Response("In session for: " + request.getSession().getAttribute("NAME"));
                }
                Debug.print(response);
                return response;
            }

        }.configureUsing(new SparkServer("Session")).start();

        Thread.sleep(TimeUnit.SECONDS.toMillis(3)); // wait for server to initialize

        System.out.println("Testing session ...");

        UnirestClient client = new UnirestClient();
        Debug.disable();
        System.out.println(client.getAsString("http://localhost:8080/test").getBody());
        System.out.println(client.getAsString("http://localhost:8080/start/mcore").getBody());
        System.out.println(client.getAsString("http://localhost:8080/test").getBody());
        System.out.println(client.getAsString("http://localhost:8080/end").getBody());
        System.out.println(client.getAsString("http://localhost:8080/test").getBody());
        service.stop();
    }
}
