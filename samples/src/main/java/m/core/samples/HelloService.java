package m.core.samples;

import m.core.http.Request;
import m.core.http.Response;
import m.core.service.Service;

public class HelloService extends Service {

    private static Response getHello(Request request) {
        String name = request.getPathParams().get("name");
        if (name != null) {
            return new Response("hello " + name);
        } else {
            return new Response("hello world");
        }
    }

    @Override
    public void configure() {
        GET("/hello", HelloService::getHello);
        GET("/hello/{name}", HelloService::getHello);
    }

}
