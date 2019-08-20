package m.core.config;

import java.util.Properties;

/**
 * A simple configuration properties manager.
 */
public class Configuration {

    private static  Properties properties = new Properties();

    static {
        properties.put("m.core.client.cache.entries.count", "1000");
        properties.put("m.core.client.cache.object.size.bytes", "8192");
        properties.put("m.core.client.connect.timeout.millis", "50000");
        properties.put("m.core.client.socket.timeout.millis", "50000");
    }

    /**
     * Returns the maximum entries a client cache can hold.
     *
     * @return the maximum entries a client cache can hold.
     */
    public static int clientCacheEntriesCount() {
        return Integer.parseInt(properties.getProperty("m.core.client.cache.entries.count"));
    }

    /**
     * Returns the maximum sized object in bytes a client cache will accept.
     *
     * @return the maximum sized object in bytes a client cache will accept
     */
    public static long clientCacheMaxObjectSizeBytes() {
        return Long.parseLong(properties.getProperty("m.core.client.cache.object.size.bytes"));
    }

    /**
     * Returns the connection timeout in milliseconds of a client.
     *
     * @return the connection timeout in milliseconds of a client
     */
    public static int clientConnectTimeoutMillis() {
        return Integer.parseInt(properties.getProperty("m.core.client.connect.timeout.millis"));
    }

    /**
     * Returns the socket timeout in milliseconds of a client.
     *
     * @return the socket timeout in milliseconds of a client
     */
    public static int clientSocketTimeoutMillis() {
        return Integer.parseInt(properties.getProperty("m.core.client.socket.timeout.millis"));
    }

    /**
     * Returns all the properties of this configuration.
     *
     * @return all properties
     */
    public static Properties getProperties() {
        return Configuration.properties;
    }

    /**
     * Sets the properties of this configuration.
     *
     * @param properties the properties
     */
    public static void setProperties(Properties properties) {
        Configuration.properties = properties;
    }

    // Don't let anyone else instantiate this class
    private Configuration() {
    }
}
