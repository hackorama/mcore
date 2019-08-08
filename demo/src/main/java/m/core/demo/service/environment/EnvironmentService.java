package m.core.demo.service.environment;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import m.core.http.Request;
import m.core.http.Response;
import m.core.server.Server;
import m.core.service.Service;

public class EnvironmentService extends Service {

    private final String STORE_NAME = "ENVIRONMENT";
    private final Gson GSON = new Gson();
    private Server server;

    @Override
    public Service attach(Service service) {
        service.configureUsing(server);
        return this;
    }

    public Response createEnvironment(Request request) {
        return editEnvironment(request);
    }

    public Response deleteEnvironment(Request request) {
        dataStore.remove(STORE_NAME, request.getParam("id"));
        return new Response("");
    }

    public Response editEnvironment(Request request) {
        Gson gson = GSON;
        Environment environment = gson.fromJson(request.getBody(), Environment.class);
        String id = request.getParam("id");
        if (id != null) { // updating existing
            environment.setId(id);
        } else { // adding as new
            environment.setId();
        }
        dataStore.put(STORE_NAME, environment.getId(), GSON.toJson(environment));
        return new Response(gson.toJson(environment));
    }

    public Response getEnvironment(Request request) {
        String id = request.getParam("id");
        if (id != null) {
            return new Response(dataStore.get(STORE_NAME, request.getParam("id")));
        } else {
            List<Environment> environments = new ArrayList<>(); // TODO use gson parsing
            for (String data : dataStore.get(STORE_NAME)) {
                environments.add(GSON.fromJson(data, Environment.class));
            }
            return new Response(GSON.toJson(environments));
        }
    }

    @Override
    public void configure() {
        GET("/environment", this::getEnvironment);
        GET("/environment/{id}", this::getEnvironment);
        POST("/environment", this::createEnvironment);
        PUT("/environment/{id}", this::editEnvironment);
        DELETE("/environment/{id}", this::deleteEnvironment);
    }

}
