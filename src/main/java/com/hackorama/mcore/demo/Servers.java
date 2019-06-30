package com.hackorama.mcore.demo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;

import com.hackorama.mcore.client.unirest.CookieUnirestClient;
import com.hackorama.mcore.common.Request;
import com.hackorama.mcore.common.Response;
import com.hackorama.mcore.common.SuppressFBWarnings;
import com.hackorama.mcore.server.Server;
import com.hackorama.mcore.server.spark.SparkServer;
import com.hackorama.mcore.server.spring.SpringServer;
import com.hackorama.mcore.server.vertx.VertxServer;
import com.hackorama.mcore.service.BaseService;
import com.hackorama.mcore.service.Service;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class Servers {

	public static void main(String[] args) throws InterruptedException, UnirestException {
		Map<String, List<org.apache.http.cookie.Cookie>> cookieResults = new HashMap<>();
		cookieResults.put("SPARK", runOnServer(new SparkServer("Spark")));
		cookieResults.put("SPRING", runOnServer(new SpringServer("Spring")));
		cookieResults.put("VERTX (Behaviour differs for multi value)", runOnServer(new VertxServer("Vertx")));
		cookieResults.forEach((k, v) -> {
			System.out.println(k);
			v.forEach(e -> {
				System.out.println("  " + e.getName() + " = " + e.getValue());
			});
			System.out.println();
		});
	}

	public static List<org.apache.http.cookie.Cookie> runOnServer(Server server)
			throws InterruptedException, UnirestException {

		System.out.println("Running on " + server.getName() + " server ...");

		Service service = new BaseService() {

			@Override
			public void configure() {
				GET("/test", this::test);
			}

			@SuppressFBWarnings // Ignore invalid UMAC warning, method is accessed by Function interface
			public Response test(Request request) {
				Response response = new Response("COOKIES");
				Cookie cookie = new Cookie("RESPONSE_COOKIE_SINGLE_VALUE", "ONE");
				response.setCookie(cookie);
				cookie = new Cookie("RESPONSE_COOKIE_MULTI_VALUE", "FIRST");
				cookie.setPath("/first");
				response.setCookie(cookie);
				cookie = new Cookie("RESPONSE_COOKIE_MULTI_VALUE", "SECOND");
				cookie.setPath("/second");
				response.setCookie(cookie);
				request.getCookies().forEach((k, v) -> {
					v.forEach(response::setCookie);
				});
				return response;
			}

		}.configureUsing(server).start();

		Thread.sleep(TimeUnit.SECONDS.toMillis(3)); // Wait for server to initialize

		CookieUnirestClient client = new CookieUnirestClient(); // Sets up cookie jar
		client.clearCookies();
		Unirest.get("http://localhost:8080/test").header("Cookie",
				"REQUEST_SINGLE_VALUE=ONE;REQUEST_MULTI_VALUE=FIRST;REQUEST_MULTI_VALUE=SECOND;REQUEST_MULTI_VALUE=LAST;REQUEST_DUPLICATE=SAME;REQUEST_DUPLICATE=SAME")
				.asString().getStatus();
		service.stop();
		return client.getCookies();
	}
}
