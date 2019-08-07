package m.core.samples;

import com.hackorama.mcore.http.Request;
import com.hackorama.mcore.http.Response;
import com.hackorama.mcore.server.spark.SparkServer;
import com.hackorama.mcore.service.Service;

public class HelloApp {
	public static void main(String[] args) {
		new Service() {

			public Response hello(Request request) {
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
