package m.core.common;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Util {

    private static JsonParser jsonParser = new JsonParser();
    private static final Gson GSON = new Gson();

    // Don't let anyone else instantiate this class
    private Util() {
    }

    public static String toJsonString(String key, String value) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(key, value);
        return jsonObject.toString();
    }

    public static JsonObject toJsonObject(String json) {
        return jsonParser.parse(json).getAsJsonObject();
    }

    public static String toJsonString(String key, Object values) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(key, GSON.toJson(values));
        return jsonObject.toString();
    }

    public static Gson getGson() {
        return GSON;
    }

}
