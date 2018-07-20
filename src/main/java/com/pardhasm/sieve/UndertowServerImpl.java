package com.pardhasm.sieve;

import com.google.inject.Inject;
import io.undertow.Undertow;

import java.util.List;
import java.util.logging.Logger;

public class UndertowServerImpl implements Server {

    private Logger logger;
    private IRouter router;

    @Inject
    public UndertowServerImpl(IRouter router, Logger logger) {
        this.router = router;
        this.logger = logger;
    }

    @Override
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
            logger.info(new StringBuilder().append("Listener : ")
                    .append(i + 1).append(Constants.SEPERATOR).append("Address : ")
                    .append(p.getAddress().toString().replace('/', ' ').trim())
                    .append(Constants.SEPERATOR).append("Protocol : ").append(p.getProtcol()).toString());
        }
    }
}
