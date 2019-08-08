package m.core.service;

public class Entity {

    private String id;

    public Entity() {
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
