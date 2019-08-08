package m.core.common;

import java.util.Arrays;
import java.util.List;

import m.core.data.DataStore;
import m.core.server.Server;
import m.core.service.Service;

public class Test {

    protected static final String DEFAULT_SERVER_HOST = "127.0.0.1";
    protected static final long DEFUALT_WAIT_SECONDS = 1;
    protected static final String SERVER_TYPE_SPARK = "SPARK";
    protected static final String SERVER_TYPE_SPRING = "SPRING";
    protected static final String SERVER_TYPE_VERTX = "VERTX";
    protected static final String SERVER_TYPE_PLAY = "PLAY";
    static final List<String> serverTypes = Arrays.asList(SERVER_TYPE_SPRING, SERVER_TYPE_SPARK,
            SERVER_TYPE_VERTX, SERVER_TYPE_PLAY);
    static String serverType = System.getenv("SERVER_TYPE");
    static DataStore dataStore = null;
    static Server server = null;
    protected static volatile Service service = null;

}
