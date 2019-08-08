package m.core.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.aeonbits.owner.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Configuration implements ServiceConfig {

    private static Logger logger = LoggerFactory.getLogger(Configuration.class);
    private static final long serialVersionUID = 756418884837275691L;
    private static ServiceConfig serviceConfig;
    private static ClientConfig clientConfig;
    private static Configuration instance = new Configuration();
    private static final String HELP_FILE = "help.txt";

    public static ClientConfig clientConfig() {
        if (clientConfig == null) {
            Configuration.init();
        }
        return clientConfig;
    }

    public static Configuration defaultConfig() {
        if (serviceConfig == null || clientConfig == null) {
            Configuration.init();
        }
        return instance;
    }

    public static String getHelpFile() {
        return HELP_FILE;
    }

    public static void init() {
        init(new String[0]);
    }

    public static void init(String[] args) {
        if (args.length > 0) {
            Properties fileProperties = new Properties();
            try (final FileInputStream fileInputStream = new FileInputStream(args[0])) {
                fileProperties.load(fileInputStream);
            } catch (IOException e) {
                logger.error("Error configuring service", e);
                throw new RuntimeException("Error confuguring service", e);
            }
            serviceConfig = ConfigFactory.create(ServiceConfig.class, fileProperties, System.getProperties(),
                    System.getenv());
            clientConfig = ConfigFactory.create(ClientConfig.class, fileProperties, System.getProperties(),
                    System.getenv());
        } else {
            serviceConfig = ConfigFactory.create(ServiceConfig.class, System.getProperties(), System.getenv());
            clientConfig = ConfigFactory.create(ClientConfig.class, System.getProperties(), System.getenv());
        }
    }

    public static ServiceConfig serviceConfig() {
        if (serviceConfig == null) {
            Configuration.init();
        }
        return serviceConfig;
    }

    // Don't let anyone else instantiate this class
    private Configuration() {
    }

    @Override
    public String datastoreLocation() {
        return "mcore.db";
    }

    @Override
    public String environmentServerHost() {
        return "127.0.0.1";
    }

    @Override
    public int environmentServerPort() {
        return 4569;
    }

    @Override
    public String environmentServiceURL() {
        return defaultServiceURL();
    }

    @Override
    public String groupServerHost() {
        return "127.0.0.1";
    }

    @Override
    public int groupServerPort() {
        return 4568;
    }

    @Override
    public String groupServiceURL() {
        return defaultServiceURL();
    }

    @Override
    public String workspaceServerHost() {
        return "127.0.0.1";
    }

    @Override
    public int workspaceServerPort() {
        return 4567;
    }

    private String defaultServiceURL() {
        return "http://" + workspaceServerHost() + ":" + workspaceServerPort();

    }

}
