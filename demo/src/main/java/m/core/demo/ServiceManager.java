package m.core.demo;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import m.core.config.Configuration;
import m.core.data.DataStore;
import m.core.data.MemoryDataStore;
import m.core.data.mapdb.MapdbDataStore;
import m.core.demo.service.environment.EnvironmentService;
import m.core.demo.service.group.GroupService;
import m.core.demo.service.workspace.WorkspaceService;
import m.core.server.Server;
import m.core.server.spark.SparkServer;
import m.core.service.Service;

/**
 * The service manager that builds and starts one or more services based on
 * external configuration
 *
 * @author Kishan Thomas (kishan.thomas@gmail.com)
 *
 */
public class ServiceManager {

    private static Logger logger = LoggerFactory.getLogger(ServiceManager.class);
    private static Service service;
    private static Server server;

    /**
     * Start service manager based on external configuration
     *
     * @param args
     *            The configuration arguments
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void start(String[] args) throws FileNotFoundException, IOException {
        Configuration.init(args);
        if (Configuration.serviceConfig().datastoreLocation() != null) {
            logger.info("Starting services with MapDB data store at {} ...",
                    Configuration.serviceConfig().datastoreLocation());
            start(new MapdbDataStore(Configuration.serviceConfig().datastoreLocation()));
        } else {
            logger.info("Starting services with in memory data store ...");
            start(new MemoryDataStore());
        }
    }

    private static void start(DataStore dataStore) {
        if (Configuration.serviceConfig().environmentServerPort() != 0) {
            logger.info("Starting Environment Service server on 0.0.0.0:{} ...",
                    Configuration.serviceConfig().environmentServerPort());
            ServiceManager.server = new SparkServer("environment",
                    Configuration.serviceConfig().environmentServerPort());
            ServiceManager.service = new EnvironmentService().configureUsing(server).configureUsing(dataStore);
        } else if (Configuration.serviceConfig().groupServerPort() != 0) {
            logger.info("Starting Group Service server on 0.0.0.0:{} ...",
                    Configuration.serviceConfig().groupServerPort());
            ServiceManager.server = new SparkServer("group", Configuration.serviceConfig().groupServerPort());
            ServiceManager.service = new GroupService().configureUsing(server).configureUsing(dataStore);
        } else {
            int port = Configuration.serviceConfig().workspaceServerPort() != 0
                    ? Configuration.serviceConfig().workspaceServerPort()
                    : Configuration.defaultConfig().workspaceServerPort();
            logger.info("Starting Workspace Service server on 0.0.0.0:{} ...", port);
            ServiceManager.server = new SparkServer("workspace", port);
            ServiceManager.service = new WorkspaceService().configureUsing(server).configureUsing(dataStore);
            if (Configuration.serviceConfig().groupServiceURL() == null) {
                logger.warn(
                        "No service url configured for Group Service, so starting a group service on the same server as Workspace Service ...");
                ServiceManager.service.attach(new GroupService().configureUsing(dataStore));
            } else {
                logger.info("Using external Group Service at {}", Configuration.serviceConfig().groupServiceURL());
            }
            if (Configuration.serviceConfig().environmentServiceURL() == null) {
                logger.warn(
                        "No service url configured for Environment Service, so starting an Environment Service on the same server as Workspace Service ...");
                ServiceManager.service.attach(new EnvironmentService().configureUsing(dataStore));
            } else {
                logger.info("Using external Environment Service at {}",
                        Configuration.serviceConfig().environmentServiceURL());
            }
        }
        ServiceManager.service.start();
    }

    /**
     * Start the Service Manager with default configuration options
     *
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void start() throws FileNotFoundException, IOException {
        start(new String[0]);
    }

    /**
     * Stop the Service Manager
     */
    public static void stop() {
        ServiceManager.service.stop();
    }

}
