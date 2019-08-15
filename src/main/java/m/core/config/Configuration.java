package m.core.config;

import java.util.Properties;

public class Configuration {

    private static  Properties properties = new Properties();

    static {
        properties.put("m.core.client.cache.entries.count", "1000");
        properties.put("m.core.client.cache.object.size.bytes", "8192");
        properties.put("m.core.client.connect.timeout.millis", "50000");
        properties.put("m.core.client.socket.timeout.millis", "50000");
    }

    public static int clientCacheEntriesCount() {
        return Integer.parseInt(properties.getProperty("m.core.client.cache.entries.count"));
    }

    public static long clientCacheMaxObjectSizeBytes() {
        return Long.parseLong(properties.getProperty("m.core.client.cache.object.size.bytes"));
    }

    public static int clientConnectTimeoutMillis() {
        return Integer.parseInt(properties.getProperty("m.core.client.connect.timeout.millis"));
    }

    public static int clientSocketTimeoutMillis() {
        return Integer.parseInt(properties.getProperty("m.core.client.socket.timeout.millis"));
    }

    public static Properties getProperties() {
        return Configuration.properties;
    }

    public static void setProperties(Properties properties) {
        Configuration.properties = properties;
    }

    // Don't let anyone else instantiate this class
    private Configuration() {
    }
}
