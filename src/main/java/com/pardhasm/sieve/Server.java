package com.pardhasm.sieve;

import com.google.inject.Inject;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

import java.util.List;
import java.util.logging.Logger;

public class Server {

    @Inject
    Logger logger;
    @Inject
    private IRouter router;

    public boolean start() {
        boolean success = false;
        Undertow undertow = Undertow.builder().addHttpListener(9090, "localhost")
                .setHandler(new HttpHandler() {
                    @Override
                    public void handleRequest(HttpServerExchange exchange) throws Exception {
                        router.handle(exchange);
                    }
                }).build();
        undertow.start();
        logger.info("Starting server");
        if (!undertow.getXnio().getName().isEmpty()) {
            success = true;
            logger.info("Server started");
            logger.info("Fetching listeners");
            printMetrics(undertow);
        }
        return success;
    }

    private void printMetrics(Undertow undertow) {
        List<Undertow.ListenerInfo> listenerInfo = undertow.getListenerInfo();
        for (int i = 0; i < listenerInfo.size(); i++) {
            Undertow.ListenerInfo p = listenerInfo.get(i);
            logger.info("Listener : " + (i + 1) + Constants.SEPERATOR
                    + "Address : " + p.getAddress().toString().replace('/', ' ').trim() + Constants.SEPERATOR
                    + "Protocol : " + p.getProtcol());
        }
    }
}
