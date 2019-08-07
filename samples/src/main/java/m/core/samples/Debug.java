package m.core.samples;

import com.hackorama.mcore.http.Request;
import com.hackorama.mcore.http.Response;
import com.hackorama.mcore.server.spark.SparkServer;
import com.hackorama.mcore.service.Service;

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

            public Response test(Request request) {
                com.hackorama.mcore.common.Debug.print(request);
                Response response = new Response("DEBUG");
                com.hackorama.mcore.common.Debug.print(response);
                return response;
            }

        }.configureUsing(new SparkServer("Debug")).start();
    }
}
