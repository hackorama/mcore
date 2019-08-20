package m.core.common;

import com.google.gson.JsonObject;

/**
 * Common helper utilities.
 */
public class Util {

    // Don't let anyone else instantiate this class
    private Util() {
    }

    /**
     * Returns a JSON formatted string of the specified key value pair.
     *
     * @param key   the key name
     * @param value the value
     * @return the JSON string
     */
    public static String toJsonString(String key, String value) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(key, value);
        return jsonObject.toString();
    }

}
