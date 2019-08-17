package m.core.http;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

/**
 * An HTTP session.
 * <p>
 * A session is valid by default unless invalidated using
 * {@link Session#invalidate()}.
 */
public class Session {

    private Map<String, Object> attributes = new HashMap<>();
    private String id;
    private long lastAccessedTime;
    private long maxInactiveInterval;
    private boolean valid = true; // Valid by default until invalidated

    /**
     * Returns an attribute value.
     *
     * @param name the attribute name
     * @return the attribute value object, or null.
     */
    public Object getAttribute(String name) {
        Optional<Entry<String, Object>> optional = attributes.entrySet().stream().filter(e -> e.getKey().equals(name))
                .findAny();
        return optional.isPresent() ? optional.get().getValue() : null;
    }

    /**
     * Returns the unique identifier of this session.
     *
     * @return the unique identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the last client request time of this session as milliseconds since
     * epoch.
     *
     * @return the last accessed time in milliseconds since epoch
     */
    public long getLastAccessedTime() {
        return lastAccessedTime;
    }

    /**
     * Returns the maximum time in seconds the server will keep this session active.
     * <p>
     * If there are no client access within this interval, then the session will be
     * invalidated by the server.
     *
     * @return the maximum interval time in seconds
     */
    public long getMaxInactiveInterval() {
        return maxInactiveInterval;
    }

    /**
     * Checks if this session is invalid.
     *
     * @return true if invalid, false otherwise
     */
    public boolean invalid() {
        return valid == false;
    }

    /**
     * Invalidates this session.
     * <p>
     * Session is valid by default, and once invalidated once cannot be validated
     * again.
     */
    public void invalidate() {
        valid = false; // Once invalidated cannot be activated
    }

    /**
     * Removes the specified attribute from this session.
     *
     * @param name the name of the attribute to remove
     */
    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    /**
     * Specifies an attribute of this session.
     *
     * @param name  the name of the attribute to set
     * @param value the value of the attribute to set
     * @return this session
     */
    public Session setAttribute(String name, Object value) {
        attributes.put(name, value);
        return this;
    }

    /**
     * Specifies the unique identifier of this session.
     *
     * @param id the unique identifier
     * @return this session
     */
    public Session setId(String id) {
        this.id = id;
        return this;
    }

    /**
     * Specifies the last client request time of this session as milliseconds since
     * epoch.
     *
     * @param lastAccessedTime the last accessed time in milliseconds since epoch
     * @return this session
     */
    public Session setLastAccessedTime(long lastAccessedTime) {
        this.lastAccessedTime = lastAccessedTime;
        return this;
    }

    /**
     * Specifies the maximum time in seconds the server will keep this session
     * active.
     * <p>
     * If there are no client access within this interval, then the session will be
     * invalidated by the server.
     *
     * @param maxInactiveInterval the maximum interval time in seconds
     * @return this session
     */
    public Session setMaxInactiveInterval(long maxInactiveInterval) {
        this.maxInactiveInterval = maxInactiveInterval;
        return this;
    }

    /**
     * Checks if the session is valid.
     *
     * @return true if valid, false otherwise
     */
    public boolean valid() {
        return valid == true;
    }

    /**
     * Returns all the attributes of this session.
     *
     * @return the attributes as a {@code Map}
     */
    public Map<String, Object> getAttributes() {
        return attributes;
    }
}
