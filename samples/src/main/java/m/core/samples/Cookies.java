package m.core.samples;

import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;

import m.core.client.unirest.CookieUnirestClient;
import m.core.common.Debug;
import m.core.http.Request;
import m.core.http.Response;
import m.core.server.spark.SparkServer;
import m.core.service.Service;

public class Cookies {

    public static void main(String[] args) throws InterruptedException {
        Service service = new Service() {

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
                Debug.print(request);
                Response response = new Response("COOKIE TEST");
                Cookie cookie = new Cookie("FUN", "SUMMER");
                cookie.setPath("/test");
                response.setCookie(cookie);
                Debug.print(response);
                return response;
            }

        }.configureUsing(new SparkServer("Debug")).start();

        Thread.sleep(TimeUnit.SECONDS.toMillis(3)); // wait for server to initialize

        System.out.println("Testing cookies ...");

        CookieUnirestClient client = new CookieUnirestClient();
        Debug.print(client.getCookieStore());
        client.get("http://localhost:8080/test");
        Debug.print(client.getCookieStore());
        client.get("http://localhost:8080/test/cookie");
        Debug.print(client.getCookieStore());

        service.stop();
    }
}
