package m.core.demo.service.workspace;


import java.util.ArrayList;
import java.util.List;

import m.core.demo.service.Node;
import m.core.demo.service.group.Group;

public class Workspace extends Node {

    private String name;
    private List<Group> owners = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Group> getOwners() {
        return owners;
    }

    public void setOwners(List<Group> owners) {
        this.owners = owners;
    }

}
