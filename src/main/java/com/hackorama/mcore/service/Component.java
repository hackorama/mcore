package com.hackorama.mcore.service;

/**
 * A component is an entity with type and ID. Components can be linked to each
 * other to form hierarchies. A component can only have one parent. A root
 * component will have no parent.
 */
public class Component {

    private String id;

    public Component() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setId() {
        id = java.util.UUID.randomUUID().toString();
    }
}
