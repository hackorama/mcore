package m.core.demo.service;

/**
 * Nodes have unique id and there are different types of nodes (Environment,
 * Group, Workspace). Nodes can be linked to each other to form hierarchies. A
 * node can only have one parent. A root node will have no parent.
 */
public class Node {

    private String id;

    public Node() {
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
