package com.hackorama.mcore.common;

import java.util.Arrays;
import java.util.List;

import com.hackorama.mcore.data.DataStore;
import com.hackorama.mcore.server.Server;
import com.hackorama.mcore.service.Service;

public class Test {

    protected static final String DEFAULT_SERVER_HOST = "127.0.0.1";
    protected static final long DEFUALT_WAIT_SECONDS = 1;
    protected static final String SERVER_TYPE_SPARK = "SPARK";
    protected static final String SERVER_TYPE_SPRING = "SPRING";
    protected static final String SERVER_TYPE_VERTX = "VERTX";
    static final List<String> serverTypes = Arrays.asList(SERVER_TYPE_SPRING, SERVER_TYPE_SPARK,
            SERVER_TYPE_VERTX);
    static String serverType = System.getenv("SERVER_TYPE");
    static DataStore dataStore = null;
    static Server server = null;
    protected static volatile Service service = null;

}
