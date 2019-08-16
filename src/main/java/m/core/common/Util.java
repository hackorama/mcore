package m.core.common;

import com.google.gson.JsonObject;

public class Util {

    // Don't let anyone else instantiate this class
    private Util() {
    }

    public static String toJsonString(String key, String value) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(key, value);
        return jsonObject.toString();
    }

}
