package com.pardhasm.sieve.core;


import com.google.inject.Guice;
import com.google.inject.Injector;
import com.pardhasm.sieve.core.impl.ApiConfigLoaderImpl;
import com.pardhasm.sieve.core.impl.UndertowServerImpl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.logging.Logger;

public class Sieve {
    private Logger logger;
    private IApiConfigLoader configLoader;
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
        configLoader = injector.getInstance(ApiConfigLoaderImpl.class);
        server = injector.getInstance(UndertowServerImpl.class);
    }

    private void loadConfig(String path) throws URISyntaxException, IOException {
        logger.info("Loading config from ".concat(path));
        StringBuilder stringBuilder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            char[] buffer = new char[10];
            while (reader.read(buffer) != -1) {
                stringBuilder.append(new String(buffer));
                buffer = new char[10];
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
