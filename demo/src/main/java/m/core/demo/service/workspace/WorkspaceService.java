package m.core.demo.service.workspace;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import m.core.client.Client;
import m.core.client.unirest.CachingUnirestClient;
import m.core.demo.common.Util;
import m.core.demo.config.Configuration;
import m.core.demo.service.group.Group;
import m.core.http.Request;
import m.core.http.Response;
import m.core.service.Service;

public class WorkspaceService extends Service {

    private static final Logger logger = LoggerFactory.getLogger(WorkspaceService.class);

    private final String WORKSPACE_STORE = "WORKSPACE";
    private final String WORKSPACES_GROUPS_STORE = "WORKSPACES_GROUPS";
    private final String ENIVRNONMENTS_WORKSPACE_STORE = "ENVIRONMENTS_WORKSPACE";
    private final Gson GSON = new Gson();
    private Client client = new CachingUnirestClient();


    private String environmentServiceURL;
    private String groupServiceURL;

    public Response addGroup(Request request) {
        String workspaceId = request.getParam("id");
        String groupId = request.getParam("groupid");
        if (workspaceId != null && groupId != null && dataStore.contains(WORKSPACE_STORE, workspaceId)
                && !serviceAvailableAndIsAnInvalidGroup(groupId)) {
            dataStore.putMulti(WORKSPACES_GROUPS_STORE, workspaceId, groupId); // Many to Many
            return new Response(
                    Util.toJsonString("message", "Added Group " + groupId + " to Workspace " + workspaceId));
        }
        return new Response(Util.toJsonString("error", "Invalid Workspace or Group"),
                HttpURLConnection.HTTP_BAD_REQUEST);
    }

    public Response createWorkspace(Request request) {
        return editWorkspace(request);
    }

    public Response deleteWorkspace(Request request) {
        dataStore.remove(WORKSPACE_STORE, request.getParam("id"));
        return new Response("");
    }

    public Response editWorkspace(Request request) {
        Gson gson = GSON;
        Workspace workspace = gson.fromJson(request.getBody(), Workspace.class);
        String id = request.getParam("id");
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

    public Response getComponent(Request request) {
        String id = request.getParam("id");
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

    private Group getGroup(String groupId) {
        Response response = client.get(groupServiceURL + "/group/" + groupId);
        if (response.getStatus() == HttpURLConnection.HTTP_OK) {
            return GSON.fromJson(response.getBody(), Group.class);
        }
        return null;
    }

    public Response getWorkspace(Request request) {
        String id = request.getParam("id");
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

    public Response getWorkspaceEnvironments(Request request) {
        String workspaceId = request.getParam("id");
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

    private Workspace insertOwnerGroups(Workspace workspace) {
        List<String> notFoundGroups = new ArrayList<>();
        dataStore.getMulti(WORKSPACES_GROUPS_STORE, workspace.getId()).forEach(groupId -> {
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

    public Response linkEnvironment(Request request) {
        String workspaceId = request.getParam("id");
        String envId = request.getParam("envid");
        if (workspaceId != null && envId != null && dataStore.contains(WORKSPACE_STORE, workspaceId)
                && !serviceAvailableAndIsAnInvalidEnvironment(envId)) {
            dataStore.put(ENIVRNONMENTS_WORKSPACE_STORE, envId, workspaceId); // Many to One
            return new Response(
                    Util.toJsonString("message", "Linked Environment " + envId + " to Workspace " + workspaceId));
        }
        return new Response(Util.toJsonString("error", "Invalid Workspace or Environment"),
                HttpURLConnection.HTTP_BAD_REQUEST);
    }

    public Response removeGroup(Request request) {
        String workspaceId = request.getParam("id");
        String groupId = request.getParam("groupid");
        if (removeGroup(workspaceId, groupId)) {
            return new Response(
                    Util.toJsonString("message", "Removed Group " + groupId + " from Workspace " + workspaceId));
        }
        return new Response(Util.toJsonString("error", "Invalid Workspace or Group"),
                HttpURLConnection.HTTP_BAD_REQUEST);
    }

    private boolean removeGroup(String workspaceId, String groupId) {
        if (workspaceId != null && groupId != null) {
            dataStore.remove(WORKSPACES_GROUPS_STORE, workspaceId, groupId);
            return true;
        }
        return false;
    }

    private boolean serviceAvailableAndIsAnInvalidEnvironment(String envId) {
        Response response = client.get(environmentServiceURL + "/environment/" + envId);
        return response.getStatus() == HttpURLConnection.HTTP_BAD_REQUEST;
    }

    private boolean serviceAvailableAndIsAnInvalidGroup(String groupId) {
        Response response = client.get(groupServiceURL + "/group/" + groupId);
        return response.getStatus() == HttpURLConnection.HTTP_BAD_REQUEST;
    }

    public Response unlinkEnvironment(Request request) {
        String workspaceId = request.getParam("id");
        String environmentId = request.getParam("envid");
        if (workspaceId != null && environmentId != null && dataStore.contains(WORKSPACE_STORE, workspaceId)) {
            dataStore.remove(ENIVRNONMENTS_WORKSPACE_STORE, environmentId);
            return new Response(Util.toJsonString("message",
                    "Unlinked Environment " + environmentId + " from Workspace " + workspaceId));
        }
        return new Response(Util.toJsonString("error", "Invalid Workspace or Environment"),
                HttpURLConnection.HTTP_BAD_REQUEST);
    }

    private boolean unlinkEnvironment(String workspaceId, String envId) {
        if (workspaceId != null && envId != null) {
            dataStore.remove(ENIVRNONMENTS_WORKSPACE_STORE, envId, workspaceId);
            return true;
        }
        return false;
    }

    public void updateEnvironmentReferences(String workspaceId, String envId) {
        if (serviceAvailableAndIsAnInvalidEnvironment(envId)) {
            unlinkEnvironment(workspaceId, envId);
            logger.info("Unlinking invalid Environmnet {} from Workspsace {}", envId, workspaceId);
        }
    }

    private boolean updateGroupReferences(String workspaceId, String groupId) {
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
    public void configure() {
        GET("/workspace", this::getWorkspace);
        GET("/workspace/{id}", this::getWorkspace);
        GET("/workspace/{id}/environment", this::getWorkspaceEnvironments);
        POST("/workspace", this::createWorkspace);
        PUT("/workspace/{id}", this::editWorkspace);
        DELETE("/workspace/{id}", this::deleteWorkspace);
        POST("/workspace/{id}/group/{groupid}", this::addGroup);
        DELETE("/workspace/{id}/group/{groupid}", this::removeGroup);
        POST("/workspace/{id}/environment/{envid}", this::linkEnvironment);
        DELETE("/workspace/{id}/environment/{envid}", this::unlinkEnvironment);
        GET("/component", this::getComponent);
        GET("/component/{id}", this::getComponent);
        configureOtherServices();
    }

}
