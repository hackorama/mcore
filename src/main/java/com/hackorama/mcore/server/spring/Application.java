package com.hackorama.mcore.server.spring;

import java.util.Collections;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

// Disable auto configuration attempt (and failure exception logs) when Mongo driver is in path
@SpringBootApplication(exclude = { MongoAutoConfiguration.class, MongoDataAutoConfiguration.class })
public class Application {

    private static ConfigurableApplicationContext context;

    public static void start(int port) {
        String[] args = {};
        SpringApplication app = new SpringApplication(Application.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.setDefaultProperties(Collections.singletonMap("server.port", port));
        context = app.run(args);
    }

    public static void stop() {
        if (context != null) {
            context.close();
        }
    }

}
