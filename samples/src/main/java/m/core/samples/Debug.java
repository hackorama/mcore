package m.core.samples;

import m.core.http.Request;
import m.core.http.Response;
import m.core.server.spark.SparkServer;
import m.core.service.Service;

public class Debug {

    public static void main(String[] args) {
        new Service() {

            @Override
            public void configure() {
                GET("/test", this::test);
                GET("/test/", this::test);
                GET("/test/{test}", this::test);
                GET("/test/{test}/", this::test);
                POST("/test", this::test);
                POST("/test/", this::test);
                POST("/test/{test}", this::test);
                POST("/test/{test}/", this::test);
            }

            private Response test(Request request) {
                m.core.common.Debug.print(request);
                Response response = new Response("DEBUG");
                m.core.common.Debug.print(response);
                return response;
            }

        }.configureUsing(new SparkServer("Debug")).start();
    }
}
