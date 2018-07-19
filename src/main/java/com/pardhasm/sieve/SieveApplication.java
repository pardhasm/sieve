package com.pardhasm.sieve;


import com.google.inject.Guice;
import com.google.inject.Injector;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.logging.Logger;

public class SieveApplication {
    private static Logger logger;
    private static ApiConfigLoader configLoader;
    private static Server server;

    public static void main(String[] args) throws IOException, URISyntaxException {
        loadClasses();
        loadConfig(validatePath(args));
        startServer();
    }

    private static String validatePath(String[] args) {
        if (args == null || args.length == 0 || args[0].trim().isEmpty()) {
            throw new IllegalArgumentException("Config path is either null or empty : " + Arrays.toString(args));
        }
        return args[0];
    }

    private static void loadClasses() {
        Injector injector = Guice.createInjector(new Binder());
        logger = injector.getInstance(Logger.class);
        configLoader = injector.getInstance(ApiConfigLoader.class);
        server = injector.getInstance(Server.class);
    }

    private static void loadConfig(String path) throws IOException, URISyntaxException {
        logger.info("Loading config from " + path);
        BufferedReader reader = new BufferedReader(new FileReader(path));
        StringBuilder stringBuilder = new StringBuilder();
        char[] buffer = new char[10];
        while (reader.read(buffer) != -1) {
            stringBuilder.append(new String(buffer));
            buffer = new char[10];
        }
        reader.close();
        String config = stringBuilder.toString();
        configLoader.loadConfig(config);
        logger.info("Config successfully loaded...!");
    }

    private static void startServer() {
        server.start();
    }

}
