package com.pardhasm.sieve;


import com.google.inject.Guice;
import com.google.inject.Injector;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.logging.Logger;

public class Sieve {
    private Logger logger;
    private ApiConfigLoader configLoader;
    private Server server;

    public void start(String[] args) throws IOException, URISyntaxException {
        loadClasses();
        loadConfig(validatePath(args));
        startServer();
    }

    private String validatePath(String[] args) {
        if (args == null || args.length == 0 || args[0].trim().isEmpty()) {
            throw new IllegalArgumentException("Config path is either null or empty : " + Arrays.toString(args));
        }
        return args[0];
    }

    private void loadClasses() {
        Injector injector = Guice.createInjector(new Binder());
        logger = injector.getInstance(Logger.class);
        configLoader = injector.getInstance(ApiConfigLoader.class);
        server = injector.getInstance(UndertowServerImpl.class);
    }

    private void loadConfig(String path) throws URISyntaxException, IOException {
        logger.info("Loading config from ".concat(path));
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(path));
            char[] buffer = new char[10];
            while (reader.read(buffer) != -1) {
                stringBuilder.append(new String(buffer));
                buffer = new char[10];
            }
        } catch (Exception e) {
            if (reader != null) {
                reader.close();
            }
        }

        String config = stringBuilder.toString();
        configLoader.loadConfig(config);
        logger.info("Config successfully loaded...!");
    }

    private void startServer() {
        server.start();
    }

}
