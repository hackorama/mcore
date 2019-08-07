package m.core.samples;

import com.hackorama.mcore.http.Request;
import com.hackorama.mcore.http.Response;
import com.hackorama.mcore.server.spark.SparkServer;
import com.hackorama.mcore.service.Service;

public class Demo {

    public static void main(String[] args) {
        new Service() {

            public Response demo(Request request) {
                return new Response("DEMO");
            }

            @Override
            public void configure() {
                GET("/demo", this::demo);
            }

        }.configureUsing(new SparkServer("Demo")).start();
    }
}
