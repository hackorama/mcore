package com.hackorama.mcore;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hackorama.mcore.config.Configuration;
import com.hackorama.mcore.data.DataStore;
import com.hackorama.mcore.data.MemoryDataStore;
import com.hackorama.mcore.data.mapdb.MapdbDataStore;
import com.hackorama.mcore.server.Server;
import com.hackorama.mcore.server.spark.SparkServer;
import com.hackorama.mcore.service.Service;
import com.hackorama.mcore.service.environment.EnvironmentService;
import com.hackorama.mcore.service.group.GroupService;
import com.hackorama.mcore.service.workspace.WorkspaceService;

/**
 * The service manager that builds and starts one or more services based on
 * external configuration
 *
 * @author KITHO
 *
 */
public class ServiceManager {

    private static Logger logger = LoggerFactory.getLogger(ServiceManager.class);
    private static List<Service> services = new ArrayList<>();
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
            ServiceManager.services.add(new EnvironmentService().configureUsing(server).configureUsing(dataStore));
        } else if (Configuration.serviceConfig().groupServerPort() != 0) {
            logger.info("Starting Group Service server on 0.0.0.0:{} ...",
                    Configuration.serviceConfig().groupServerPort());
            ServiceManager.server = new SparkServer("group", Configuration.serviceConfig().groupServerPort());
            ServiceManager.services.add(new GroupService().configureUsing(server).configureUsing(dataStore));
        } else {
            int port = Configuration.serviceConfig().workspaceServerPort() != 0
                    ? Configuration.serviceConfig().workspaceServerPort()
                    : Configuration.defaultConfig().workspaceServerPort();
            logger.info("Starting Workspace Service server on 0.0.0.0:{} ...", port);
            ServiceManager.server = new SparkServer("workspace", port);
            ServiceManager.services.add(new WorkspaceService().configureUsing(server).configureUsing(dataStore));
            if (Configuration.serviceConfig().groupServiceURL() == null) {
                logger.warn(
                        "No service url configured for Group Service, so starting a group service on the same server as Workspace Service ...");
                ServiceManager.services.add(new GroupService().configureUsing(server).configureUsing(dataStore));
            } else {
                logger.info("Using external Group Service at {}", Configuration.serviceConfig().groupServiceURL());
            }
            if (Configuration.serviceConfig().environmentServiceURL() == null) {
                logger.warn(
                        "No service url configured for Environment Service, so starting an Environment Service on the same server as Workspace Service ...");
                ServiceManager.services.add(new EnvironmentService().configureUsing(server).configureUsing(dataStore));
            } else {
                logger.info("Using external Environment Service at {}",
                        Configuration.serviceConfig().environmentServiceURL());
            }
        }
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
        services.stream().forEach(Service::stop);
        if (server != null) {
            server.stop();
        }
    }

}
