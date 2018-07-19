package com.pardhasm.sieve;

import com.google.inject.Inject;
import io.undertow.Undertow;

import java.util.List;
import java.util.logging.Logger;

public class Server {

    @Inject
    private Logger logger;
    @Inject
    private IRouter router;

    public void start() {
        Undertow undertow = Undertow.builder().addHttpListener(9090, "localhost")
                .setHandler(exchange -> router.handle(exchange)).build();
        undertow.start();
        logger.info("Starting server");
        if (!undertow.getXnio().getName().isEmpty()) {
            logger.info("Server started");
            logger.info("Fetching listeners");
            printMetrics(undertow);
            logger.info("Ready to accept requests");
        }
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
