package com.hackorama.mcore.service.workspace;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import com.hackorama.mcore.client.Client;
import com.hackorama.mcore.client.unirest.CachingUnirestClient;
import com.hackorama.mcore.common.HttpMethod;
import com.hackorama.mcore.common.Request;
import com.hackorama.mcore.common.Response;
import com.hackorama.mcore.common.Util;
import com.hackorama.mcore.config.Configuration;
import com.hackorama.mcore.data.DataStore;
import com.hackorama.mcore.data.MemoryDataStore;
import com.hackorama.mcore.server.Server;
import com.hackorama.mcore.service.Service;
import com.hackorama.mcore.service.group.Group;

public class WorkspaceService implements Service {

    private static Logger logger = LoggerFactory.getLogger(WorkspaceService.class);
    private static final String WORKSPACE_STORE = "WORKSPACE";
    private static final String WORKSPACES_GROUPS_STORE = "WORKSPACES_GROUPS";
    private static final String ENIVRNONMENTS_WORKSPACE_STORE = "ENVIRONMENTS_WORKSPACE";
    private static final Gson GSON = new Gson();
    private static DataStore dataStore = new MemoryDataStore();
    private static Server server;
    private static Client client = new CachingUnirestClient();
    @Override
    public Service attach(Service service) {
        service.configureUsing(server);
        return this;
    }

    private static String environmentServiceURL;
    private static String groupServiceURL;

    public static Response addGroup(Request request) {
        String workspaceId = request.getParams().get("id");
        String groupId = request.getParams().get("groupid");
        if (workspaceId != null && groupId != null && dataStore.contains(WORKSPACE_STORE, workspaceId)
                && !serviceAvailableAndIsAnInvalidGroup(groupId)) {
            dataStore.putMultiKey(WORKSPACES_GROUPS_STORE, workspaceId, groupId); // Many to Many
            return new Response(
                    Util.toJsonString("message", "Added Group " + groupId + " to Workspace " + workspaceId));
        }
        return new Response(Util.toJsonString("error", "Invalid Workspace or Group"),
                HttpURLConnection.HTTP_BAD_REQUEST);
    }

    public static Response createWorkspace(Request request) {
        return editWorkspace(request);
    }

    public static Response deleteWorkspace(Request request) {
        dataStore.remove(WORKSPACE_STORE, request.getParams().get("id"));
        return new Response("");
    }

    public static Response editWorkspace(Request request) {
        Gson gson = GSON;
        Workspace workspace = gson.fromJson(request.getBody(), Workspace.class);
        String id = request.getParams().get("id");
        if (id == null) { // adding as new
            workspace.setId();
        } else if (dataStore.contains(WORKSPACE_STORE, id)) { // updating existing
            workspace.setId(id);
        } else {
            return new Response(Util.toJsonString("error", "Invalid Workspace Id"), HttpURLConnection.HTTP_BAD_REQUEST);
        }
        dataStore.put(WORKSPACE_STORE, workspace.getId(), GSON.toJson(workspace));
        return new Response(GSON.toJson(insertOwnerGroups(workspace)));
    }

    public static Response getComponent(Request request) {
        String id = request.getParams().get("id");
        if (id != null) {
            Map<String, List<String>> nodes = new HashMap<>();
            if (dataStore.contains(WORKSPACE_STORE, id)) {
                List<String> environmentList = dataStore.getByValue(ENIVRNONMENTS_WORKSPACE_STORE, id);
                nodes.put("environments", environmentList);
                return new Response(GSON.toJson(nodes));
            }
        } else {
            Map<String, Set<String>> nodes = new HashMap<>();
            Set<String> workspaceList = dataStore.getKeys(WORKSPACE_STORE);
            nodes.put("workspaces", workspaceList);
            return new Response(GSON.toJson(nodes));
        }
        return new Response(Util.toJsonString("error", "Invalid Component"), HttpURLConnection.HTTP_BAD_REQUEST);
    }

    private static Group getGroup(String groupId) {
        Response response = client.get(groupServiceURL + "/group/" + groupId);
        if (response.getStatus() == HttpURLConnection.HTTP_OK) {
            return GSON.fromJson(response.getBody(), Group.class);
        }
        return null;
    }

    public static Response getWorkspace(Request request) {
        String id = request.getParams().get("id");
        if (id == null) {
            List<Workspace> workspaces = new ArrayList<>(); // TODO use gson parsing
            for (String data : dataStore.get(WORKSPACE_STORE)) {
                workspaces.add(insertOwnerGroups(GSON.fromJson(data, Workspace.class)));
            }
            return new Response(GSON.toJson(workspaces));
        } else if (dataStore.contains(WORKSPACE_STORE, id)) {
            return new Response(
                    GSON.toJson(insertOwnerGroups(GSON.fromJson(dataStore.get(WORKSPACE_STORE, id), Workspace.class))));
        }
        return new Response(Util.toJsonString("error", "Invalid Workspace"), HttpURLConnection.HTTP_BAD_REQUEST);
    }

    public static Response getWorkspaceEnvironments(Request request) {
        String workspaceId = request.getParams().get("id");
        if (workspaceId != null && dataStore.contains(WORKSPACE_STORE, workspaceId)) {
            List<String> environmentList = dataStore.getByValue(ENIVRNONMENTS_WORKSPACE_STORE, workspaceId);
            environmentList.forEach(envId -> {
                updateEnvironmentReferences(workspaceId, envId);
            });
            environmentList = dataStore.getByValue(ENIVRNONMENTS_WORKSPACE_STORE, workspaceId);
            return new Response(GSON.toJson(environmentList));
        }
        return new Response(Util.toJsonString("error", "Invalid Workspace"), HttpURLConnection.HTTP_BAD_REQUEST);
    }

    private static Workspace insertOwnerGroups(Workspace workspace) {
        List<String> notFoundGroups = new ArrayList<>();
        dataStore.getMultiKey(WORKSPACES_GROUPS_STORE, workspace.getId()).forEach(groupId -> {
            Group group = getGroup(groupId);
            if (group != null) { // If group service available and found a valid group insert it
                workspace.getOwners().add(group);
            } else {
                notFoundGroups.add(groupId);
            }
        });
        notFoundGroups.forEach(groupId -> {
            // If group service available and group not found then remove invalid group link
            if (!updateGroupReferences(workspace.getId(), groupId)) {
                // If group service is not available return partial group response with id only
                Group idOnlyGroup = new Group();
                idOnlyGroup.setId(groupId);
                idOnlyGroup.setName("");
                idOnlyGroup.setEmail("");
                ;
                workspace.getOwners().add(idOnlyGroup);
            }
        });
        return workspace;
    }

    public static Response linkEnvironment(Request request) {
        String workspaceId = request.getParams().get("id");
        String envId = request.getParams().get("envid");
        if (workspaceId != null && envId != null && dataStore.contains(WORKSPACE_STORE, workspaceId)
                && !serviceAvailableAndIsAnInvalidEnvironment(envId)) {
            dataStore.put(ENIVRNONMENTS_WORKSPACE_STORE, envId, workspaceId); // Many to One
            return new Response(
                    Util.toJsonString("message", "Linked Environment " + envId + " to Workspace " + workspaceId));
        }
        return new Response(Util.toJsonString("error", "Invalid Workspace or Environment"),
                HttpURLConnection.HTTP_BAD_REQUEST);
    }

    public static Response removeGroup(Request request) {
        String workspaceId = request.getParams().get("id");
        String groupId = request.getParams().get("groupid");
        if (removeGroup(workspaceId, groupId)) {
            return new Response(
                    Util.toJsonString("message", "Removed Group " + groupId + " from Workspace " + workspaceId));
        }
        return new Response(Util.toJsonString("error", "Invalid Workspace or Group"),
                HttpURLConnection.HTTP_BAD_REQUEST);
    }

    private static boolean removeGroup(String workspaceId, String groupId) {
        if (workspaceId != null && groupId != null) {
            dataStore.remove(WORKSPACES_GROUPS_STORE, workspaceId, groupId);
            return true;
        }
        return false;
    }

    private static boolean serviceAvailableAndIsAnInvalidEnvironment(String envId) {
        Response response = client.get(environmentServiceURL + "/environment/" + envId);
        return response.getStatus() == HttpURLConnection.HTTP_BAD_REQUEST;
    }

    private static boolean serviceAvailableAndIsAnInvalidGroup(String groupId) {
        Response response = client.get(groupServiceURL + "/group/" + groupId);
        return response.getStatus() == HttpURLConnection.HTTP_BAD_REQUEST;
    }

    private static void setServer(Server server) {
        WorkspaceService.server = server;
    }

    private static void setStore(DataStore dataStore) {
        WorkspaceService.dataStore = dataStore;
    }

    public static Response unlinkEnvironment(Request request) {
        String workspaceId = request.getParams().get("id");
        String environmentId = request.getParams().get("envid");
        if (workspaceId != null && environmentId != null && dataStore.contains(WORKSPACE_STORE, workspaceId)) {
            dataStore.remove(ENIVRNONMENTS_WORKSPACE_STORE, environmentId);
            return new Response(Util.toJsonString("message",
                    "Unlinked Environment " + environmentId + " from Workspace " + workspaceId));
        }
        return new Response(Util.toJsonString("error", "Invalid Workspace or Environment"),
                HttpURLConnection.HTTP_BAD_REQUEST);
    }

    private static boolean unlinkEnvironment(String workspaceId, String envId) {
        if (workspaceId != null && envId != null) {
            dataStore.remove(ENIVRNONMENTS_WORKSPACE_STORE, envId, workspaceId);
            return true;
        }
        return false;
    }

    public static void updateEnvironmentReferences(String workspaceId, String envId) {
        if (serviceAvailableAndIsAnInvalidEnvironment(envId)) {
            unlinkEnvironment(workspaceId, envId);
            logger.info("Unlinking invalid Environmnet {} from Workspsace {}", envId, workspaceId);
        }
    }

    private static boolean updateGroupReferences(String workspaceId, String groupId) {
        if (serviceAvailableAndIsAnInvalidGroup(groupId)) {
            removeGroup(workspaceId, groupId);
            logger.info("Unlinking invalid owner group {} from workspsace {}", groupId, workspaceId);
            return true;
        }
        return false;
    }

    private void configureOtherServices() {
        environmentServiceURL = Configuration.serviceConfig().environmentServiceURL() != null
                ? Configuration.serviceConfig().environmentServiceURL()
                : Configuration.defaultConfig().environmentServiceURL();
        groupServiceURL = Configuration.serviceConfig().groupServiceURL() != null
                ? Configuration.serviceConfig().groupServiceURL()
                : Configuration.defaultConfig().groupServiceURL();
    }

    @Override
    public Service configureUsing(DataStore dataStore) {
        WorkspaceService.setStore(dataStore);
        return this;
    }

    @Override
    public Service configureUsing(Server server) {
        WorkspaceService.setServer(server);
        try {
            server.setRoutes(HttpMethod.GET, "/workspace",
                    WorkspaceService.class.getMethod("getWorkspace", Request.class));
            server.setRoutes(HttpMethod.GET, "/workspace/{id}",
                    WorkspaceService.class.getMethod("getWorkspace", Request.class));
            server.setRoutes(HttpMethod.GET, "/workspace/{id}/environment",
                    WorkspaceService.class.getMethod("getWorkspaceEnvironments", Request.class));
            server.setRoutes(HttpMethod.POST, "/workspace",
                    WorkspaceService.class.getMethod("createWorkspace", Request.class));
            server.setRoutes(HttpMethod.PUT, "/workspace/{id}",
                    WorkspaceService.class.getMethod("editWorkspace", Request.class));
            server.setRoutes(HttpMethod.DELETE, "/workspace/{id}",
                    WorkspaceService.class.getMethod("deleteWorkspace", Request.class));
            server.setRoutes(HttpMethod.POST, "/workspace/{id}/group/{groupid}",
                    WorkspaceService.class.getMethod("addGroup", Request.class));
            server.setRoutes(HttpMethod.DELETE, "/workspace/{id}/group/{groupid}",
                    WorkspaceService.class.getMethod("removeGroup", Request.class));
            server.setRoutes(HttpMethod.POST, "/workspace/{id}/environment/{envid}",
                    WorkspaceService.class.getMethod("linkEnvironment", Request.class));
            server.setRoutes(HttpMethod.DELETE, "/workspace/{id}/environment/{envid}",
                    WorkspaceService.class.getMethod("unlinkEnvironment", Request.class));
            server.setRoutes(HttpMethod.GET, "/component",
                    WorkspaceService.class.getMethod("getComponent", Request.class));
            server.setRoutes(HttpMethod.GET, "/component/{id}",
                    WorkspaceService.class.getMethod("getComponent", Request.class));
        } catch (NoSuchMethodException | SecurityException e) {
            e.printStackTrace(); // TODO implement checked exception
        }
        configureOtherServices();
        return this;
    }

    @Override
    public Service start() {
        if(server == null) {
            throw new RuntimeException("Please configure a server before starting the servoce");
        }
        server.start();
        return this;
    }

    @Override
    public void stop() {
        if(server != null) {
            server.stop();
        }
    }

}
