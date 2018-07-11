package com.pardhasm.sieve;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

public enum Server {
    INSTANCE;

    private Undertow undertow = null;


    private void init() {
        if (undertow == null) {
            undertow = Undertow.builder().addHttpListener(9090, "localhost")
                    .setHandler(new HttpHandler() {
                        @Override
                        public void handleRequest(HttpServerExchange exchange) throws Exception {
                            Router.INSTANCE.handle(exchange);
                        }
                    }).build();

        }
    }

    public synchronized void start() {
        if (undertow == null) {
            init();
        }
        undertow.start();
    }
}
