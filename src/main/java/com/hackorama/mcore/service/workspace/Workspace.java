package com.hackorama.mcore.service.workspace;


import java.util.ArrayList;
import java.util.List;

import com.hackorama.mcore.service.Component;
import com.hackorama.mcore.service.group.Group;

public class Workspace extends Component {

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
