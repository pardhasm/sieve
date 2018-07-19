package com.pardhasm.sieve;


import com.google.inject.Guice;
import com.google.inject.Injector;

import java.util.logging.Logger;

public class SieveApplication {

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new Binder());
        Logger logger = injector.getInstance(Logger.class);
        logger.info("Starting server");
        Server server = injector.getInstance(Server.class);
        server.start();
        logger.info("Ready to accept requests");
    }

}
