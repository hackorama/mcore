package com.hackorama.mcore.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

public class Session {

    private Map<String, Object> attributes = new HashMap<>();
    private String id;
    private long lastAccessedTime;
    private long maxInactiveInterval;
    private boolean valid = true; // Valid by default until invalidated

    public Object getAttribute(String name) {
        Optional<Entry<String, Object>> optional = attributes.entrySet().stream().filter(e -> e.getKey().equals(name))
                .findAny();
        return optional.isPresent() ? optional.get().getValue() : null;
    }

    public String getId() {
        return id;
    }

    public long getLastAccessedTime() {
        return lastAccessedTime;
    }

    public long getMaxInactiveInterval() {
        return maxInactiveInterval;
    }

    public boolean invalid() {
        return valid == false;
    }

    public void invalidate() {
        valid = false; // Once invalidated cannot be activated
    }

    public void removeAttribute(String name) {
        attributes.remove(name);
        //attributes.entrySet().removeIf(e -> e.getKey().equals(name));
    }

    public Session setAttribute(String name, Object value) {
        attributes.put(name, value);
        return this;
    }

    public Session setId(String id) {
        this.id = id;
        return this;
    }

    public Session setLastAccessedTime(long lastAccessedTime) {
        this.lastAccessedTime = lastAccessedTime;
        return this;
    }

    public Session setMaxInactiveInterval(long maxInactiveInterval) {
        this.maxInactiveInterval = maxInactiveInterval;
        return this;
    }

    public boolean valid() {
        return valid == true;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }
}
