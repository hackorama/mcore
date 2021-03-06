package m.core.samples;

import java.util.concurrent.TimeUnit;

import m.core.client.unirest.UnirestClient;
import m.core.common.Debug;
import m.core.http.Request;
import m.core.http.Response;
import m.core.server.spark.SparkServer;
import m.core.service.Service;

public class Session {

    public static void main(String[] args) throws InterruptedException {
        Service service = new Service() {

            @Override
            public void configure() {
                GET("/start/{name}", this::start);
                GET("/test", this::test);
                GET("/end", this::end);
            }

            private Response end(Request request) {
                Debug.print(request);
                Response response = new Response("Ended session for: " + request.getSession().getAttribute("NAME"));
                request.getSession().removeAttribute("NAME"); // Not required if invalidating, like in this case
                request.getSession().invalidate();
                Debug.print(response);
                return response;
            }

            private Response start(Request request) {
                Debug.print(request);
                request.getSession().setAttribute("NAME", request.getPathParams().get("name"));
                Response response = new Response("Started session for: " + request.getSession().getAttribute("NAME"));
                Debug.print(response);
                return response;
            }

            private Response test(Request request) {
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
