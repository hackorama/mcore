package m.core.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.aeonbits.owner.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Configuration {

    private static ClientConfig clientConfig;
    private static Configuration instance = new Configuration();
    private static Logger logger = LoggerFactory.getLogger(Configuration.class);

    public static ClientConfig clientConfig() {
        if (clientConfig == null) {
            Configuration.init();
        }
        return clientConfig;
    }

    public static Configuration defaultConfig() {
        if (clientConfig == null) {
            Configuration.init();
        }
        return instance;
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
            clientConfig = ConfigFactory.create(ClientConfig.class, fileProperties, System.getProperties(),
                    System.getenv());
        } else {
            clientConfig = ConfigFactory.create(ClientConfig.class, System.getProperties(), System.getenv());
        }
    }

    // Don't let anyone else instantiate this class
    private Configuration() {
    }

}
