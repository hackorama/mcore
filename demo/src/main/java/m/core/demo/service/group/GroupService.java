package m.core.demo.service.group;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import m.core.demo.common.Util;
import m.core.http.Request;
import m.core.http.Response;
import m.core.service.Service;

public class GroupService extends Service {

    private static final String STORE_NAME = "GROUP";
    private final Gson GSON = new Gson();

    public Response createGroup(Request request) {
        return editGroup(request);
    }

    public Response deleteGroup(Request request) {
        dataStore.remove(STORE_NAME, request.getParam("id"));
        return new Response("");
    }

    public Response editGroup(Request request) {
        Group group = GSON.fromJson(request.getBody(), Group.class);
        String id = request.getParam("id");
        if (id != null) { // updating existing
            group.setId(id);
        } else { // adding as new
            group.setId();
        }
        dataStore.put(STORE_NAME, group.getId(), GSON.toJson(group));
        return new Response(GSON.toJson(group));
    }

    public Response getGroup(Request request) {
        String id = request.getParam("id");
        if (id == null) {
            List<Group> groups = new ArrayList<>();
            for (String data : dataStore.get(STORE_NAME)) {
                groups.add(GSON.fromJson(data, Group.class));
            }
            return new Response(GSON.toJson(groups));
        } else if (dataStore.contains(STORE_NAME, id)) {
            return new Response(dataStore.get(STORE_NAME, id));
        }
        return new Response(Util.toJsonString("error", "Invalid Group"), HttpURLConnection.HTTP_BAD_REQUEST);
    }

    @Override
    public void configure() {
        GET("/group", this::getGroup);
        GET("/group/{id}", this::getGroup);
        POST("/group", this::createGroup);
        PUT("/group/{id}", this::editGroup);
        DELETE("/group/{id}", this::deleteGroup);
    }

}
